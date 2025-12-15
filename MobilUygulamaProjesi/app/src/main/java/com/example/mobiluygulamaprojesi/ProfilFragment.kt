package com.example.mobiluygulamaprojesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mobiluygulamaprojesi.databinding.FragmentHesabimBinding
import com.example.mobiluygulamaprojesi.databinding.FragmentProfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ProfilFragment : Fragment() {

    private lateinit var design: FragmentProfilBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design = FragmentProfilBinding.inflate(layoutInflater,container,false)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            design.textProfilEposta.text= currentUser.email
            db.collection("Kullanicilar").document(currentUser.uid).get()
                .addOnSuccessListener { belge ->
                    if(belge.exists()){
                        design.textProfilIsim.text = belge.getString("adSoyad") ?: "İsim soyisim girilmedi"
                        design.textProfilTelefon.text=belge.getString("telefon") ?: "Telefon girilmedi"
                        design.textProfilAdres.text=belge.getString("adres") ?: "Adres girilmedi"
                    }
                    else{
                        design.textProfilIsim.text = "-"
                        design.textProfilTelefon.text= "-"
                        design.textProfilAdres.text= "Henüz adres bilgisi girilmedi"
                    }
                }
        }
        return design.root
    }


}