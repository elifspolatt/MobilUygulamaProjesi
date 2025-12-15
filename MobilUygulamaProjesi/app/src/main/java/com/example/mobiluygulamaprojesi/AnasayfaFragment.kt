package com.example.mobiluygulamaprojesi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mobiluygulamaprojesi.databinding.FragmentAnasayfaBinding
import com.google.firebase.firestore.FirebaseFirestore


class AnasayfaFragment : Fragment() {
    private lateinit var kitaplarList: ArrayList<Kitaplar>
    private lateinit var adapter: RVAdapter
    private lateinit var db: FirebaseFirestore

    private lateinit var design: FragmentAnasayfaBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design = FragmentAnasayfaBinding.inflate(layoutInflater,container,false)

        db = FirebaseFirestore.getInstance()

        design.recylerViewKitaplar.setHasFixedSize(true)
        design.recylerViewKitaplar.layoutManager = GridLayoutManager(context,2, RecyclerView.VERTICAL,false)

        kitaplarList = ArrayList<Kitaplar>()

        adapter = RVAdapter(requireContext(),kitaplarList){ secilenKitap->
            Navigation.findNavController(design.root)
                .navigate(AnasayfaFragmentDirections.actionAnasayfaToKitapDetay(secilenKitap))
        }
        design.recylerViewKitaplar.adapter = adapter

        db.collection("Kitaplar").get()
            .addOnSuccessListener { kitaplar ->
                kitaplarList.clear()
                for(kitap in kitaplar) {
                    try {
                        val kitapNo = (kitap.get("kitapNo") as? Number) ?.toLong() ?: 0
                        val kitapAdi = kitap.getString("kitapAdi") ?: ""
                        val kitapYayinci = kitap.getString("kitapYayinci") ?: ""
                        val kitapFiyati = (kitap.get("kitapFiyati") as? Number) ?.toDouble() ?: 0.0
                        val kitapResimAdi = kitap.getString("kitapResimAdi") ?: ""
                        val kitap = Kitaplar(kitapNo,kitapAdi,kitapFiyati,kitapYayinci,kitapResimAdi)
                        kitaplarList.add(kitap)
                    }catch (e: Exception) {
                        Log.e("Hata","Veri dönüştürme hatası: ${e.message}")
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),"Veri alma hatası: ${exception.localizedMessage}",Toast.LENGTH_SHORT).show()
            }

        return design.root
    }

}