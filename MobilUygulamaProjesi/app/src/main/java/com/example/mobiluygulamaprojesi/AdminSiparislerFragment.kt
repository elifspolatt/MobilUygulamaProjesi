package com.example.mobiluygulamaprojesi

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiluygulamaprojesi.databinding.FragmentAdminSiparislerBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminSiparislerFragment : Fragment()
{
    private lateinit var design: FragmentAdminSiparislerBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var siparisListesi: ArrayList<Siparisler>
    private lateinit var adapter: AdminSiparislerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        design = FragmentAdminSiparislerBinding.inflate(layoutInflater,container,false)
        db = FirebaseFirestore.getInstance()
        design.recyclerViewAdminSiparisler.layoutManager = LinearLayoutManager(requireContext())
        design.recyclerViewAdminSiparisler.setHasFixedSize(true)
        siparisListesi = ArrayList()
        adapter = AdminSiparislerAdapter(requireContext(),siparisListesi){ secilenSiparis ->
            durumGuncellemePenceresiAc(secilenSiparis)
        }
        design.recyclerViewAdminSiparisler.adapter=adapter
        siparisleriGetir()
        return design.root
    }
    private fun siparisleriGetir(){
        db.collection("Siparisler").addSnapshotListener { value, error ->
            if(error != null) {
                Toast.makeText(requireContext(),"Hata: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if(value != null) {
                siparisListesi.clear()
                for(belge in value) {
                    try{
                        val siparisId=belge.id
                        val kullaniciId=belge.getString("kullaniciId") ?: ""
                        val kullaniciIsmi=belge.getString("kullaniciIsmi") ?: "Bilinmeyen Müşteri"
                        val kitapAdi=belge.getString("kitapAdi") ?: ""
                        val kitapResimUrl=belge.getString("kitapResimUrl") ?: ""
                        val toplamFiyat=belge.getDouble("toplamFiyat") ?: 0.0
                        val siparisTarihi=belge.getString("siparisTarihi") ?: ""
                        val siparisDurumu=belge.getString("siparisDurumu") ?: "Hazırlanıyor"
                        val siparis=Siparisler(
                            siparisId=siparisId,
                            kullaniciId=kullaniciId,
                            kullaniciIsmi=kullaniciIsmi,
                            kitapAdi=kitapAdi,
                            kitapResimUrl=kitapResimUrl,
                            toplamFiyat=toplamFiyat,
                            siparisTarihi=siparisTarihi,
                            siparisDurumu=siparisDurumu
                        )
                        siparisListesi.add(siparis)
                    }catch (e:Exception){

                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }
    private fun durumGuncellemePenceresiAc(siparis:Siparisler){
        val secenekler=arrayOf("Hazırlanıyor","Kargoda","Teslim Edildi","İptal Edildi")
        val builder= AlertDialog.Builder(requireContext())
        builder.setTitle("Sipariş Durumunu Güncelle")
        builder.setItems(secenekler){_,which ->
            val secilenDurum=secenekler[which]
            db.collection("Siparisler").document(siparis.siparisId)
                .update("siparisDurumu",secilenDurum)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(),"Sipariş durumu güncellendi: $secilenDurum",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),"Güncelleme Başarısız",Toast.LENGTH_SHORT).show()
                }
        }
        builder.setNegativeButton("İptal",null)
        builder.show()
    }
}