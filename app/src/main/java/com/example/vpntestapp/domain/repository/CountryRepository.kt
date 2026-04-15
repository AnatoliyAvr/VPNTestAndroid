package com.example.vpntestapp.domain.repository

import com.example.vpntestapp.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    fun getCountries(): Flow<Result<List<Country>>>
}
