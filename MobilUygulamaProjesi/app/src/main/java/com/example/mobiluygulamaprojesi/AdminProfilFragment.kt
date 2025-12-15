package com.example.mobiluygulamaprojesi

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobiluygulamaprojesi.databinding.FragmentAdminProfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminProfilFragment: Fragment()  {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var design: FragmentAdminProfilBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        design = FragmentAdminProfilBinding.inflate(layoutInflater,container,false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        adminBilgileriniGetir()
        design.butonBilgileriGuncelle.setOnClickListener {
            bilgiGuncellePenceresi()
        }
        design.butonSifreSifirla.setOnClickListener {
            sifreSifirlamaPenceresi()
        }
        design.butonAdminCikisYap.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(),"Çıkış yapıldı.", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return design.root
    }
    private fun adminBilgileriniGetir(){
        val currentUser = auth.currentUser
        if(currentUser != null) {
            design.textAdminEposta.text = currentUser.email
            db.collection("Kullanicilar").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if(document != null && document.exists()) {
                        val adSoyad = document.getString("isim")?: "Admin"
                        design.textAdminIsimSoyisim.text = adSoyad
                }
                    else{
                        design.textAdminIsimSoyisim.text = "Bilgi bulunamadı"
                    }
                }
                .addOnFailureListener {
                    design.textAdminIsimSoyisim.text = "Hata oluştu"
                }
        }
    }
    private fun bilgiGuncellePenceresi(){
        val editText= EditText(requireContext())
        editText.hint="Yeni isim soyisim"
        editText.setText(design.textAdminIsimSoyisim.text)
        val isim= AlertDialog.Builder(requireContext())
        isim.setTitle("İsim Soyisim Güncelleme")
        isim.setMessage("Yeni isim soyisim giriniz:")
        isim.setView(editText)
        isim.setPositiveButton("Güncelle"){_,_ ->
            val yeniIsim=editText.text.toString().trim()
            if(yeniIsim.isNotEmpty()){
                val currentUser = auth.currentUser
                if(currentUser != null) {
                    db.collection("Kullanicilar").document(currentUser.uid)
                        .update("isim",yeniIsim)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(),"İsim soyisim güncellendi.", Toast.LENGTH_SHORT).show()
                            adminBilgileriniGetir()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(),"Güncelleme başarısız.", Toast.LENGTH_SHORT).show()
                        }
                }
}          else{
                Toast.makeText(requireContext(),"Lütfen isim soyisim giriniz.", Toast.LENGTH_SHORT).show()
}
        }
        isim.setNegativeButton("İptal",null)
        isim.show()
    }
    private fun sifreSifirlamaPenceresi(){
        val currentUser = auth.currentUser
        val eposta=currentUser?.email
        if  (eposta != null) {
            val isim= AlertDialog.Builder(requireContext())
            isim.setTitle("Şifre Sıfırlama")
            isim.setMessage("$eposta adresine şifre sıfırlama maili gönderilsin mi?")
            isim.setPositiveButton("Evet"){_,_ ->
            auth.sendPasswordResetEmail(eposta)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(),"Şifre sıfırlama maili gönderildi.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {e->
                    Toast.makeText(requireContext(),"Hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
                }
            isim.setNegativeButton("Hayır",null)
            isim.show()

        }else{
            Toast.makeText(requireContext(),"E-posta adresi bulunamadı.", Toast.LENGTH_SHORT).show()
        }

    }
}