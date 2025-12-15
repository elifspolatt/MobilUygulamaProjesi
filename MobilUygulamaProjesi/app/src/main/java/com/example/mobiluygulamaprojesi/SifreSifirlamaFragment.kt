package com.example.mobiluygulamaprojesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.mobiluygulamaprojesi.databinding.FragmentSifreSifirlamaBinding
import com.google.firebase.firestore.FirebaseFirestore


class SifreSifirlamaFragment : Fragment() {
    private lateinit var design: FragmentSifreSifirlamaBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design= FragmentSifreSifirlamaBinding.inflate(layoutInflater,container,false)

        design.butonSifreSifirlama.setOnClickListener { sifreSifirlama ->
            val eposta = design.editTextSifreSifirlamaEposta.text.toString().trim()

            if(eposta.isNotEmpty()) {
                db.collection("Kullanicilar").whereEqualTo("eposta", eposta).get()
                    .addOnSuccessListener { belgeler ->
                        if(!belgeler.isEmpty) {
                            val action = SifreSifirlamaFragmentDirections.actionSifreSifirlamaToSifreGirme()
                            Navigation.findNavController(sifreSifirlama)
                                .navigate(R.id.action_sifreSifirlama_to_sifreGirme)
                            Toast.makeText(requireContext(),"Kullanıcı doğrulandı.", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(requireContext(),"Kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(),"Hata: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
            }
            else {
                Toast.makeText(requireContext(),"Lütfen e-posta adresinizi giriniz.", Toast.LENGTH_SHORT).show()
            }
        }

        return design.root
    }


}