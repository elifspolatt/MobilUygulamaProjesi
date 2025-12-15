package com.example.mobiluygulamaprojesi

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mobiluygulamaprojesi.databinding.FragmentKitapDetayBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class KitapDetayFragment : Fragment() {

    private lateinit var design: FragmentKitapDetayBinding

    private val args: KitapDetayFragmentArgs by navArgs()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var favoriMi = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design = FragmentKitapDetayBinding.inflate(layoutInflater,container,false)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val gelenKitap = args.kitapNesnesi

        design.textKitapAdi.text = gelenKitap.kitapAdi
        design.textKitapYayincisi.text = gelenKitap.kitapYayinci
        design.textKitapFiyati.text = "${gelenKitap.kitapFiyati} ₺"

        Glide.with(requireContext()).load(gelenKitap.kitapResimAdi)
            .placeholder(R.mipmap.ic_launcher).fitCenter()
            .into(design.imageViewKitapDetay)

        favoriKontrol(gelenKitap.kitapNo.toString())

        design.butonSepet.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val sepetRef = db.collection("Kullanicilar")
                    .document(currentUser.uid).collection("Sepet")

                val belgeId = gelenKitap.kitapNo.toString()

                sepetRef.document(belgeId).get()
                    .addOnSuccessListener { document ->
                        if(document.exists()) {
                            val simdikiAdet = (document.get("adet") as? Number) ?.toInt() ?: 1
                            val yeniAdet = simdikiAdet + 1

                            sepetRef.document(belgeId).update("adet",yeniAdet)
                        }
                        else {
                            val sepetKitap = hashMapOf(
                                "kitapId" to gelenKitap.kitapNo,
                                "kitapAdi" to gelenKitap.kitapAdi,
                                "kitapYayinci" to gelenKitap.kitapYayinci,
                                "kitapFiyati" to gelenKitap.kitapFiyati,
                                "kitapResimAdi" to gelenKitap.kitapResimAdi,
                                "adet" to 1
                            )
                            sepetRef.document(belgeId).set(sepetKitap)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(),"Sepete eklendi", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(requireContext(),
                                        "Hata: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Hata: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Lütfen giriş yapınız.", Toast.LENGTH_SHORT).show()
            }

        }

        design.butonFavori.setOnClickListener {
            val currentUser = auth.currentUser

            if (currentUser != null) {
                if(favoriMi){
                    favoridenCikar(gelenKitap.kitapNo.toString())
                }
                else {
                    favoriyeEkle(gelenKitap)
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen giriş yapınız.", Toast.LENGTH_SHORT).show()
            }
        }

        return design.root
    }

    private fun favoriKontrol(kitapId: String) {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            db.collection("Kullanicilar").document(currentUser.uid)
                .collection("Favoriler").document(kitapId)
                .get().addOnSuccessListener { document ->
                    if(document.exists()) {
                        favoriMi = true
                        yildizDolu()
                    }
                    else {
                        favoriMi = false
                        yildizBos()
                    }
                }
        }
    }

    private fun favoriyeEkle(kitap: Kitaplar) {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            val favoriRef = db.collection("Kullanicilar")
                .document(currentUser.uid).collection("Favoriler")
            val favoriKitap = hashMapOf(
                "kitapId" to kitap.kitapNo,
                "kitapAdi" to kitap.kitapAdi,
                "kitapYayinci" to kitap.kitapYayinci,
                "kitapFiyati" to kitap.kitapFiyati,
                "kitapResimAdi" to kitap.kitapResimAdi
            )
            favoriRef.document(kitap.kitapNo.toString()).set(favoriKitap)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Favorilere Eklendi", Toast.LENGTH_SHORT).show()
                    favoriMi = true
                    yildizDolu()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Hata: ${exception.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun favoridenCikar(kitapId: String) {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            db.collection("Kullanicilar").document(currentUser.uid)
                .collection("Favoriler").document(kitapId).delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Favorilerden Çıkarıldı", Toast.LENGTH_SHORT).show()
                    favoriMi = false
                    yildizBos()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Hata: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun yildizDolu() {
        design.butonFavori.setImageResource(android.R.drawable.btn_star_big_on)
        design.butonFavori.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFC107"))
    }

    private fun yildizBos() {
        design.butonFavori.setImageResource(android.R.drawable.btn_star_big_off)
        design.butonFavori.imageTintList = ColorStateList.valueOf(Color.GRAY)
    }
}