package lol.mycitadel.app.ui.users

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
import lol.mycitadel.app.data.network.ProfileFull
import lol.mycitadel.app.data.repository.UsersRepository

data class UserViewState(
    val loading: Boolean = true,
    val profile: ProfileFull? = null,
    val view: String = "full",          // "full" | "limited"
    val notFound: Boolean = false,
    val error: String? = null,
    val actionBusy: Boolean = false,
    val toast: String? = null,
    val toastIsError: Boolean = false,
)

class UserViewModel(
    private val repo: UsersRepository,
    private val userId: Int,
) : ViewModel() {

    private val _state = MutableStateFlow(UserViewState())
    val state: StateFlow<UserViewState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.update { it.copy(loading = true, notFound = false, error = null) }
        viewModelScope.launch {
            when (val r = repo.view(userId)) {
                is UsersRepository.ProfileResult.Success -> {
                    _state.update {
                        it.copy(loading = false, profile = r.profile, view = r.view)
                    }
                }
                UsersRepository.ProfileResult.NotFound -> {
                    _state.update { it.copy(loading = false, notFound = true) }
                }
                is UsersRepository.ProfileResult.Failure -> {
                    _state.update { it.copy(loading = false, error = r.message) }
                }
            }
        }
    }

    /* ── Actions ─────────────────────────────────────────────── */

    fun requestConnection() = runAction("pending_out", "Request sent.") {
        repo.requestConnection(userId)
    }

    fun acceptConnection() = runAction("connected", "Connected. +25 reputation.") {
        repo.acceptConnection(userId)
    }

    fun cancelRequest() = runAndExit("Request cancelled.") {
        repo.blockUser(userId)
    }

    fun denyRequest() = runAndExit("Request denied.") {
        repo.blockUser(userId)
    }

    fun severConnection() = runAndExit("Connection severed.") {
        repo.blockUser(userId)
    }

    fun hideUser() = runAndExit("Hidden.") {
        repo.blockUser(userId)
    }

    /** Update connection state in-place and refresh the profile. */
    private fun runAction(
        newState: String,
        successMsg: String,
        call: suspend () -> UsersRepository.ActionResult,
    ) {
        if (_state.value.actionBusy) return
        _state.update { it.copy(actionBusy = true) }
        viewModelScope.launch {
            when (val r = call()) {
                is UsersRepository.ActionResult.Success -> {
                    _state.update { st ->
                        st.copy(
                            actionBusy = false,
                            toast = successMsg,
                            toastIsError = false,
                            profile = st.profile?.copy(connectionState = newState),
                        )
                    }
                    load()
                }
                is UsersRepository.ActionResult.Failure ->
                    _state.update { it.copy(actionBusy = false, toast = r.message, toastIsError = true) }
            }
        }
    }

    /** Action causes the profile to become unreachable — screen should navigate back. */
    private fun runAndExit(
        successMsg: String,
        call: suspend () -> UsersRepository.ActionResult,
    ) {
        if (_state.value.actionBusy) return
        _state.update { it.copy(actionBusy = true) }
        viewModelScope.launch {
            when (val r = call()) {
                is UsersRepository.ActionResult.Success ->
                    _state.update { it.copy(actionBusy = false, toast = successMsg, toastIsError = false) }
                is UsersRepository.ActionResult.Failure ->
                    _state.update { it.copy(actionBusy = false, toast = r.message, toastIsError = true) }
            }
        }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }

    companion object {
        fun factory(userId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                UserViewModel(app.usersRepository, userId)
            }
        }
    }
}