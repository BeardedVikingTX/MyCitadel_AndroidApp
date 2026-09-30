package lol.mycitadel.app.ui.users

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
import lol.mycitadel.app.MyCitadelApp
import lol.mycitadel.app.data.network.UserDto
import lol.mycitadel.app.data.network.UserSummary
import lol.mycitadel.app.data.repository.UsersRepository
import lol.mycitadel.app.data.repository.MessagesRepository

enum class DirectoryFilter(val label: String) {
    All("All"),
    NotConnected("Not Connected"),
}

data class UsersState(
    val loading: Boolean = true,
    val users: List<UserSummary> = emptyList(),
    val self: UserDto? = null,          // prepended "YOU" card
    val query: String = "",
    val filter: DirectoryFilter = DirectoryFilter.All,
    val total: Int = 0,
    val hasMore: Boolean = false,
    val loadingMore: Boolean = false,
    val fatalError: String? = null,
    val toast: String? = null,
    val toastIsError: Boolean = false,
    val openingChatFor: Set<Int> = emptySet(),
)

class UsersViewModel(
    private val repo: UsersRepository,
    private val messagesRepo: MessagesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UsersState())
    val state: StateFlow<UsersState> = _state.asStateFlow()

    private var searchJob: Job? = null

    /** Called once by the screen with the current session user. */
    fun setSelf(user: UserDto?) {
        _state.update { it.copy(self = user) }
    }

    init {
        load(reset = true)
    }

    /* ── Search + filter ─────────────────────────────────────── */

    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(320)
            load(reset = true)
        }
    }

    fun onFilterChange(filter: DirectoryFilter) {
        if (_state.value.filter == filter) return
        _state.update { it.copy(filter = filter) }
        load(reset = true)
    }

    fun refresh() = load(reset = true)

    fun loadMore() {
        if (_state.value.loadingMore || !_state.value.hasMore) return
        load(reset = false)
    }

    /* ── Load ────────────────────────────────────────────────── */

    private fun load(reset: Boolean) {
        if (reset) {
            _state.update { it.copy(loading = true, fatalError = null, users = emptyList()) }
        } else {
            _state.update { it.copy(loadingMore = true) }
        }

        viewModelScope.launch {
            val s = _state.value
            val result = repo.list(
                query = s.query,
                offset = if (reset) 0 else s.users.size,
                excludeConnected = s.filter == DirectoryFilter.NotConnected,
            )

            when (result) {
                is UsersRepository.ListResult.Success -> {
                    _state.update { st ->
                        val merged = if (reset) result.users else st.users + result.users
                        st.copy(
                            loading = false,
                            loadingMore = false,
                            users = merged,
                            total = result.total,
                            hasMore = result.hasMore,
                            fatalError = null,
                        )
                    }
                }

                is UsersRepository.ListResult.Failure -> {
                    _state.update { st ->
                        if (reset) st.copy(
                            loading = false,
                            loadingMore = false,
                            fatalError = result.message
                        )
                        else st.copy(
                            loadingMore = false,
                            toast = result.message,
                            toastIsError = true
                        )
                    }
                }
            }
        }
    }

    /* ── Inline card actions ─────────────────────────────────── */

    fun requestConnection(user: UserSummary) = updateState(user.id, "pending_out") {
        repo.requestConnection(user.id)
    }

    fun acceptConnection(user: UserSummary) = updateState(user.id, "connected") {
        repo.acceptConnection(user.id)
    }

    fun cancelRequest(user: UserSummary) = removeUser(user.id) {
        repo.blockUser(user.id)
    }

    fun denyRequest(user: UserSummary) = removeUser(user.id) {
        repo.blockUser(user.id)
    }

    fun severConnection(user: UserSummary) = removeUser(user.id) {
        repo.blockUser(user.id)
    }

    fun hideUser(user: UserSummary) = removeUser(user.id) {
        repo.blockUser(user.id)
    }

    fun startConversation(targetUserId: Int, onOpen: (Long) -> Unit) {
        if (targetUserId in _state.value.openingChatFor) return
        _state.update { it.copy(openingChatFor = it.openingChatFor + targetUserId) }

        viewModelScope.launch {
            when (val r = messagesRepo.openConversation(targetUserId)) {
                is MessagesRepository.Result.Success -> {
                    _state.update { it.copy(openingChatFor = it.openingChatFor - targetUserId) }
                    onOpen(r.data)
                }

                is MessagesRepository.Result.Failure -> {
                    _state.update {
                        it.copy(
                            openingChatFor = it.openingChatFor - targetUserId,
                            toast = r.message,
                            toastIsError = true,
                        )
                    }
                }
            }
        }
    }

    private fun updateState(
        userId: Int,
        newState: String,
        call: suspend () -> UsersRepository.ActionResult,
    ) {
        viewModelScope.launch {
            when (val r = call()) {
                is UsersRepository.ActionResult.Success -> {
                    _state.update { st ->
                        st.copy(
                            users = st.users.map { u ->
                                if (u.id == userId) u.copy(connectionState = newState) else u
                            },
                            toast = toastForAction(newState),
                            toastIsError = false,
                        )
                    }
                }

                is UsersRepository.ActionResult.Failure ->
                    _state.update { it.copy(toast = r.message, toastIsError = true) }
            }
        }
    }

    private fun removeUser(
        userId: Int,
        call: suspend () -> UsersRepository.ActionResult,
    ) {
        viewModelScope.launch {
            when (val r = call()) {
                is UsersRepository.ActionResult.Success -> {
                    _state.update { st ->
                        st.copy(
                            users = st.users.filterNot { it.id == userId },
                            toast = "Updated.",
                            toastIsError = false,
                        )
                    }
                }

                is UsersRepository.ActionResult.Failure ->
                    _state.update { it.copy(toast = r.message, toastIsError = true) }
            }
        }
    }

    private fun toastForAction(state: String) = when (state) {
        "connected" -> "Connected. +25 reputation."
        "pending_out" -> "Request sent."
        else -> "Updated."
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                UsersViewModel(
                    app.usersRepository,
                    app.messagesRepository,
                )
            }
        }
    }
}
