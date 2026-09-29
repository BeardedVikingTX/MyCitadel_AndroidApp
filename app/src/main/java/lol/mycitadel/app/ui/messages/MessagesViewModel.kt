package lol.mycitadel.app.ui.messages

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
import lol.mycitadel.app.data.network.ConversationDto
import lol.mycitadel.app.data.repository.MessagesRepository

data class MessagesState(
    val loading: Boolean = true,
    val conversations: List<ConversationDto> = emptyList(),
    val fatalError: String? = null,
    val toast: String? = null,
    val toastIsError: Boolean = false,
)

class MessagesViewModel(private val repo: MessagesRepository) : ViewModel() {

    private val _state = MutableStateFlow(MessagesState())
    val state: StateFlow<MessagesState> = _state.asStateFlow()

    private var refreshJob: Job? = null

    init {
        load()
        startPeriodicRefresh()
    }

    fun load() {
        _state.update { it.copy(loading = true, fatalError = null) }
        viewModelScope.launch {
            when (val r = repo.listConversations()) {
                is MessagesRepository.Result.Success -> {
                    _state.update {
                        it.copy(loading = false, conversations = r.data, fatalError = null)
                    }
                }
                is MessagesRepository.Result.Failure -> {
                    _state.update {
                        it.copy(loading = false, fatalError = r.message)
                    }
                }
            }
        }
    }

    /** Refresh the inbox every 10 seconds while the screen is open. */
    private fun startPeriodicRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                delay(10_000)
                when (val r = repo.listConversations()) {
                    is MessagesRepository.Result.Success -> {
                        _state.update { it.copy(conversations = r.data) }
                    }
                    else -> { /* silent */ }
                }
            }
        }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                MessagesViewModel(app.messagesRepository)
            }
        }
    }
}