package com.example.mobiluygulamaprojesi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.mobiluygulamaprojesi.databinding.FragmentGirisBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class GirisFragment : Fragment() {
    private lateinit var design: FragmentGirisBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design = FragmentGirisBinding.inflate(layoutInflater,container,false)

        design.butonGirisKayit.setOnClickListener { kayitOlma ->
            Navigation.findNavController(kayitOlma).navigate(R.id.action_giris_to_kayit)
        }
        design.butonGirisSifreSifirlama.setOnClickListener { sifreSifirlama ->
            Navigation.findNavController(sifreSifirlama).navigate(R.id.action_giris_to_sifreSifirlama)
        }
        design.butonGirisGirisYap.setOnClickListener { girisYapma ->
            val girilenEposta = design.editTextGirisEposta.text.toString()
            val girilenSifre = design.editTextGirisSifre.text.toString()

            if (girilenEposta.isEmpty() || girilenSifre.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Lütfen e-posta ve şifre giriniz",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                auth.signInWithEmailAndPassword(girilenEposta, girilenSifre)
                    .addOnSuccessListener {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            val db = FirebaseFirestore.getInstance()
                            db.collection("Kullanicilar").document(currentUser.uid)
                                .update("aktifMi", true)
                                .addOnCompleteListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Giriş Yapıldı",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    adminMi(girisYapma,currentUser.uid)
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                                Toast.makeText(requireContext(), "Hata: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
            }
        }
        return design.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = auth.currentUser
        if(currentUser != null)
            adminMi(view,currentUser.uid)

    }

    private fun adminMi(view: View, uid: String) {
        db.collection("Kullanicilar").document(uid).get()
            .addOnSuccessListener { belge ->
                if(belge.exists()){
                    val adminMi=belge.getBoolean("adminMi") ?: false
                    if(adminMi){
                        Toast.makeText(requireContext(),"Admin Girişi", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), AdminActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    else{
                        try {
                            Navigation.findNavController(view).navigate(R.id.action_giris_to_anasayfa)
                        } catch (e: Exception) {
                            Log.e("NavigationError", "Hata: ${e.message}")
                        }
                    }
                }
                else{
                    Navigation.findNavController(view).navigate(R.id.action_giris_to_anasayfa)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Hata: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }
}