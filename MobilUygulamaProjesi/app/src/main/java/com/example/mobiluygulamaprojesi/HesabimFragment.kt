package com.example.mobiluygulamaprojesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.mobiluygulamaprojesi.databinding.FragmentHesabimBinding
import com.google.firebase.auth.FirebaseAuth


class HesabimFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var design: FragmentHesabimBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design = FragmentHesabimBinding.inflate(layoutInflater,container,false)

        design.butonHesabimBilgiler.setOnClickListener { profilBilgileri ->
            Navigation.findNavController(profilBilgileri).navigate(R.id.action_hesabim_to_profil)
        }
        design.butonHesabimDegistir.setOnClickListener { profilDegistir ->
            Navigation.findNavController(profilDegistir).navigate(R.id.action_hesabim_to_profilDegistir)
        }
        design.butonSifremiDegistir.setOnClickListener { sifreDegistir ->
            Navigation.findNavController(sifreDegistir).navigate(R.id.action_hesabim_to_sifreGirme)
        }
        design.butonHesabimPasif.setOnClickListener { pasifYap ->
            val currentUser = auth.currentUser
            if(currentUser!=null){
                val yapici = androidx.appcompat.app.AlertDialog
                    .Builder(requireContext())
                yapici.setTitle("Hesabı Dondur")
                yapici.setMessage("Hesabınızı geçici olarak dondurmak istediğinize emin misiniz?")

                yapici.setPositiveButton("Evet"){ dialogInterface, i ->
                    val kullaniciRef = com.google.firebase.firestore
                        .FirebaseFirestore.getInstance().collection("Kullanicilar")
                        .document(currentUser.uid)

                    kullaniciRef.update("aktifMi",false)
                        .addOnSuccessListener {
                            auth.signOut()
                            Toast.makeText(requireContext(),"Hesabınız geçici olarak donduruldu",Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(pasifYap).navigate(R.id.action_hesabim_to_giris)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
                        }
                }
                yapici.setNegativeButton("Hayır"){ dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                yapici.show()
            }
        }
        design.butonHesabimHesaptanCik.setOnClickListener { cikisYap ->
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Çıkış Yapıldı", Toast.LENGTH_SHORT).show()
            Navigation.findNavController(cikisYap).navigate(R.id.action_hesabim_to_giris)
        }

        return design.root
    }


}