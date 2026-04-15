package com.example.vpntestapp.data.repository

import com.example.vpntestapp.data.local.CountryDao
import com.example.vpntestapp.data.mapper.toDomain
import com.example.vpntestapp.data.mapper.toEntity
import com.example.vpntestapp.data.remote.CountryApiService
import com.example.vpntestapp.domain.model.Country
import com.example.vpntestapp.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val api: CountryApiService,
    private val dao: CountryDao
) : CountryRepository {

    override fun getCountries(): Flow<Result<List<Country>>> = flow {
        val cached = dao.getAllCountries()
        if (cached.isNotEmpty()) {
            emit(Result.success(cached.map { it.toDomain() }.sortedBy { it.name }))
        }

        try {
            val remote = api.getAllCountries()
            val entities = remote.map { it.toEntity() }
            dao.insertAll(entities)
            val countries = remote.map { it.toDomain() }.sortedBy { it.name }
            emit(Result.success(countries))
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                emit(Result.failure(e))
            }
        }
    }
}
