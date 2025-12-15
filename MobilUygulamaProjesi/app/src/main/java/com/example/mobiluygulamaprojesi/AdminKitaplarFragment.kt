package com.example.mobiluygulamaprojesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mobiluygulamaprojesi.databinding.FragmentAdminKitaplarBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminKitaplarFragment : Fragment() {

    private lateinit var design: FragmentAdminKitaplarBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var kitapListesi: ArrayList<Kitaplar>
    private lateinit var adapter: RVAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design = FragmentAdminKitaplarBinding.inflate(layoutInflater,container,false)
        db = FirebaseFirestore.getInstance()

        design.recyclerViewAdminKitaplar.setHasFixedSize(true)
        design.recyclerViewAdminKitaplar.layoutManager = GridLayoutManager(requireContext(), 2)

        kitapListesi = ArrayList()

        adapter = RVAdapter(requireContext(), kitapListesi) { secilenKitap ->
            val kitapAkisi = AdminKitaplarFragmentDirections
                .actionAdminKitaplarFragmentToAdminKitapDetayFragment(secilenKitap)
            Navigation.findNavController(design.root).navigate(kitapAkisi)
        }
        design.recyclerViewAdminKitaplar.adapter = adapter

        design.floatingActionButtonAdminKitapEkle.setOnClickListener { kitapEkle ->
            val ekleme = AdminKitaplarFragmentDirections
                .actionAdminKitaplarFragmentToAdminKitapDetayFragment(null)

            Navigation.findNavController(kitapEkle).navigate(ekleme)
        }

        kitaplariYukle()

        return design.root
    }

    private fun kitaplariYukle() {
        db.collection("Kitaplar").addSnapshotListener { value, error ->
            if(error != null) {
                Toast.makeText(requireContext(),"Hata: ${error.localizedMessage}",Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if(value != null) {
                kitapListesi.clear()
                for(belge in value) {
                    try {
                        val kitapNo = (belge.get("kitapNo") as? Number?)?.toLong() ?: 0
                        val kitapAdi = belge.getString("kitapAdi") ?: ""
                        val kitapFiyati = belge.getDouble("kitapFiyati") ?: 0.0
                        val kitapYayinci = belge.getString("kitapYayinci") ?: ""
                        val kitapResimAdi = belge.getString("kitapResimAdi") ?: ""
                        val kitapStok = (belge.get("kitapStok") as? Number?) ?.toInt() ?: 0
                        val satisDurumu = belge.getBoolean("satisDurumu") ?: true

                        val kitap = Kitaplar(kitapNo, kitapAdi, kitapFiyati, kitapYayinci, kitapResimAdi, kitapStok, satisDurumu)
                        kitapListesi.add(kitap)

                    } catch(e: Exception) {

                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }
}