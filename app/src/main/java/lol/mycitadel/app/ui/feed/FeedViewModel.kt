package lol.mycitadel.app.ui.feed

import android.net.Uri
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
import lol.mycitadel.app.data.network.AttachmentDto
import lol.mycitadel.app.data.network.PostDto
import lol.mycitadel.app.data.repository.FeedRepository
import lol.mycitadel.app.data.network.UserDto
/* ── Scope ──────────────────────────────────────────────────── */

enum class FeedScope(val key: String, val label: String) {
    All("all", "All"),
    Self("self", "Mine"),
    Connections("connections", "Circle"),
}

/* ── Composer ───────────────────────────────────────────────── */

data class ComposerState(
    val text: String = "",
    val visibility: String = "public",
    val attachments: List<AttachmentDto> = emptyList(),
    val uploading: Int = 0,     // count of in-flight uploads
    val submitting: Boolean = false,
) {
    val canSubmit: Boolean get() = (text.isNotBlank() || attachments.isNotEmpty())
            && !submitting
            && uploading == 0
}

/* ── Feed state ─────────────────────────────────────────────── */

data class FeedState(
    val loading: Boolean = true,
    val posts: List<PostDto> = emptyList(),
    val scope: FeedScope = FeedScope.All,
    val hasMore: Boolean = false,
    val nextCursor: String? = null,
    val loadingMore: Boolean = false,
    val composer: ComposerState = ComposerState(),
    val fatalError: String? = null,     // blocks the whole screen
    val toast: String? = null,          // transient notification
    val toastIsError: Boolean = false,
)

