package com.example.vpntestapp.data.repository

import com.example.vpntestapp.domain.model.VpnStatus
import com.example.vpntestapp.domain.repository.VpnRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VpnRepositoryImpl @Inject constructor() : VpnRepository {

    private val _vpnStatus = MutableStateFlow(VpnStatus.DISCONNECTED)
    override val vpnStatus: Flow<VpnStatus> = _vpnStatus

    override suspend fun connect() {
        if (_vpnStatus.value != VpnStatus.DISCONNECTED) return
        _vpnStatus.value = VpnStatus.CONNECTING
        delay(2000L)
        _vpnStatus.value = VpnStatus.CONNECTED
    }

    override suspend fun disconnect() {
        if (_vpnStatus.value != VpnStatus.CONNECTED) return
        _vpnStatus.value = VpnStatus.DISCONNECTED
    }
}
