package com.example.mobiluygulamaprojesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.mobiluygulamaprojesi.databinding.FragmentSifreGirmeBinding
import com.google.firebase.auth.FirebaseAuth


class SifreGirmeFragment : Fragment() {
    private lateinit var design: FragmentSifreGirmeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design= FragmentSifreGirmeBinding.inflate(layoutInflater,container,false)

        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$"
        val passwordMatcher = Regex(passwordPattern)

        design.butonSifreGirme.setOnClickListener { sifreGirme ->
            val yeniSifre = design.editTextSifreGirmeSifre.text.toString()
            val currentUser = auth.currentUser

            if(currentUser != null) {
                if(passwordMatcher.matches(yeniSifre)) {
                    currentUser.updatePassword(yeniSifre)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(),"Şifre değiştirildi",Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(sifreGirme).popBackStack()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireContext(),"Hata: ${exception.localizedMessage}",Toast.LENGTH_SHORT).show()
                        }
                }
                else {
                    Toast.makeText(requireContext(),"Şifre kurallara uygun değil",Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(requireContext(),"Lütfen giriş yapınız",Toast.LENGTH_SHORT).show()
                Navigation.findNavController(sifreGirme).navigate(R.id.action_sifreGirme_to_giris)
            }
        }

        return design.root
    }


}