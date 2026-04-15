package com.example.vpntestapp.data.remote

import com.example.vpntestapp.data.remote.dto.CountryDto
import retrofit2.http.GET

interface CountryApiService {
    @GET("v3.1/all?fields=name,cca2,flags,region")
    suspend fun getAllCountries(): List<CountryDto>
}
