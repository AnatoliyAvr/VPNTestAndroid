package com.example.vpntestapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CountryDto(
    @SerializedName("name") val name: NameDto,
    @SerializedName("cca2") val cca2: String,
    @SerializedName("flags") val flags: FlagsDto,
    @SerializedName("region") val region: String
)

data class NameDto(
    @SerializedName("common") val common: String,
    @SerializedName("official") val official: String
)

data class FlagsDto(
    @SerializedName("png") val png: String,
    @SerializedName("svg") val svg: String?
)
