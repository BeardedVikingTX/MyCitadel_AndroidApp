package lol.mycitadel.app.ui.messages

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lol.mycitadel.app.MyCitadelApp
import lol.mycitadel.app.data.network.MessageAttachmentDto
import lol.mycitadel.app.data.network.MessageDto
import lol.mycitadel.app.data.network.OtherUserDto
import lol.mycitadel.app.data.repository.MessagesRepository
import java.util.UUID

data class ChatState(
    val loading: Boolean = true,
    val conversationId: Long,
    val other: OtherUserDto? = null,
    val messages: List<MessageDto> = emptyList(),
    val composerText: String = "",
    val pendingAttachments: List<MessageAttachmentDto> = emptyList(),
    val uploading: Int = 0,
    val sending: Boolean = false,
    val fatalError: String? = null,
    val toast: String? = null,
    val toastIsError: Boolean = false,
) {
    val canSend: Boolean
        get() = (composerText.isNotBlank() || pendingAttachments.isNotEmpty())
                && !sending && uploading == 0
}

class ChatViewModel(
    private val repo: MessagesRepository,
    private val conversationId: Long,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState(conversationId = conversationId))
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private var streamJob: Job? = null
    private var lastSeenId: Long = 0

    init {
        loadThread()
        startStream()
    }

    /* ── Load ───────────────────────────────────────────────── */

    private fun loadThread() {
        _state.update { it.copy(loading = true, fatalError = null) }
        viewModelScope.launch {
            when (val r = repo.loadThread(conversationId)) {
                is MessagesRepository.Result.Success -> {
                    val msgs = r.data.messages
                    val lastId = msgs.lastOrNull()?.id ?: 0L
                    lastSeenId = maxOf(lastSeenId, lastId)
                    _state.update {
                        it.copy(
                            loading = false,
                            other = r.data.other,
                            messages = msgs,
                            fatalError = null,
                        )
                    }
                    if (lastId > 0) markRead(lastId)
                }
                is MessagesRepository.Result.Failure -> {
                    _state.update {
                        it.copy(loading = false, fatalError = r.message)
                    }
                }
            }
        }
    }

    /* ── Stream (long-poll) ─────────────────────────────────── */

    private fun startStream() {
        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            while (isActive) {
                val result = repo.stream(lastSeenId)
                when (result) {
                    is MessagesRepository.Result.Success -> {
                        val newMsgs = result.data.messages
                        if (newMsgs.isNotEmpty()) {
                            lastSeenId = maxOf(lastSeenId, result.data.lastId)
                            _state.update { st ->
                                val existingIds = st.messages.map { it.id }.toSet()
                                val filtered = newMsgs.filter { it.id !in existingIds }
                                if (filtered.isEmpty()) st
                                else st.copy(messages = st.messages + filtered)
                            }
                            markRead(lastSeenId)
                        }
                    }
                    is MessagesRepository.Result.Failure -> {
                        // Back off before retrying
                        delay(3000)
                    }
                }
            }
        }
    }

    /* ── Composer ───────────────────────────────────────────── */

    fun onTextChange(text: String) {
        _state.update { it.copy(composerText = text) }
    }

    fun addAttachment(uri: Uri) {
        if (_state.value.pendingAttachments.size >= 6) {
            showToast("Maximum 6 attachments per message.", true)
            return
        }
        _state.update { it.copy(uploading = it.uploading + 1) }
        viewModelScope.launch {
            when (val r = repo.uploadAttachment(uri)) {
                is MessagesRepository.Result.Success -> {
                    _state.update { st ->
                        st.copy(
                            uploading = (st.uploading - 1).coerceAtLeast(0),
                            pendingAttachments = st.pendingAttachments + r.data,
                        )
                    }
                }
                is MessagesRepository.Result.Failure -> {
                    _state.update { st ->
                        st.copy(
                            uploading = (st.uploading - 1).coerceAtLeast(0),
                            toast = r.message,
                            toastIsError = true,
                        )
                    }
                }
            }
        }
    }

    fun removeAttachment(index: Int) {
        _state.update { st ->
            val list = st.pendingAttachments.toMutableList()
            if (index in list.indices) list.removeAt(index)
            st.copy(pendingAttachments = list)
        }
    }

    /* ── Send ───────────────────────────────────────────────── */

    fun send(currentUserId: Int) {
        val s = _state.value
        if (!s.canSend) return

        val text = s.composerText.trim()
        val atts = s.pendingAttachments
        val idem = UUID.randomUUID().toString().replace("-", "")
        val tempId = -(System.currentTimeMillis())

        val optimistic = MessageDto(
            id = tempId,
            conversationId = conversationId,
            senderId = currentUserId,
            body = text,
            createdAt = java.time.Instant.now().toString(),
            attachments = atts.map {
                MessageAttachmentDto(
                    token = it.token, url = it.url, kind = it.kind,
                    mime = it.mime, name = it.name, size = it.size,
                    width = it.width, height = it.height,
                )
            },
        )

        _state.update { st ->
            st.copy(
                messages = st.messages + optimistic,
                composerText = "",
                pendingAttachments = emptyList(),
                sending = true,
            )
        }

        viewModelScope.launch {
            when (val r = repo.send(conversationId, text, atts.map { it.token }, idem)) {
                is MessagesRepository.Result.Success -> {
                    val realId = r.data.messageId
                    lastSeenId = maxOf(lastSeenId, realId)
                    _state.update { st ->
                        val replaced = st.messages.map {
                            if (it.id == tempId) it.copy(id = realId) else it
                        }
                        st.copy(messages = replaced, sending = false)
                    }
                }
                is MessagesRepository.Result.Failure -> {
                    _state.update { st ->
                        st.copy(
                            messages = st.messages.filterNot { it.id == tempId },
                            sending = false,
                            toast = r.message,
                            toastIsError = true,
                        )
                    }
                }
            }
        }
    }

    /* ── Read receipts ──────────────────────────────────────── */

    private fun markRead(upToId: Long) {
        viewModelScope.launch {
            repo.markRead(conversationId, upToId)
        }
    }

    /* ── Toast ──────────────────────────────────────────────── */

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }

    private fun showToast(msg: String, isError: Boolean) {
        _state.update { it.copy(toast = msg, toastIsError = isError) }
    }

    companion object {
        fun factory(conversationId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                ChatViewModel(app.messagesRepository, conversationId)
            }
        }
    }
}