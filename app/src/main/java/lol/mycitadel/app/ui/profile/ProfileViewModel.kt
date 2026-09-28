package lol.mycitadel.app.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import lol.mycitadel.app.MyCitadelApp
import lol.mycitadel.app.data.network.AccountUpdateRequest
import lol.mycitadel.app.data.network.ProfileOptionsDto
import lol.mycitadel.app.data.repository.ProfileRepository
import kotlinx.serialization.json.contentOrNull

/* ── Sections ─────────────────────────────────────────────── */

enum class ProfileSection(val label: String) {
    Identity("Identity"),
    Images("Images"),
    Location("Location"),
    Personal("Personal"),
    Work("Work"),
    Life("Life"),
    Interests("Interests"),
    Favorites("Favorites"),
    Links("Links"),
    Theme("Theme"),
    Privacy("Privacy"),
    Account("Account"),
}

/* ── State ────────────────────────────────────────────────── */

enum class SaveStatus { Idle, Saving, Saved, Error }

data class ProfileEditState(
    val loading: Boolean = true,
    val profile: JsonObject = JsonObject(emptyMap()),
    val options: ProfileOptionsDto = ProfileOptionsDto(),
    val activeSection: ProfileSection = ProfileSection.Identity,
    val dirty: Set<String> = emptySet(),
    val saveStatus: SaveStatus = SaveStatus.Idle,
    val errorMessage: String? = null,
    val uploadingKind: String? = null,
    val loadError: String? = null,
)

