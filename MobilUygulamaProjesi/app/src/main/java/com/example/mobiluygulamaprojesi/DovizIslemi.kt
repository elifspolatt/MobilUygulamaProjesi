package com.example.mobiluygulamaprojesi

import retrofit2.Call
import retrofit2.http.GET

interface DovizIslemi {
    @GET("latest?fromEUR&to=TRY,USD")
    fun getDovizKurlari(): Call<DovizKurlari>
}