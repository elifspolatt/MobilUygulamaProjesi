package com.example.mobiluygulamaprojesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.mobiluygulamaprojesi.databinding.FragmentProfilDegistirBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ProfilDegistirFragment : Fragment() {

    private lateinit var design: FragmentProfilDegistirBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        design = FragmentProfilDegistirBinding.inflate(layoutInflater,container,false)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("Kullanicilar").document(currentUser.uid).get()
                .addOnSuccessListener { belge ->
                    if(belge.exists()){
                        design.editTextDuzenleAd.setText(belge.getString("adSoyad"))
                        design.editTextDuzenleTelefon.setText(belge.getString("telefon"))
                        design.editTextDuzenleAdres.setText(belge.getString("adres"))
                    }
                }
        }
        design.butonDegisiklikleriKaydet.setOnClickListener { kaydet ->
            val adSoyad = design.editTextDuzenleAd.text.toString().trim()
            val telefon = design.editTextDuzenleTelefon.text.toString().trim()
            val adres = design.editTextDuzenleAdres.text.toString().trim()

            val kullanici = auth.currentUser
            if(kullanici != null) {
                val kullaniciVerileri = hashMapOf(
                    "adSoyad" to adSoyad,
                    "telefon" to telefon,
                    "adres" to adres,
                    "eposta" to kullanici.email
                )

                db.collection("Kullanicilar").document(kullanici.uid)
                    .set(kullaniciVerileri, SetOptions.merge()).addOnSuccessListener {
                        Toast.makeText(requireContext(),"Profil güncellendi",Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(kaydet).popBackStack()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(),"Hata: ${exception.localizedMessage}",Toast.LENGTH_SHORT).show()
                    }
            }
            else
            {
                Toast.makeText(requireContext(),"Tekrar giriş yapınız",Toast.LENGTH_SHORT).show()
            }
        }
        return design.root
    }

}