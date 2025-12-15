package com.example.mobiluygulamaprojesi

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiluygulamaprojesi.databinding.FragmentAdminKullanicilarBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminKullanicilarFragment : Fragment() {

    private lateinit var design: FragmentAdminKullanicilarBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var kullaniciListesi: ArrayList<Kullanicilar>
    private lateinit var adapter: AdminKullanicilarAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design = FragmentAdminKullanicilarBinding.inflate(layoutInflater,container,false)
        db = FirebaseFirestore.getInstance()

        design.recyclerViewAdminKullanicilar.layoutManager = LinearLayoutManager(requireContext())
        design.recyclerViewAdminKullanicilar.setHasFixedSize(true)

        kullaniciListesi = ArrayList()

        adapter = AdminKullanicilarAdapter(requireContext(),kullaniciListesi){ secilenKullanici, islemTuru ->
            when(islemTuru){
                "duzenle" -> kullaniciDuzenlePenceresiAc(secilenKullanici)
                "pasif_yap" -> kullaniciDurumuDegistir(secilenKullanici.kullaniciId,false)
                "aktif_yap" -> kullaniciDurumuDegistir(secilenKullanici.kullaniciId,true)
                "sil" -> kullaniciSil(secilenKullanici)
            }
        }

        design.recyclerViewAdminKullanicilar.adapter=adapter

        kullanicilariGetir()

        return design.root
    }

    private fun kullanicilariGetir() {
        db.collection("Kullanicilar").addSnapshotListener { value, error ->
            if(error != null) {
                Toast.makeText(requireContext(),"Hata: ${error.localizedMessage}",Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if(value != null) {
                kullaniciListesi.clear()
                for(belge in value) {
                    try {
                        val adSoyad=belge.getString("adSoyad") ?: ""
                        val eposta=belge.getString("eposta") ?: ""
                        val telefon=belge.getString("telefon") ?: ""
                        val sifre=belge.getString("sifre") ?: ""
                        val adminMi=belge.getBoolean("adminMi") ?: false
                        val aktifMi=belge.getBoolean("aktifMi") ?: true
                        val kullaniciId=belge.id
                        val kullanici=Kullanicilar(
                            eposta=eposta,
                            sifre=sifre,
                            adSoyad = adSoyad,
                            telefon=telefon,
                            kullaniciId=kullaniciId,
                            adminMi=adminMi,
                            aktifMi=aktifMi
                        )
                        kullaniciListesi.add(kullanici)
                    }
                    catch (e: Exception) {

                    }
                }
                adapter.notifyDataSetChanged()
            }

        }

    }
    private fun kullaniciDurumuDegistir(belgeId: String, yeniDurum: Boolean) {
        db.collection("Kullanicilar").document(belgeId)
            .update("aktifMi", yeniDurum)
            .addOnSuccessListener {
                val mesaj = if(yeniDurum) "Kullanıcı Aktif Hale Getirildi" else "Kullanıcı Pasif Hale Getirildi"
                Toast.makeText(requireContext(),mesaj, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),"Hata: ${exception.localizedMessage}",Toast.LENGTH_SHORT).show()
            }
    }
    private fun kullaniciSil(kullanici: Kullanicilar) {
        val yapici = AlertDialog.Builder(requireContext())
        yapici.setTitle("Kullanıcı Silme")
        yapici.setMessage("${kullanici.adSoyad} isimli kullanıcıyı silmek istediğinize emin misiniz?")

        yapici.setPositiveButton("EVET") { _, _ ->
            db.collection("Kullanicilar").document(kullanici.kullaniciId).delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(),"Kullanıcı Silindi", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(),"Hata: ${exception.localizedMessage}",Toast.LENGTH_SHORT).show()
                }
        }
        yapici.setNegativeButton("HAYIR",null)
        yapici.show()
    }

    private fun kullaniciDuzenlePenceresiAc(kullanici: Kullanicilar) {
        val layout = android.widget.LinearLayout(requireContext())
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50,40,50,10)

        val editIsim = android.widget.EditText(requireContext())
        editIsim.hint = "İsim Soyisim"
        editIsim.setText(kullanici.adSoyad)
        layout.addView(editIsim)

        val editTelefon = android.widget.EditText(requireContext())
        editTelefon.hint = "Telefon"
        editTelefon.inputType = android.text.InputType.TYPE_CLASS_PHONE
        editTelefon.setText(kullanici.telefon)
        layout.addView(editTelefon)

        val editPosta = android.widget.EditText(requireContext())
        editPosta.hint = "E-posta"
        editPosta.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        editPosta.setText(kullanici.eposta)
        layout.addView(editPosta)

        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Kullanıcı Bilgilerini Güncelle")
        alert.setView(layout)

        alert.setPositiveButton("GÜNCELLE") { _, _ ->
            val yeniIsim = editIsim.text.toString().trim()
            val yeniTelefon = editTelefon.text.toString().trim()
            val yeniEposta = editPosta.text.toString().trim()

            if(yeniIsim.isNotEmpty() && yeniEposta.isNotEmpty()) {
                val guncelVeri = mapOf(
                    "adSoyad" to yeniIsim,
                    "telefon" to yeniTelefon,
                    "eposta" to yeniEposta
                )

                db.collection("Kullanicilar").document(kullanici.kullaniciId).update(guncelVeri)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(),"Bilgiler güncellendi", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(),"Hata: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
            }
            else {
                Toast.makeText(requireContext(),"Lütfen isim, telefon ve e-posta giriniz", Toast.LENGTH_SHORT).show()
            }
        }
        alert.setNegativeButton("İPTAL",null)
        alert.show()
    }
}