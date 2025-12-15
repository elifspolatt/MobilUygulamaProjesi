package com.example.mobiluygulamaprojesi

data class SepetimKitaplar (
    var belgeId: String,
    var kitapId: Long,
    var kitapAdi: String,
    var kitapFiyati: Double,
    var kitapResimAdi: String,
    var adet: Int
) {
}