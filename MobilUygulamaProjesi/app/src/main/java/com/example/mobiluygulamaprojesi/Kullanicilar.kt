package com.example.mobiluygulamaprojesi

data class Kullanicilar(
    var eposta: String="",
    var sifre: String="",
    var adSoyad: String="",
    var telefon: String="",
    var kullaniciId: String="",
    var adminMi: Boolean=false,
    var aktifMi: Boolean=true
)