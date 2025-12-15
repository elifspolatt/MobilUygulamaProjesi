package com.example.mobiluygulamaprojesi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RVAdapter(private val mContext: Context, private val kitaplarListesi: List<Kitaplar>,
    private val onItemClick: (Kitaplar) -> Unit)
    : RecyclerView.Adapter<RVAdapter.CardViewTasarimNesneleriniTutucu>() {
    inner class CardViewTasarimNesneleriniTutucu(view: View): RecyclerView.ViewHolder(view) {
        var cardViewKitap: CardView
        var textCardAd: TextView
        var textCardFiyat: TextView
        var kitapResmi: ImageView

        init {
            cardViewKitap = view.findViewById(R.id.cardViewKitap)
            textCardAd = view.findViewById(R.id.textCardAd)
            textCardFiyat = view.findViewById(R.id.textCardFiyat)
            kitapResmi = view.findViewById(R.id.kitapResmi)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewTasarimNesneleriniTutucu {
        val design = LayoutInflater.from(mContext).inflate(R.layout.card_tasarimi,parent,false)
        return CardViewTasarimNesneleriniTutucu(design)
    }

    override fun onBindViewHolder(holder: CardViewTasarimNesneleriniTutucu, position: Int) {
        val kitap = kitaplarListesi[position]

        holder.textCardAd.text = kitap.kitapAdi
        holder.textCardFiyat.text = "${kitap.kitapFiyati} ₺"

        val resimUrl = kitap.kitapResimAdi
        Glide.with(mContext).load(resimUrl).placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher).fitCenter().into(holder.kitapResmi)

        holder.cardViewKitap.setOnClickListener {
            onItemClick(kitap)
        }
    }

    override fun getItemCount(): Int {
        return kitaplarListesi.size
    }
}