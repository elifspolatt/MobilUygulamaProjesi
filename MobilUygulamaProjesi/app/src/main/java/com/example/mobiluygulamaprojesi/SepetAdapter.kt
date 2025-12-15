package com.example.mobiluygulamaprojesi

import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobiluygulamaprojesi.databinding.SepetimCardTasarimiBinding

class SepetAdapter(var mContext: Context, var sepetListesi: ArrayList<SepetimKitaplar>, var viewModel: SepetimFragment)
    : RecyclerView.Adapter<SepetAdapter.SepetCardTutucu>() {
    inner class SepetCardTutucu(var design: SepetimCardTasarimiBinding) : RecyclerView.ViewHolder(design.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SepetCardTutucu {
        val binding = SepetimCardTasarimiBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return SepetCardTutucu(binding)
    }

    override fun onBindViewHolder(holder: SepetCardTutucu, position: Int) {
        val sepetKitap = sepetListesi[position]
        val tasarim = holder.design
        tasarim.textSepetKitapAdi.text = sepetKitap.kitapAdi
        tasarim.textSepetFiyati.text = "${sepetKitap.kitapFiyati} ₺"
        tasarim.textSepetAdet.text = sepetKitap.adet.toString()

        Glide.with(mContext).load(sepetKitap.kitapResimAdi)
            .placeholder(R.mipmap.ic_launcher).fitCenter()
            .into(tasarim.imageSepetResim)

        tasarim.imageSepetSil.setOnClickListener {
            viewModel.sepettenSil(sepetKitap.belgeId)
        }

        tasarim.butonArtir.setOnClickListener {
            viewModel.adetGuncelle(sepetKitap.belgeId, sepetKitap.adet + 1)
        }

        tasarim.butonAzalt.setOnClickListener {
            if(sepetKitap.adet > 1) {
                viewModel.adetGuncelle(sepetKitap.belgeId, sepetKitap.adet - 1)
            }
            else {
                viewModel.sepettenSil(sepetKitap.belgeId)
            }
        }
    }

    override fun getItemCount(): Int {
        return sepetListesi.size
    }
}