class FeedViewModel(private val repo: FeedRepository) : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state: StateFlow<FeedState> = _state.asStateFlow()

    init { loadFeed(reset = true) }

    /* ── Feed loading ────────────────────────────────────────── */

    fun loadFeed(reset: Boolean = false) {
        if (reset) {
            _state.update { it.copy(loading = true, fatalError = null, posts = emptyList()) }
        } else {
            if (_state.value.loadingMore || !_state.value.hasMore) return
            _state.update { it.copy(loadingMore = true) }
        }

        viewModelScope.launch {
            val snapshot = _state.value
            val result = repo.fetchFeed(
                scope = snapshot.scope.key,
                cursor = if (reset) null else snapshot.nextCursor,
            )

            when (result) {
                is FeedRepository.FetchResult.Success -> {
                    _state.update { st ->
                        val merged = if (reset) result.posts else st.posts + result.posts
                        st.copy(
                            loading = false,
                            loadingMore = false,
                            posts = merged,
                            hasMore = result.hasMore,
                            nextCursor = result.nextCursor,
                            fatalError = null,
                        )
                    }
                }
                is FeedRepository.FetchResult.Failure -> {
                    _state.update { st ->
                        if (reset) {
                            st.copy(loading = false, loadingMore = false, fatalError = result.message)
                        } else {
                            st.copy(loadingMore = false, toast = result.message, toastIsError = true)
                        }
                    }
                }
            }
        }
    }

    fun refresh() = loadFeed(reset = true)

    fun changeScope(scope: FeedScope) {
        if (_state.value.scope == scope) return
        _state.update { it.copy(scope = scope) }
        loadFeed(reset = true)
    }

    /* ── Composer ────────────────────────────────────────────── */

    fun onComposerText(text: String) {
        _state.update { it.copy(composer = it.composer.copy(text = text)) }
    }

    fun onComposerVisibility(visibility: String) {
        _state.update { it.copy(composer = it.composer.copy(visibility = visibility)) }
    }

    /** Called when a Uri is picked. Uploads it immediately. */
    fun addAttachment(uri: Uri) {
        val current = _state.value.composer.attachments.size
        if (current >= 6) {
            showToast("Maximum 6 attachments per post.", isError = true)
            return
        }

        _state.update { it.copy(composer = it.composer.copy(uploading = it.composer.uploading + 1)) }

        viewModelScope.launch {
            when (val result = repo.uploadMedia(uri)) {
                is FeedRepository.UploadResult.Success -> {
                    _state.update { st ->
                        st.copy(
                            composer = st.composer.copy(
                                uploading = (st.composer.uploading - 1).coerceAtLeast(0),
                                attachments = st.composer.attachments + result.attachment,
                            )
                        )
                    }
                }
                is FeedRepository.UploadResult.Failure -> {
                    _state.update { st ->
                        st.copy(
                            composer = st.composer.copy(uploading = (st.composer.uploading - 1).coerceAtLeast(0)),
                            toast = result.message,
                            toastIsError = true,
                        )
                    }
                }
            }
        }
    }

    fun removeAttachment(index: Int) {
        _state.update { st ->
            val list = st.composer.attachments.toMutableList()
            if (index in list.indices) list.removeAt(index)
            st.copy(composer = st.composer.copy(attachments = list))
        }
    }

    /* ── Submit ──────────────────────────────────────────────── */

    fun submitPost(currentUser: lol.mycitadel.app.data.network.UserDto?) {
        val c = _state.value.composer
        if (!c.canSubmit) return

        _state.update { it.copy(composer = it.composer.copy(submitting = true)) }

        viewModelScope.launch {
            val content = c.text.trim()
            val tokens = c.attachments.map { it.token }

            when (val result = repo.createPost(content, c.visibility, tokens)) {
                is FeedRepository.PostResult.Success -> {
                    // Optimistic insert at top
                    val newPost = PostDto(
                        id = result.postId,
                        userId = currentUser?.id ?: 0,
                        content = content,
                        visibility = c.visibility,
                        createdAt = java.time.Instant.now().toString(),
                        isEdited = false,
                        isDeleted = false,
                        authorUsername = currentUser?.username ?: "",
                        authorDisplayName = currentUser?.username,
                        authorAvatarUrl = null,     // fetched on refresh
                        isOwn = true,
                        attachments = c.attachments,
                    )
                    _state.update { st ->
                        st.copy(
                            posts = listOf(newPost) + st.posts,
                            composer = ComposerState(),   // reset
                            toast = "Posted. +10 reputation ✓",
                            toastIsError = false,
                        )
                    }
                    // Refresh in background so avatar + server truth arrive
                    loadFeed(reset = true)
                }
                is FeedRepository.PostResult.Failure -> {
                    _state.update { st ->
                        st.copy(
                            composer = st.composer.copy(submitting = false),
                            toast = result.message,
                            toastIsError = true,
                        )
                    }
                }
            }
        }
    }

    /* ── Edit ────────────────────────────────────────────────── */

    fun updatePost(id: Int, newContent: String, newVisibility: String) {
        viewModelScope.launch {
            when (val result = repo.updatePost(id, newContent, newVisibility)) {
                is FeedRepository.PostResult.Success -> {
                    // Replace in-place if server returned the fresh post
                    val fresh = result.post
                    _state.update { st ->
                        val updatedList = st.posts.map { p ->
                            if (p.id == id && fresh != null) fresh
                            else if (p.id == id) p.copy(
                                content = newContent,
                                visibility = newVisibility,
                                isEdited = true,
                            )
                            else p
                        }
                        st.copy(posts = updatedList, toast = "Post updated ✓", toastIsError = false)
                    }
                }
                is FeedRepository.PostResult.Failure -> {
                    _state.update { it.copy(toast = result.message, toastIsError = true) }
                }
            }
        }
    }

    /* ── Delete ──────────────────────────────────────────────── */

    fun deletePost(id: Int) {
        viewModelScope.launch {
            when (val result = repo.deletePost(id)) {
                is FeedRepository.PostResult.Success -> {
                    _state.update { st ->
                        st.copy(
                            posts = st.posts.filterNot { it.id == id },
                            toast = "Post deleted. −10 reputation.",
                            toastIsError = false,
                        )
                    }
                }
                is FeedRepository.PostResult.Failure -> {
                    _state.update { it.copy(toast = result.message, toastIsError = true) }
                }
            }
        }
    }

    /* ── Toast ───────────────────────────────────────────────── */

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
                FeedViewModel(app.feedRepository)
            }
        }
    }
}