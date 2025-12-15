package com.example.mobiluygulamaprojesi

import java.io.Serializable

data class Kitaplar(var kitapNo: Long, var kitapAdi: String,
                    var kitapFiyati: Double, var kitapYayinci: String,
                    var kitapResimAdi: String, var kitapStok: Int = 0,
                    var satisDurumu: Boolean = true) : Serializable {

}