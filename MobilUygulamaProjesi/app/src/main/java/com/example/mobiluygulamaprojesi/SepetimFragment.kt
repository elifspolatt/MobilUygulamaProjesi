package com.example.mobiluygulamaprojesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiluygulamaprojesi.databinding.FragmentSepetimBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class SepetimFragment : Fragment() {

    private lateinit var design: FragmentSepetimBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var sepetListesi: ArrayList<SepetimKitaplar>
    private lateinit var adapter: SepetAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design = FragmentSepetimBinding.inflate(layoutInflater,container,false)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        design.rvSepet.layoutManager = LinearLayoutManager(requireContext())
        sepetListesi = ArrayList()

        adapter = SepetAdapter(requireContext(),sepetListesi,this)
        design.rvSepet.adapter = adapter

        sepetVerileriniGetir()

        dovizKurlariniGetir()

        design.butonSepetiOnayla.setOnClickListener {
            if(sepetListesi.isNotEmpty()) {
                odemeYap()
            }
            else {
                Toast.makeText(requireContext(),"Sepet boştur.", Toast.LENGTH_SHORT).show()
            }
        }


        return design.root
    }

    private fun dovizKurlariniGetir() {
        val retrofit = Retrofit.Builder().baseUrl("https://api.frankfurter.app/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        val service = retrofit.create(DovizIslemi::class.java)

        service.getDovizKurlari().enqueue(object: Callback<DovizKurlari> {
            override fun onResponse(call: Call<DovizKurlari>, response: Response<DovizKurlari>) {
                if(response.isSuccessful) {
                    val kurlar = response.body()
                    if(kurlar != null) {
                        val tryRate = kurlar.rates["TRY"]
                        val usdRate = kurlar.rates["USD"]

                        if(tryRate != null && usdRate != null) {
                            val dolarTl = tryRate / usdRate
                            val formattedText = String.format("€1 = %.2f ₺ | $1 = %.2f TRY", tryRate, dolarTl)
                            design.textDovizAd.text = formattedText
                        }
                    }
                }
            }
            override fun onFailure(call: Call<DovizKurlari>, t: Throwable) {
                design.textDovizAd.text = "Kur bilgisi alınamadı."
            }
        }
        )
    }

    private fun sepetVerileriniGetir(){
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("Kullanicilar").document(currentUser.uid)
                .collection("Sepet")
                .addSnapshotListener { value, error ->
                    if(error != null) {
                        Toast.makeText(requireContext(),"Hata: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }
                    if(value != null) {
                        sepetListesi.clear()
                        var toplamFiyat: Double = 0.0
                        for(kitap in value){
                            try{
                                val belgeId=kitap.id
                                val kitapId=(kitap.get("kitapId") as? Number)?.toLong() ?: 0
                                val kitapAdi=kitap.getString("kitapAdi") ?: ""
                                val kitapFiyati=(kitap.getDouble("kitapFiyati") as? Number)?.toDouble() ?: 0.0
                                val kitapResimAdi=kitap.getString("kitapResimAdi") ?: ""
                                val adet=(kitap.get("adet") as? Number)?.toInt() ?: 0

                                val sepetKitap= SepetimKitaplar(belgeId,kitapId,kitapAdi,kitapFiyati,kitapResimAdi,adet)
                                sepetListesi.add(sepetKitap)
                                toplamFiyat += kitapFiyati * adet

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                        adapter.notifyDataSetChanged()
                        design.textToplamFiyat.text = "${toplamFiyat} ₺"
                    }

                }

        }
    }

    fun sepettenSil(belgeId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("Kullanicilar").document(currentUser.uid)
                .collection("Sepet").document(belgeId).delete()

        }
    }
    fun adetGuncelle(belgeId: String, yeniAdet: Int) {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            db.collection("Kullanicilar").document(currentUser.uid)
                .collection("Sepet").document(belgeId).update("adet",yeniAdet)
        }
    }
    private fun odemeYap(){
        val yapici= AlertDialog.Builder(requireContext())
        yapici.setTitle("Ödeme işlemi")
        yapici.setMessage("Ödeme işlemini onaylıyor musunuz?\nÖdeme tutarı: ${design.textToplamFiyat.text}")
        yapici.setPositiveButton("ONAYLA"){_,_ ->
            siparisleriOlustur()
        }
        yapici.setNegativeButton("İPTAL",null)
        yapici.show()
    }

    private fun siparisleriOlustur() {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            db.collection("Kullanicilar").document(currentUser.uid).get()
                .addOnSuccessListener { document->
                    if(document != null && document.exists()) {
                        val kullaniciIsmi = document.getString("isim") ?: "İsimsiz Müşteri"
                        val tarihFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val suankiTarih = tarihFormat.format(Date())

                        for(sepetUrun in sepetListesi) {
                            val siparisMap = hashMapOf(
                                "kullaniciId" to currentUser.uid,
                                "kullaniciIsmi" to kullaniciIsmi,
                                "kitapAdi" to "${sepetUrun.kitapAdi} (${sepetUrun.adet} adet)",
                                "kitapResimUrl" to sepetUrun.kitapResimAdi,
                                "toplamFiyat" to sepetUrun.kitapFiyati * sepetUrun.adet,
                                "siparisTarihi" to suankiTarih,
                                "siparisDurumu" to "Hazırlanıyor"
                            )
                            db.collection("Siparisler").add(siparisMap)
                        }

                        sepetiBosalt()
                        Toast.makeText(requireContext(),"Siparişiniz alındı, onay bekleniyor.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(),"Hata: ${exception.localizedMessage}",
                        Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun sepetiBosalt() {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            val sepetRef = db.collection("Kullanicilar")
                .document(currentUser.uid).collection("Sepet")
            sepetRef.get().addOnSuccessListener { belgeler ->
                for(belge in belgeler) {
                    belge.reference.delete()
                }
            }
        }
    }
}