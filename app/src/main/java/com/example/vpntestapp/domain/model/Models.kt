package com.example.vpntestapp.domain.model

data class Country(
    val name: String,
    val code: String,
    val flagUrl: String,
    val region: String
)

enum class VpnStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

data class VpnServer(
    val id: String,
    val name: String,
    val country: String,
    val countryCode: String,
    val flagUrl: String,
    val isPremium: Boolean = false,
    val ping: Int? = null
)
