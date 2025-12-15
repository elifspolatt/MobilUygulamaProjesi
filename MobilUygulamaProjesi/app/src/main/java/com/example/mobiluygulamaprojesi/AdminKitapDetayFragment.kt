package com.example.mobiluygulamaprojesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mobiluygulamaprojesi.databinding.FragmentAdminKitapDetayBinding
import com.google.firebase.firestore.FirebaseFirestore


class AdminKitapDetayFragment : Fragment() {
    private lateinit var design: FragmentAdminKitapDetayBinding
    private lateinit var db: FirebaseFirestore
    private val args: AdminKitapDetayFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design = FragmentAdminKitapDetayBinding.inflate(layoutInflater,container,false)
        db= FirebaseFirestore.getInstance()

        val gelenKitap = args.secilenKitap

        if(gelenKitap != null) {
            design.editAdminKitapNo.setText(gelenKitap.kitapNo.toString())
            design.editAdminKitapAdi.setText(gelenKitap.kitapAdi)
            design.editAdminKitapYayinci.setText(gelenKitap.kitapYayinci)
            design.editAdminKitapFiyati.setText(gelenKitap.kitapFiyati.toString())
            design.editAdminKitapResimAdi.setText(gelenKitap.kitapResimAdi)

            design.editAdminKitapStok.setText(gelenKitap.kitapStok.toString())
            design.switchAdminSatisDurumu.isChecked = gelenKitap.satisDurumu

            Glide.with(this).load(gelenKitap.kitapResimAdi).into(design.imageAdminKitapResim)

            design.butonAdminSil.visibility = View.VISIBLE
            design.butonAdminKaydet.text="Güncelle"
        }
        else{
            design.butonAdminSil.visibility = View.GONE
            design.butonAdminKaydet.text="Kaydet"
        }
        design.butonAdminKaydet.setOnClickListener {
            val kitapNoStr = design.editAdminKitapNo.text.toString()
            val kitapAdi = design.editAdminKitapAdi.text.toString()
            val kitapYayinci= design.editAdminKitapYayinci.text.toString()
            val kitapFiyatiStr = design.editAdminKitapFiyati.text.toString()
            val kitapResimAdi= design.editAdminKitapResimAdi.text.toString()

            val kitapStokStr = design.editAdminKitapStok.text.toString()
            val satisDurumu = design.switchAdminSatisDurumu.isChecked

            if(kitapNoStr.isEmpty() || kitapAdi.isEmpty()|| kitapYayinci.isEmpty()
                || kitapFiyatiStr.isEmpty() || kitapStokStr.isEmpty()){
                Toast.makeText(requireContext(),"Alanları Boş Bırakmayınız", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val kitapNo=kitapNoStr.toLongOrNull() ?: 0
            val kitapFiyati=kitapFiyatiStr.toDoubleOrNull() ?: 0.0
            val kitapStok = kitapStokStr.toIntOrNull() ?: 0

            if(gelenKitap==null){
                kitapEkle(kitapNo, kitapAdi, kitapYayinci, kitapFiyati, kitapResimAdi,kitapStok,satisDurumu)
            }
            else{
                if(kitapNo == gelenKitap.kitapNo) {
                    kitapGuncelle(gelenKitap.kitapAdi, kitapNo,
                        kitapAdi, kitapYayinci, kitapFiyati, kitapResimAdi, kitapStok, satisDurumu)
                }
                else {
                    kitapGuncelleKontrol(gelenKitap.kitapAdi, kitapNo,
                        kitapAdi, kitapYayinci, kitapFiyati, kitapResimAdi,
                        kitapStok, satisDurumu)
                }
            }
        }
        design.butonAdminSil.setOnClickListener {
            if(gelenKitap!=null){
                kitapSil(gelenKitap.kitapAdi)
            }
        }
        return design.root
    }

    private fun kitapEkle(kitapNo: Long, kitapAdi: String, kitapYayinci: String, kitapFiyati: Double?, kitapResimAdi: String,
                          kitapStok: Int, satisDurumu: Boolean) {
        db.collection("Kitaplar").whereEqualTo("kitapNo",kitapNo).get()
            .addOnSuccessListener { belgeler ->
                if(!belgeler.isEmpty) {
                    Toast.makeText(requireContext(),"Bu Kitap No Kullanılıyor", Toast.LENGTH_SHORT).show()
                }
                else {
                    val yeniKitapMap = hashMapOf(
                        "kitapId" to kitapNo,
                        "kitapAdi" to kitapAdi,
                        "kitapYayinci" to kitapYayinci,
                        "kitapFiyati" to kitapFiyati,
                        "kitapResimAdi" to kitapResimAdi,
                        "kitapStok" to kitapStok,
                        "satisDurumu" to satisDurumu
                    )
                    db.collection("Kitaplar").add(yeniKitapMap)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(),"Kitap eklendi", Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(design.root).popBackStack()
                        }
                }
            }
    }

    private fun kitapGuncelle(kitapEskiAdi: String, kitapYeniNo: Long, kitapYeniAdi: String,
                              kitapYeniYayinci: String, kitapYeniFiyati: Double?, kitapYeniResimAdi: String,
                              kitapYeniStok: Int, yeniSatisDurumu: Boolean) {
        db.collection("Kitaplar").whereEqualTo("kitapAdi", kitapEskiAdi).get()
            .addOnSuccessListener { belgeler ->
                for(belge in belgeler) {
                    db.collection("Kitaplar").document(belge.id)
                        .update(mapOf(
                            "kitapId" to kitapYeniNo,
                            "kitapNo" to kitapYeniNo,
                            "kitapAdi" to kitapYeniAdi,
                            "kitapYayinci" to kitapYeniYayinci,
                            "kitapFiyati" to kitapYeniFiyati,
                            "kitapResimAdi" to kitapYeniResimAdi,
                            "kitapStok" to kitapYeniStok,
                            "satisDurumu" to yeniSatisDurumu
                        ))
                }
                Toast.makeText(requireContext(),"Kitap güncellendi", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(design.root).popBackStack()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),"Hata: ${exception.localizedMessage}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun kitapGuncelleKontrol(kitapEskiAdi: String, kitapYeniNo: Long, kitapYeniAdi: String,
                                     kitapYeniYayinci: String, kitapYeniFiyati: Double?, kitapYeniResimAdi: String,
                                     kitapYeniStok: Int, yeniSatisDurumu: Boolean) {
        db.collection("Kitaplar").whereEqualTo("kitapNo",kitapYeniNo).get()
            .addOnSuccessListener { belgeler ->
                if(!belgeler.isEmpty) {
                    Toast.makeText(requireContext(),"Bu Kitap No Kullanılıyor", Toast.LENGTH_SHORT).show()
                }
                else {
                    kitapGuncelle(kitapEskiAdi, kitapYeniNo, kitapYeniAdi, kitapYeniYayinci, kitapYeniFiyati, kitapYeniResimAdi, kitapYeniStok, yeniSatisDurumu)
                }
            }
    }

    private fun kitapSil(kitapAdi: String) {
        db.collection("Kitaplar").whereEqualTo("kitapAdi", kitapAdi).get()
            .addOnSuccessListener { belgeler ->
                for(belge in belgeler) {
                    db.collection("Kitaplar").document(belge.id).delete()
                }
                Toast.makeText(requireContext(),"Kitap Silindi", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(design.root).popBackStack()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),"Hata: ${exception.localizedMessage}",Toast.LENGTH_SHORT).show()
            }
    }

}