class ProfileViewModel(
    private val repo: ProfileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileEditState())
    val state: StateFlow<ProfileEditState> = _state.asStateFlow()

    private var saveJob: Job? = null
    private var statusJob: Job? = null

    init { load() }

    fun load() {
        _state.update { it.copy(loading = true, loadError = null) }
        viewModelScope.launch {
            when (val result = repo.fetch()) {
                is ProfileRepository.FetchResult.Success -> {
                    _state.update {
                        it.copy(
                            loading = false,
                            profile = result.profile,
                            options = result.options,
                            dirty = emptySet(),
                        )
                    }
                }
                is ProfileRepository.FetchResult.Failure -> {
                    _state.update {
                        it.copy(
                            loading = false,
                            loadError = result.message,
                        )
                    }
                }
            }
        }
    }

    fun selectSection(section: ProfileSection) {
        _state.update { it.copy(activeSection = section) }
    }

    /* ── Field edit ─────────────────────────────────────────── */

    fun onFieldChange(field: String, value: Any?) {
        val newElement = when (value) {
            null          -> JsonNull
            is String     -> JsonPrimitive(value)
            is Int        -> JsonPrimitive(value)
            is Long       -> JsonPrimitive(value)
            is Boolean    -> JsonPrimitive(value)
            is List<*>    -> JsonArray(value.map { v ->
                if (v == null) JsonNull else JsonPrimitive(v.toString())
            })
            else          -> JsonPrimitive(value.toString())
        }

        _state.update { st ->
            val newProfile = JsonObject(st.profile.toMutableMap().apply {
                this[field] = newElement
            })
            st.copy(
                profile = newProfile,
                dirty = st.dirty + field,
                saveStatus = SaveStatus.Idle,
            )
        }
        scheduleSave()
    }

    /** Toggle a looking_for chip. */
    fun toggleLookingFor(value: String) {
        val current = (_state.value.profile["looking_for"] as? JsonArray)
            ?.mapNotNull { it.jsonPrimitive.contentOrNull } ?: emptyList()
        val next = if (value in current) current - value else current + value
        onFieldChange("looking_for", next)
    }

    /* ── Images ─────────────────────────────────────────────── */

    fun uploadImage(kind: String, uri: Uri) {
        _state.update { it.copy(uploadingKind = kind, errorMessage = null) }
        viewModelScope.launch {
            when (val result = repo.uploadImage(kind, uri)) {
                is ProfileRepository.UploadResult.Success -> {
                    val field = "${kind}_url"
                    _state.update { st ->
                        val newProfile = JsonObject(
                            st.profile.toMutableMap().apply {
                                this[field] = JsonPrimitive(result.url)
                            }
                        )
                        st.copy(profile = newProfile, uploadingKind = null)
                    }
                    showStatus(SaveStatus.Saved)
                }
                is ProfileRepository.UploadResult.Failure -> {
                    _state.update {
                        it.copy(
                            uploadingKind = null,
                            errorMessage = friendlyMessage(result.code, result.message),
                            saveStatus = SaveStatus.Error,
                        )
                    }
                    clearStatusSoon()
                }
            }
        }
    }

    /* ── Autosave ───────────────────────────────────────────── */

    private fun scheduleSave(delayMs: Long = 1200) {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(delayMs)
            flushSave()
        }
    }

    fun saveNow() {
        saveJob?.cancel()
        viewModelScope.launch { flushSave() }
    }

    private suspend fun flushSave() {
        val st = _state.value
        if (st.dirty.isEmpty()) return

        // Privacy section uses a different endpoint
        val isPrivacy = st.dirty.all {
            it == "visibility" || it.startsWith("show_")
        }

        val payload = buildJsonObject {
            st.dirty.forEach { field ->
                st.profile[field]?.let { put(field, it) }
            }
        }

        _state.update { it.copy(saveStatus = SaveStatus.Saving, errorMessage = null) }

        val result = if (isPrivacy) repo.saveSettings(payload) else repo.update(payload)

        when (result) {
            is ProfileRepository.SaveResult.Success -> {
                _state.update {
                    it.copy(
                        dirty = it.dirty - st.dirty,
                        saveStatus = SaveStatus.Saved,
                    )
                }
                clearStatusSoon()
            }
            is ProfileRepository.SaveResult.Failure -> {
                _state.update {
                    it.copy(
                        saveStatus = SaveStatus.Error,
                        errorMessage = friendlyMessage(result.code, result.message),
                    )
                }
                clearStatusSoon(2600)
            }
        }
    }

    private fun showStatus(status: SaveStatus, duration: Long = 1400) {
        _state.update { it.copy(saveStatus = status) }
        clearStatusSoon(duration)
    }

    private fun clearStatusSoon(delayMs: Long = 1400) {
        statusJob?.cancel()
        statusJob = viewModelScope.launch {
            delay(delayMs)
            _state.update { it.copy(saveStatus = SaveStatus.Idle) }
        }
    }

    /* ── Account updates ────────────────────────────────────── */

    fun updateAccount(
        username: String? = null,
        email: String? = null,
        newPassword: String? = null,
        currentPassword: String? = null,
    ) {
        viewModelScope.launch {
            _state.update { it.copy(saveStatus = SaveStatus.Saving, errorMessage = null) }
            val result = repo.updateAccount(
                AccountUpdateRequest(
                    username = username?.takeIf { it.isNotBlank() },
                    email = email?.takeIf { it.isNotBlank() },
                    newPassword = newPassword?.takeIf { it.isNotBlank() },
                    currentPassword = currentPassword?.takeIf { it.isNotBlank() },
                )
            )
            when (result) {
                is ProfileRepository.SaveResult.Success -> {
                    // Reload to reflect the change
                    load()
                    showStatus(SaveStatus.Saved, 2200)
                }
                is ProfileRepository.SaveResult.Failure -> {
                    _state.update {
                        it.copy(
                            saveStatus = SaveStatus.Error,
                            errorMessage = friendlyMessage(result.code, result.message),
                        )
                    }
                    clearStatusSoon(3200)
                }
            }
        }
    }

    /* ── Error copy ─────────────────────────────────────────── */

    private fun friendlyMessage(code: String, fallback: String): String = when (code) {
        "no_changes"          -> "Nothing to save."
        "invalid_field"       -> fallback
        "invalid_visibility"  -> "Invalid visibility value."
        "username_taken"      -> "That username is already in use."
        "email_taken"         -> "That email is already registered."
        "invalid_current_password" -> "Your current password is incorrect."
        "current_password_required" -> "Enter your current password first."
        "weak_password"       -> fallback
        "rate_limited"        -> "Too many attempts. Please wait."
        "csrf_invalid"        -> "Session expired. Please try again."
        "network_error"       -> fallback
        "read_failed"         -> "Could not read the selected image."
        else                  -> fallback
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                ProfileViewModel(app.profileRepository)
            }
        }
    }
}

/* ── Helpers for reading JsonObject values in the UI ───────── */

fun JsonObject.stringField(key: String): String =
    this[key]?.jsonPrimitive?.contentOrNull ?: ""

fun JsonObject.boolField(key: String, default: Boolean = false): Boolean =
    this[key]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull() ?: default

fun JsonObject.intField(key: String, default: Int = 0): Int =
    this[key]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: default

fun JsonObject.stringListField(key: String): List<String> =
    (this[key] as? JsonArray)?.mapNotNull { it.jsonPrimitive.contentOrNull } ?: emptyList()