package com.example.vpntestapp.domain.repository

import com.example.vpntestapp.domain.model.VpnStatus
import kotlinx.coroutines.flow.Flow

interface VpnRepository {
    val vpnStatus: Flow<VpnStatus>
    suspend fun connect()
    suspend fun disconnect()
}
