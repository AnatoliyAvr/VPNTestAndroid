package com.example.vpntestapp.domain.usecase

import com.example.vpntestapp.domain.model.Country
import com.example.vpntestapp.domain.model.VpnStatus
import com.example.vpntestapp.domain.repository.CountryRepository
import com.example.vpntestapp.domain.repository.VpnRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    operator fun invoke(): Flow<Result<List<Country>>> = repository.getCountries()
}

class ConnectVpnUseCase @Inject constructor(
    private val repository: VpnRepository
) {
    suspend operator fun invoke() = repository.connect()
}

class DisconnectVpnUseCase @Inject constructor(
    private val repository: VpnRepository
) {
    suspend operator fun invoke() = repository.disconnect()
}

class GetVpnStatusUseCase @Inject constructor(
    private val repository: VpnRepository
) {
    operator fun invoke(): Flow<VpnStatus> = repository.vpnStatus
}
