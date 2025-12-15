package com.example.mobiluygulamaprojesi

data class DovizKurlari(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)