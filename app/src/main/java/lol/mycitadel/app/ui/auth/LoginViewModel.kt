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
import lol.mycitadel.app.data.network.UserDto
import lol.mycitadel.app.data.repository.AuthRepository

enum class LoginStep { Password, TwoFactor }

data class LoginFormState(
    val identifier: String = "",
    val password: String = "",
    val twoFaCode: String = "",
    val step: LoginStep = LoginStep.Password,
    val submitting: Boolean = false,
    val errorMessage: String? = null,
    val twoFaMessage: String? = null,
    val successUser: UserDto? = null,
)

class LoginViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginFormState())
    val state: StateFlow<LoginFormState> = _state.asStateFlow()

    fun onIdentifierChange(v: String) {
        _state.update { it.copy(identifier = v, errorMessage = null) }
    }

    fun onPasswordChange(v: String) {
        _state.update { it.copy(password = v, errorMessage = null) }
    }

    fun onTwoFaCodeChange(v: String) {
        _state.update { it.copy(twoFaCode = v, errorMessage = null) }
    }

    /** Return to password step — call when 2FA pending state expires or user asks. */
    fun backToPassword() {
        _state.update {
            it.copy(
                step = LoginStep.Password,
                twoFaCode = "",
                twoFaMessage = null,
                errorMessage = null,
                submitting = false,
            )
        }
    }

    fun submitPassword() {
        val s = _state.value
        if (s.identifier.isBlank()) {
            _state.update { it.copy(errorMessage = "Please enter your username or email.") }
            return
        }
        if (s.password.isBlank()) {
            _state.update { it.copy(errorMessage = "Please enter your password.") }
            return
        }
        if (s.submitting) return

        _state.update { it.copy(submitting = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = repo.login(s.identifier, s.password)) {
                is AuthRepository.LoginResult.Success -> {
                    _state.update { it.copy(submitting = false, successUser = result.user) }
                }
                is AuthRepository.LoginResult.TwoFactorRequired -> {
                    _state.update {
                        it.copy(
                            submitting = false,
                            step = LoginStep.TwoFactor,
                            twoFaMessage = result.message,
                            password = "", // clear from memory once we're past the password step
                        )
                    }
                }
                is AuthRepository.LoginResult.Failure -> {
                    _state.update {
                        it.copy(
                            submitting = false,
                            errorMessage = friendlyMessage(result.code, result.message),
                        )
                    }
                }
            }
        }
    }

    fun submitTwoFa() {
        val s = _state.value
        if (s.twoFaCode.isBlank()) {
            _state.update { it.copy(errorMessage = "Please enter your code.") }
            return
        }
        if (s.submitting) return

        _state.update { it.copy(submitting = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = repo.login2fa(s.twoFaCode)) {
                is AuthRepository.LoginResult.Success -> {
                    _state.update { it.copy(submitting = false, successUser = result.user) }
                }
                is AuthRepository.LoginResult.TwoFactorRequired -> {
                    // API shouldn't return this on the 2FA endpoint, but guard anyway
                    _state.update {
                        it.copy(submitting = false, errorMessage = "Unexpected response.")
                    }
                }
                is AuthRepository.LoginResult.Failure -> {
                    _state.update {
                        it.copy(
                            submitting = false,
                            errorMessage = friendlyMessage(result.code, result.message),
                        )
                    }
                    // Session expired — bounce back to password step
                    if (result.code in setOf("no_pending", "expired", "too_many_attempts")) {
                        kotlinx.coroutines.delay(1800)
                        backToPassword()
                    }
                }
            }
        }
    }

    private fun friendlyMessage(code: String, fallback: String): String = when (code) {
        "missing_credentials" -> "Please enter your username/email and password."
        "invalid_credentials" -> "Invalid credentials. Please check and try again."
        "rate_limited"        -> "Too many attempts. Please wait a few minutes and try again."
        "csrf_invalid"        -> "Session expired. Please try again."
        "csrf_failed"         -> fallback
        "origin_required"     -> "Request blocked by the API gate. Please try again."
        "network_error"       -> fallback
        "invalid_code"        -> fallback
        "no_pending"          -> "Session expired. Please log in again."
        "expired"             -> "Session expired. Please log in again."
        "too_many_attempts"   -> "Too many wrong codes. Please log in again."
        "malformed"           -> fallback
        else                  -> fallback
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                LoginViewModel(app.authRepository)
            }
        }
    }
}