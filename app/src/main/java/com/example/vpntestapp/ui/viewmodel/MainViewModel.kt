package com.example.vpntestapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpntestapp.domain.model.Country
import com.example.vpntestapp.domain.model.VpnServer
import com.example.vpntestapp.domain.model.VpnStatus
import com.example.vpntestapp.domain.usecase.ConnectVpnUseCase
import com.example.vpntestapp.domain.usecase.DisconnectVpnUseCase
import com.example.vpntestapp.domain.usecase.GetCountriesUseCase
import com.example.vpntestapp.domain.usecase.GetVpnStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val vpnStatus: VpnStatus = VpnStatus.DISCONNECTED,
    val selectedServer: VpnServer = DEFAULT_SERVERS.first(),
    val countries: List<Country> = emptyList(),
    val isLoadingCountries: Boolean = false,
    val countriesError: String? = null,
    val isServerPickerVisible: Boolean = false
)

val DEFAULT_SERVERS = listOf(
    VpnServer("de", "Germany", "Germany", "DE", "https://flagcdn.com/w80/de.png"),
    VpnServer("us", "United States", "United States", "US", "https://flagcdn.com/w80/us.png"),
    VpnServer("jp", "Japan", "Japan", "JP", "https://flagcdn.com/w80/jp.png"),
    VpnServer("nl", "Netherlands", "Netherlands", "NL", "https://flagcdn.com/w80/nl.png"),
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCountriesUseCase: GetCountriesUseCase,
    private val connectVpnUseCase: ConnectVpnUseCase,
    private val disconnectVpnUseCase: DisconnectVpnUseCase,
    private val getVpnStatusUseCase: GetVpnStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeVpnStatus()
        loadCountries()
    }

    private fun observeVpnStatus() {
        viewModelScope.launch {
            getVpnStatusUseCase().collect { status ->
                _uiState.update { it.copy(vpnStatus = status) }
            }
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCountries = true, countriesError = null) }
            getCountriesUseCase().collect { result ->
                result.fold(
                    onSuccess = { countries ->
                        _uiState.update {
                            it.copy(
                                countries = countries,
                                isLoadingCountries = false,
                                countriesError = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoadingCountries = false,
                                countriesError = error.message ?: "Failed to load countries"
                            )
                        }
                    }
                )
            }
        }
    }

    fun onConnectToggle() {
        viewModelScope.launch {
            when (_uiState.value.vpnStatus) {
                VpnStatus.DISCONNECTED -> connectVpnUseCase()
                VpnStatus.CONNECTED -> disconnectVpnUseCase()
                VpnStatus.CONNECTING -> Unit
            }
        }
    }

    fun onServerSelected(server: VpnServer) {
        _uiState.update { it.copy(selectedServer = server, isServerPickerVisible = false) }
    }

    fun onServerFromCountry(country: Country) {
        val server = VpnServer(
            id = country.code.lowercase(),
            name = country.name,
            country = country.name,
            countryCode = country.code,
            flagUrl = country.flagUrl
        )
        _uiState.update { it.copy(selectedServer = server, isServerPickerVisible = false) }
    }

    fun toggleServerPicker() {
        _uiState.update { it.copy(isServerPickerVisible = !it.isServerPickerVisible) }
    }

    fun retryLoadCountries() {
        loadCountries()
    }
}
