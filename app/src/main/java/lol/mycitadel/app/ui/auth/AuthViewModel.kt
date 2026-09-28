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

data class RegisterFormState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val referralCode: String = "",
    val acceptedTerms: Boolean = false,
    val acceptedAge: Boolean = false,
    val submitting: Boolean = false,
    val errorMessage: String? = null,
    val successUser: UserDto? = null
) {
    val passwordStrength: Int
        get() {
            if (password.isEmpty()) return 0
            var s = 0
            if (password.length >= 12) s += 1
            if (password.length >= 16) s += 1
            if (password.length >= 20) s += 1
            if (password.any { it.isLowerCase() } && password.any { it.isUpperCase() }) s += 1
            if (password.any { it.isDigit() }) s += 1
            if (password.any { !it.isLetterOrDigit() }) s += 1
            return s.coerceAtMost(6)
        }
}

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _form = MutableStateFlow(RegisterFormState())
    val form: StateFlow<RegisterFormState> = _form.asStateFlow()

    fun onUsernameChange(v: String)        { _form.update { it.copy(username = v, errorMessage = null) } }
    fun onEmailChange(v: String)           { _form.update { it.copy(email = v, errorMessage = null) } }
    fun onPasswordChange(v: String)        { _form.update { it.copy(password = v, errorMessage = null) } }
    fun onPasswordConfirmChange(v: String) { _form.update { it.copy(passwordConfirm = v, errorMessage = null) } }
    fun onReferralChange(v: String)        { _form.update { it.copy(referralCode = v, errorMessage = null) } }
    fun onTermsChange(v: Boolean)          { _form.update { it.copy(acceptedTerms = v, errorMessage = null) } }
    fun onAgeChange(v: Boolean)            { _form.update { it.copy(acceptedAge = v, errorMessage = null) } }
    fun clearError()                       { _form.update { it.copy(errorMessage = null) } }

    fun submit() {
        val s = _form.value

        val localError: String? = when {
            !Regex("^[a-zA-Z0-9_]{3,32}$").matches(s.username.trim()) ->
                "Username must be 3–32 characters: letters, numbers, underscores only."
            !Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(s.email.trim()) ->
                "Please enter a valid email address."
            s.password.length < 12 ->
                "Password must be at least 12 characters."
            s.password != s.passwordConfirm ->
                "Passwords do not match."
            !s.acceptedTerms ->
                "You must accept the Terms of Service to register."
            !s.acceptedAge ->
                "You must confirm you are at least 13 years old."
            else -> null
        }
        if (localError != null) {
            _form.update { it.copy(errorMessage = localError) }
            return
        }

        _form.update { it.copy(submitting = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repo.register(
                username = s.username,
                email = s.email,
                password = s.password,
                referralCode = s.referralCode.takeIf { r -> r.isNotBlank() }
            )

            when (result) {
                is AuthRepository.RegisterResult.Success -> {
                    _form.update {
                        it.copy(submitting = false, successUser = result.user, errorMessage = null)
                    }
                }
                is AuthRepository.RegisterResult.Failure -> {
                    _form.update {
                        it.copy(
                            submitting = false,
                            errorMessage = friendlyMessage(result.code, result.message)
                        )
                    }
                }
            }
        }
    }

    private fun friendlyMessage(code: String, fallback: String): String = when (code) {
        "user_exists"        -> "That username or email is already registered. Try logging in instead."
        "invalid_username"   -> "Username must be 3–32 characters: letters, numbers, underscores only."
        "reserved_username"  -> "That username is reserved. Please choose another."
        "invalid_email"      -> "Please enter a valid email address."
        "weak_password"      -> fallback
        "rate_limited"       -> "Too many attempts. Try again in an hour."
        "csrf_invalid"       -> "Session expired. Please try again."
        "origin_required"    -> "Request blocked by the API gate. Please try again."
        "registration_failed"-> "We could not create your account. Please try again in a moment."
        "network_error"      -> fallback
        "csrf_failed"        -> fallback
        else                 -> fallback
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                AuthViewModel(app.authRepository)
            }
        }
    }
}