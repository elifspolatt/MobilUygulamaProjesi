package com.example.mobiluygulamaprojesi

import java.io.Serializable

data class Siparisler (
    var siparisId: String="",
    var kullaniciId: String="",
    var kullaniciIsmi: String="",
    var kitapAdi: String="",
    var kitapResimUrl: String="",
    var toplamFiyat: Double=0.0,
    var siparisTarihi: String="",
    var siparisDurumu: String="Hazırlanıyor",
): Serializable
