package lol.mycitadel.app.ui.auth

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
import lol.mycitadel.app.data.repository.AuthRepository

data class ForgotPasswordState(
    val email: String = "",
    val submitting: Boolean = false,
    val submitted: Boolean = false,
    val typedEmail: String = "",
    val errorMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = email.isNotBlank() && !submitting && !submitted
}

class ForgotPasswordViewModel(
    private val repo: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    fun onEmailChange(v: String) {
        _state.update { it.copy(email = v, errorMessage = null) }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    /** Reset back to the form state, keeping the previous email in the field. */
    fun tryAgain() {
        _state.update {
            it.copy(
                submitted = false,
                submitting = false,
                typedEmail = "",
                errorMessage = null,
            )
        }
    }

    fun submit() {
        val current = _state.value
        if (current.submitting || current.submitted) return

        val email = current.email.trim()
        if (email.isEmpty()) {
            _state.update { it.copy(errorMessage = "Please enter your email address.") }
            return
        }
        // Simple shape check — the API validates too, but this catches typos
        // before we take a round trip.
        if (!Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(email)) {
            _state.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return
        }

        _state.update { it.copy(submitting = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = repo.requestPasswordReset(email)) {
                is AuthRepository.ResetRequestResult.Success -> {
                    _state.update {
                        it.copy(
                            submitting = false,
                            submitted = true,
                            typedEmail = email,
                            errorMessage = null,
                        )
                    }
                }
                is AuthRepository.ResetRequestResult.Failure -> {
                    // The API is deliberately ambiguous. A rate_limited
                    // response is the only one that deserves real messaging,
                    // and even then we should be gentle about it.
                    val friendly = when (result.code) {
                        "rate_limited"  ->
                            "Too many requests. Please wait a few minutes before trying again."
                        "network_error" ->
                            result.message
                        else ->
                            result.message
                    }
                    _state.update {
                        it.copy(
                            submitting = false,
                            errorMessage = friendly,
                        )
                    }
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                ForgotPasswordViewModel(app.authRepository)
            }
        }
    }
}