package lol.mycitadel.app.ui.notifications

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
import lol.mycitadel.app.data.network.NotificationDto
import lol.mycitadel.app.data.repository.NotificationsRepository
import lol.mycitadel.app.data.repository.UsersRepository

enum class NotifTab(val key: String, val label: String) {
    All("all", "All"),
    Unread("unread", "Unread"),
}

data class NotificationsState(
    val loading: Boolean = true,
    val notifications: List<NotificationDto> = emptyList(),
    val unreadCount: Int = 0,
    val tab: NotifTab = NotifTab.All,
    val fatalError: String? = null,
    val toast: String? = null,
    val toastIsError: Boolean = false,
    val busyIds: Set<Int> = emptySet(),
)

class NotificationsViewModel(
    private val notifRepo: NotificationsRepository,
    private val usersRepo: UsersRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    init { load() }

    /* ── Load ─────────────────────────────────────────────────── */

    fun load() {
        _state.update { it.copy(loading = true, fatalError = null) }
        viewModelScope.launch {
            val unreadOnly = _state.value.tab == NotifTab.Unread
            when (val r = notifRepo.list(unreadOnly = unreadOnly)) {
                is NotificationsRepository.ListResult.Success -> {
                    _state.update {
                        it.copy(
                            loading = false,
                            notifications = r.notifications,
                            unreadCount = r.unreadCount,
                            fatalError = null,
                        )
                    }
                }
                is NotificationsRepository.ListResult.Failure -> {
                    _state.update {
                        it.copy(loading = false, fatalError = r.message)
                    }
                }
            }
        }
    }

    fun switchTab(tab: NotifTab) {
        if (_state.value.tab == tab) return
        _state.update { it.copy(tab = tab) }
        load()
    }

    /* ── Mark read ────────────────────────────────────────────── */

    fun markRead(id: Int) {
        val n = _state.value.notifications.firstOrNull { it.id == id } ?: return
        if (n.read) return

        _state.update { st ->
            st.copy(
                notifications = st.notifications.map {
                    if (it.id == id) it.copy(read = true) else it
                },
                unreadCount = (st.unreadCount - 1).coerceAtLeast(0),
            )
        }

        viewModelScope.launch {
            if (notifRepo.markRead(id) is NotificationsRepository.ActionResult.Failure) {
                // Roll back on failure
                _state.update { st ->
                    st.copy(
                        notifications = st.notifications.map {
                            if (it.id == id) it.copy(read = false) else it
                        },
                        unreadCount = st.unreadCount + 1,
                    )
                }
            }
        }
    }

    fun markAllRead() {
        if (_state.value.unreadCount == 0) return
        val previousUnread = _state.value.unreadCount

        _state.update { st ->
            st.copy(
                notifications = st.notifications.map { it.copy(read = true) },
                unreadCount = 0,
            )
        }

        viewModelScope.launch {
            when (notifRepo.markAllRead()) {
                is NotificationsRepository.ActionResult.Success -> {
                    showToast("All marked as read.", isError = false)
                }
                is NotificationsRepository.ActionResult.Failure -> {
                    _state.update { st ->
                        st.copy(
                            unreadCount = previousUnread,
                            notifications = st.notifications.map { it.copy(read = false) },
                            toast = "Could not mark all as read.",
                            toastIsError = true,
                        )
                    }
                }
            }
        }
    }

    /* ── Accept / Deny connection requests ────────────────────── */

    fun acceptConnection(notificationId: Int, actorId: Int, actorName: String) {
        if (notificationId in _state.value.busyIds) return
        _state.update { it.copy(busyIds = it.busyIds + notificationId) }

        viewModelScope.launch {
            when (val r = usersRepo.acceptConnection(actorId)) {
                is UsersRepository.ActionResult.Success -> {
                    // Try to also delete the notification server-side
                    notifRepo.delete(notificationId)
                    // Remove from local list
                    _state.update { st ->
                        val wasUnread = st.notifications
                            .firstOrNull { it.id == notificationId }?.read == false
                        st.copy(
                            notifications = st.notifications.filterNot { it.id == notificationId },
                            busyIds = st.busyIds - notificationId,
                            unreadCount = if (wasUnread)
                                (st.unreadCount - 1).coerceAtLeast(0)
                            else st.unreadCount,
                        )
                    }
                    showToast("Connected with $actorName. +25 rep.", isError = false)
                }
                is UsersRepository.ActionResult.Failure -> {
                    _state.update { it.copy(busyIds = it.busyIds - notificationId) }
                    showToast(r.message, isError = true)
                }
            }
        }
    }

    fun denyConnection(notificationId: Int, actorId: Int, actorName: String) {
        if (notificationId in _state.value.busyIds) return
        _state.update { it.copy(busyIds = it.busyIds + notificationId) }

        viewModelScope.launch {
            when (val r = usersRepo.blockUser(actorId)) {
                is UsersRepository.ActionResult.Success -> {
                    notifRepo.delete(notificationId)
                    _state.update { st ->
                        val wasUnread = st.notifications
                            .firstOrNull { it.id == notificationId }?.read == false
                        st.copy(
                            notifications = st.notifications.filterNot { it.id == notificationId },
                            busyIds = st.busyIds - notificationId,
                            unreadCount = if (wasUnread)
                                (st.unreadCount - 1).coerceAtLeast(0)
                            else st.unreadCount,
                        )
                    }
                    showToast("Request denied.", isError = false)
                }
                is UsersRepository.ActionResult.Failure -> {
                    _state.update { it.copy(busyIds = it.busyIds - notificationId) }
                    showToast(r.message, isError = true)
                }
            }
        }
    }

    /* ── Toast ────────────────────────────────────────────────── */

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }

    private fun showToast(msg: String, isError: Boolean) {
        _state.update { it.copy(toast = msg, toastIsError = isError) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                NotificationsViewModel(
                    app.notificationsRepository,
                    app.usersRepository,
                )
            }
        }
    }
}