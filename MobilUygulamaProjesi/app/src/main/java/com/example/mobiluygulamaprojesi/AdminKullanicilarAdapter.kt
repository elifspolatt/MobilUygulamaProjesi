package com.example.mobiluygulamaprojesi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import androidx.appcompat.widget.PopupMenu

class AdminKullanicilarAdapter(
    private val mContext: Context,
    private val kullaniciListesi: List<Kullanicilar>,
    private val onItemClick: (Kullanicilar, String) -> Unit
) : RecyclerView.Adapter<AdminKullanicilarAdapter.KullaniciCardTutucu>() {

    inner class KullaniciCardTutucu(tasarim: View): RecyclerView.ViewHolder(tasarim){
        var textIsim: TextView = tasarim.findViewById(R.id.textKullaniciIsim)
        var textEposta: TextView = tasarim.findViewById(R.id.textKullaniciEposta)
        var textTelefon: TextView = tasarim.findViewById(R.id.textKullaniciTelefon)
        var imageDurum: ImageView = tasarim.findViewById(R.id.imageKullaniciDurum)
        var cardView: View = tasarim.findViewById<ImageView>(R.id.imageKullaniciResim).parent.parent as View
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KullaniciCardTutucu {
        val tasarim = LayoutInflater.from(mContext)
            .inflate(R.layout.card_kullanici_tasarimi, parent, false)
        return KullaniciCardTutucu(tasarim)
    }

    override fun onBindViewHolder(holder: KullaniciCardTutucu, position: Int) {
        val kullanici = kullaniciListesi[position]

        holder.textIsim.text = kullanici.adSoyad
        holder.textEposta.text = kullanici.eposta

        if(kullanici.telefon.isNotEmpty()){
            holder.textTelefon.text = kullanici.telefon
            holder.textTelefon.visibility = View.VISIBLE
        }
        else {
            holder.textTelefon.visibility = View.GONE
        }

        if(kullanici.adminMi) {
            holder.imageDurum.setColorFilter(Color.parseColor("#4CAF50"))
        }
        else{
            holder.imageDurum.setColorFilter(Color.parseColor("#F44336"))
        }

        holder.itemView.setOnClickListener { view ->
            val popup = PopupMenu(mContext, holder.imageDurum)

            popup.menu.add("Bilgileri Düzenle")

            if(kullanici.aktifMi) {
                popup.menu.add("Pasif Hale Getir")
            }
            else{
                popup.menu.add("Aktif Hale Getir")
            }
            popup.menu.add("Kullanıcıyı Sil")

            popup.setOnMenuItemClickListener { item ->
                when(item.title) {
                    "Bilgileri Düzenle" -> onItemClick(kullanici,"duzenle")
                    "Pasif Hale Getir" -> onItemClick(kullanici,"pasif_yap")
                    "Aktif Hale Getir" -> onItemClick(kullanici,"aktif_yap")
                    "Kullanıcıyı Sil" -> onItemClick(kullanici,"sil")
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int {
        return kullaniciListesi.size
    }

}