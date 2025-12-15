package com.example.mobiluygulamaprojesi

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition

class AdminSiparislerAdapter(
    private val mContext: Context,
    private val siparisListesi: List<Siparisler>,
    private val onDurumGuncelleClick: (Siparisler) -> Unit
): RecyclerView.Adapter<AdminSiparislerAdapter.SiparisTutucu>()
{
    inner class SiparisTutucu(tasarim: View): RecyclerView.ViewHolder(tasarim)
    {
        var textKitapAdi: TextView= tasarim.findViewById(R.id.textSiparisKitapAdi)
        var textKullanici: TextView= tasarim.findViewById(R.id.textSiparisKullaniciIsmi)
        var textTutar: TextView= tasarim.findViewById(R.id.textSiparisToplamFiyat)
        var textTarih: TextView= tasarim.findViewById(R.id.textSiparisTarihi)
        var textDurum: TextView= tasarim.findViewById(R.id.textSiparisDurum)
        var imageKitap: ImageView= tasarim.findViewById(R.id.imageSiparisResim)
        var butonGuncelle: Button= tasarim.findViewById(R.id.butonSiparisGuncelle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiparisTutucu
    {
        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.card_siparis_admin,parent,false)
        return SiparisTutucu(tasarim)
    }
    override fun onBindViewHolder(holder: SiparisTutucu, position: Int) {
        val siparis = siparisListesi[position]
        holder.textKitapAdi.text = siparis.kitapAdi
        holder.textKullanici.text="Müşteri:${siparis.kullaniciIsmi}"
        holder.textTutar.text="Toplam Tutar:${siparis.toplamFiyat} ₺"
        holder.textTarih.text="Sipariş Tarihi:${siparis.siparisTarihi}"
        holder.textDurum.text=siparis.siparisDurumu

        when (siparis.siparisDurumu) {
            "Hazırlanıyor" -> holder.textDurum.setTextColor(Color.parseColor("#FF9800"))
            "Kargoda" -> holder.textDurum.setTextColor(Color.parseColor("#2196F3"))
            "Teslim Edildi" -> holder.textDurum.setTextColor(Color.parseColor("#4CAF50"))
            "İptal Edildi" -> holder.textDurum.setTextColor(Color.RED)
            else -> holder.textDurum.setTextColor(Color.BLACK)
        }

        Glide.with(mContext)
            .load(siparis.kitapResimUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(holder.imageKitap)
        holder.butonGuncelle.setOnClickListener {
            onDurumGuncelleClick(siparis)
        }
    }

    override fun getItemCount(): Int {
        return siparisListesi.size
    }
}