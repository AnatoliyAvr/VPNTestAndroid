package com.example.vpntestapp.data.mapper

import com.example.vpntestapp.data.local.CountryEntity
import com.example.vpntestapp.data.remote.dto.CountryDto
import com.example.vpntestapp.domain.model.Country

fun CountryDto.toDomain(): Country = Country(
    name = name.common,
    code = cca2,
    flagUrl = flags.png,
    region = region
)

fun CountryDto.toEntity(): CountryEntity = CountryEntity(
    code = cca2,
    name = name.common,
    flagUrl = flags.png,
    region = region
)

fun CountryEntity.toDomain(): Country = Country(
    name = name,
    code = code,
    flagUrl = flagUrl,
    region = region
)
