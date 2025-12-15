package com.example.mobiluygulamaprojesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.mobiluygulamaprojesi.databinding.FragmentKayitBinding
import com.google.firebase.auth.FirebaseAuth


class KayitFragment : Fragment() {

    private lateinit var design: FragmentKayitBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        design = FragmentKayitBinding.inflate(layoutInflater, container, false)

        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?])(?=\\S+$).{6,}$"
        val passwordMatcher = Regex(passwordPattern)

        design.butonKayitKayitOl.setOnClickListener { kayitOl ->
            val adSoyad = design.editTextKayitAdSoyad.text.toString().trim()
            val eposta = design.editTextKayitEposta.text.toString().trim()
            val sifre = design.editTextKayitSifre.text.toString().trim()

            if (adSoyad.isEmpty() || eposta.isEmpty() || sifre.isEmpty()) {
                Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            } else if (!passwordMatcher.matches(sifre)) {
                Toast.makeText(
                    context,
                    "Şifre en az 6 karakter olmalıdır, büyük-küçük harf, rakam ve özel karakter içermelidir",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                auth.createUserWithEmailAndPassword(eposta, sifre)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Kayıt Başarılı", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(kayitOl).navigate(R.id.action_kayit_to_giris)
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            context,
                            "Kayıt Hatası: ${exception.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }

                return design.root
    }

}