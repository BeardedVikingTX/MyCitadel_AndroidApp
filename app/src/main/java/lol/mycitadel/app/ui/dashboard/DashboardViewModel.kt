package lol.mycitadel.app.ui.dashboard

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
import lol.mycitadel.app.data.network.DashboardResponse
import lol.mycitadel.app.data.repository.DashboardRepository

sealed interface DashboardState {
    data object Loading : DashboardState
    data class Success(
        val data: DashboardResponse,
        val refreshing: Boolean = false,
    ) : DashboardState
    data class Error(
        val code: String,
        val message: String,
    ) : DashboardState
}

class DashboardViewModel(
    private val repo: DashboardRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = DashboardState.Loading
        fetch()
    }

    fun refresh() {
        val current = _state.value
        if (current is DashboardState.Success) {
            _state.value = current.copy(refreshing = true)
        }
        fetch()
    }

    private fun fetch() {
        viewModelScope.launch {
            when (val result = repo.fetch()) {
                is DashboardRepository.Result.Success -> {
                    _state.value = DashboardState.Success(result.data)
                }
                is DashboardRepository.Result.Failure -> {
                    _state.value = DashboardState.Error(
                        code = result.code,
                        message = result.message,
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                DashboardViewModel(app.dashboardRepository)
            }
        }
    }
}