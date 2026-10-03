package lol.mycitadel.app.ui.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lol.mycitadel.app.MyCitadelApp
import lol.mycitadel.app.data.repository.PremiumRepository

data class PremiumState(
    val loading: Boolean = false,
    val isPremium: Boolean = false,
    val checkoutUrl: String? = null,
    val portalUrl: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

class PremiumViewModel(private val repo: PremiumRepository) : ViewModel() {

    private val _state = MutableStateFlow(PremiumState())
    val state: StateFlow<PremiumState> = _state.asStateFlow()

    init {
        checkStatus()
    }

    fun checkStatus() {
        viewModelScope.launch {
            when (val r = repo.fetchStatus()) {
                is PremiumRepository.StatusResult.Success -> {
                    _state.update { it.copy(isPremium = r.isPremium) }
                }
                is PremiumRepository.StatusResult.Failure -> {
                    // non-critical, status defaults to state
                }
            }
        }
    }

    fun startCheckout() {
        _state.update { it.copy(loading = true, errorMessage = null, checkoutUrl = null) }
        viewModelScope.launch {
            when (val r = repo.createCheckoutSession()) {
                is PremiumRepository.CheckoutResult.Success -> {
                    _state.update { it.copy(loading = false, checkoutUrl = r.checkoutUrl) }
                }
                is PremiumRepository.CheckoutResult.Failure -> {
                    _state.update { it.copy(loading = false, errorMessage = r.message) }
                }
            }
        }
    }

    fun openPortal() {
        _state.update { it.copy(loading = true, errorMessage = null, portalUrl = null) }
        viewModelScope.launch {
            when (val r = repo.openPortalSession()) {
                is PremiumRepository.PortalResult.Success -> {
                    _state.update { it.copy(loading = false, portalUrl = r.portalUrl) }
                }
                is PremiumRepository.PortalResult.Failure -> {
                    _state.update { it.copy(loading = false, errorMessage = r.message) }
                }
            }
        }
    }

    fun clearCheckoutUrl() {
        _state.update { it.copy(checkoutUrl = null) }
    }

    fun clearPortalUrl() {
        _state.update { it.copy(portalUrl = null) }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                PremiumViewModel(app.premiumRepository)
            }
        }
    }
}
