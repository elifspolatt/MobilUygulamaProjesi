package com.example.mobiluygulamaprojesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mobiluygulamaprojesi.databinding.FragmentFavorilerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FavorilerFragment : Fragment() {

    private lateinit var design: FragmentFavorilerBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var favoriKitaplarList: ArrayList<Kitaplar>
    private lateinit var adapter: RVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        design = FragmentFavorilerBinding.inflate(layoutInflater,container,false)
        design.rvFavoriler.setHasFixedSize(true)
        design.rvFavoriler.layoutManager = GridLayoutManager(getContext(),2)

        favoriKitaplarList = ArrayList()
        adapter = RVAdapter(requireContext(),favoriKitaplarList) { secilenKitap ->
            Navigation.findNavController(design.root)
                .navigate(FavorilerFragmentDirections.actionFavorilerToKitapDetay(secilenKitap))
        }
        design.rvFavoriler.adapter = adapter
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("Kullanicilar").document(currentUser.uid).collection("Favoriler")
                .addSnapshotListener { value, error ->
                    if(error != null) {
                        Toast.makeText(requireContext(),error.localizedMessage, Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }
                    if(value != null) {
                        favoriKitaplarList.clear()

                        for(kitap in value) {
                            val kitapNo = (kitap.get("kitapId") as? Number)?.toLong() ?: 0
                            val kitapAdi = kitap.getString("kitapAdi") ?: ""
                            val kitapYayinci = kitap.getString("kitapYayinci") ?: ""
                            val kitapFiyati = (kitap.get("kitapFiyati") as? Number)?.toDouble() ?: 0.0
                            val kitapResimAdi = kitap.getString("kitapResimAdi") ?: ""

                            val kitap = Kitaplar(kitapNo,kitapAdi,kitapFiyati,kitapYayinci,kitapResimAdi)
                            favoriKitaplarList.add(kitap)
                        }

                        if(favoriKitaplarList.isEmpty()) {
                            design.textFavorilerBos.visibility = View.VISIBLE
                            design.rvFavoriler.visibility = View.GONE
                        }
                        else {
                            design.textFavorilerBos.visibility = View.GONE
                            design.rvFavoriler.visibility = View.VISIBLE
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
        }
        else{
            design.textFavorilerBos.text = "Giriş yapmalısınız."
            design.textFavorilerBos.visibility = View.VISIBLE
        }
        return design.root
    }
}