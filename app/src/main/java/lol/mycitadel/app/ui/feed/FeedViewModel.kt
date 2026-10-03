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
import lol.mycitadel.app.data.network.CommentDto
import lol.mycitadel.app.data.network.PostDto
import lol.mycitadel.app.data.network.UserDto
import lol.mycitadel.app.data.repository.FeedRepository
import lol.mycitadel.app.data.network.CommentAuthorDto

/* ── Scope ──────────────────────────────────────────────────── */

enum class FeedScope(val key: String, val label: String) {
    All("all", "All"),
    Self("self", "Mine"),
    Connections("connections", "Circle"),
}

/* ── Tier limits (mirror posts/create.php + comments/create.php) ── */

object TierLimits {
    const val FREE_POST_MAX_CHARS          = 50
    const val FREE_POST_MAX_ATTACHMENTS    = 1
    const val FREE_COMMENT_MAX_CHARS       = 25
    val FREE_REACTIONS                     = listOf("like", "dislike")

    const val PREMIUM_POST_MAX_CHARS       = 1500
    const val PREMIUM_POST_MAX_ATTACHMENTS = 10
    const val PREMIUM_COMMENT_MAX_CHARS    = 1500
    val PREMIUM_REACTIONS                  = listOf("like", "dislike", "heart", "angry")

    fun postMaxChars(isPremium: Boolean): Int =
        if (isPremium) PREMIUM_POST_MAX_CHARS else FREE_POST_MAX_CHARS

    fun postMaxAttachments(isPremium: Boolean): Int =
        if (isPremium) PREMIUM_POST_MAX_ATTACHMENTS else FREE_POST_MAX_ATTACHMENTS

    fun commentMaxChars(isPremium: Boolean): Int =
        if (isPremium) PREMIUM_COMMENT_MAX_CHARS else FREE_COMMENT_MAX_CHARS

    fun reactions(isPremium: Boolean): List<String> =
        if (isPremium) PREMIUM_REACTIONS else FREE_REACTIONS
}

/* ── Composer ───────────────────────────────────────────────── */

data class ComposerState(
    val text: String = "",
    val visibility: String = "public",
    val attachments: List<AttachmentDto> = emptyList(),
    val uploading: Int = 0,
    val submitting: Boolean = false,
) {
    val canSubmit: Boolean get() = (text.isNotBlank() || attachments.isNotEmpty())
            && !submitting
            && uploading == 0
}

/* ── Per-post comment thread state ──────────────────────────── */

data class PostCommentsState(
    val expanded: Boolean = false,
    val loaded: Boolean = false,
    val loading: Boolean = false,
    val comments: List<CommentDto> = emptyList(),
    val draft: String = "",
    val submitting: Boolean = false,
)

/* ── Feed state ─────────────────────────────────────────────── */

data class FeedState(
    val loading: Boolean = true,
    val posts: List<PostDto> = emptyList(),
    val scope: FeedScope = FeedScope.All,
    val hasMore: Boolean = false,
    val nextCursor: String? = null,
    val loadingMore: Boolean = false,
    val focusedPostId: Int? = null,
    val composer: ComposerState = ComposerState(),
    val commentsByPost: Map<Int, PostCommentsState> = emptyMap(),
    val fatalError: String? = null,
    val toast: String? = null,
    val toastIsError: Boolean = false,
)

class FeedViewModel(
    private val repo: FeedRepository,
    initialFocusedPostId: Int? = null,
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState(focusedPostId = initialFocusedPostId))
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
                postId = if (reset) snapshot.focusedPostId else null,
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

                    snapshot.focusedPostId?.let { focusedId ->
                        if (result.posts.any { it.id == focusedId }) {
                            toggleComments(focusedId)
                        }
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

    fun addAttachment(uri: Uri, isPremium: Boolean) {
        val max = TierLimits.postMaxAttachments(isPremium)
        val current = _state.value.composer.attachments.size
        if (current >= max) {
            val plural = if (max == 1) "attachment" else "attachments"
            val hint   = if (isPremium) "" else " Upgrade to Premium for up to 10."
            showToast("Maximum $max $plural per post.$hint", isError = true)
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

    /* ── Submit post ─────────────────────────────────────────── */

    fun submitPost(currentUser: UserDto?) {
        val c = _state.value.composer
        if (!c.canSubmit) return

        val isPremium = currentUser?.premium == true
        val maxChars  = TierLimits.postMaxChars(isPremium)

        if (c.text.length > maxChars) {
            showToast("Posts are limited to $maxChars characters on your tier.", isError = true)
            return
        }

        _state.update { it.copy(composer = it.composer.copy(submitting = true)) }

        viewModelScope.launch {
            val content = c.text.trim()
            val tokens  = c.attachments.map { it.token }

            when (val result = repo.createPost(content, c.visibility, tokens)) {
                is FeedRepository.PostResult.Success -> {
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
                        authorAvatarUrl = null,
                        isOwn = true,
                        attachments = c.attachments,
                    )
                    _state.update { st ->
                        st.copy(
                            posts = listOf(newPost) + st.posts,
                            composer = ComposerState(),
                            toast = "Posted. +10 reputation ✓",
                            toastIsError = false,
                        )
                    }
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

    /* ── Edit post ───────────────────────────────────────────── */

    fun updatePost(id: Int, newContent: String, newVisibility: String) {
        viewModelScope.launch {
            when (val result = repo.updatePost(id, newContent, newVisibility)) {
                is FeedRepository.PostResult.Success -> {
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

    /* ── Delete post ─────────────────────────────────────────── */

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

    /* ══════════════════════════════════════════════════════════
     * REACTIONS
     * ========================================================== */

    fun toggleReaction(postId: Int, reaction: String) {
        val post = _state.value.posts.firstOrNull { it.id == postId } ?: return

        // Optimistic compute
        val wasActive    = post.viewerReaction == reaction
        val priorViewer  = post.viewerReaction
        val priorCount   = post.reactionCount

        val newViewer = if (wasActive) null else reaction
        val delta = when {
            wasActive           -> -1
            priorViewer == null -> 1
            else                -> 0   // changing reaction type: count unchanged
        }
        val optimisticCount = (priorCount + delta).coerceAtLeast(0)

        _state.update { st ->
            st.copy(posts = st.posts.map { p ->
                if (p.id == postId) p.copy(viewerReaction = newViewer, reactionCount = optimisticCount)
                else p
            })
        }

        viewModelScope.launch {
            when (val result = repo.toggleReaction(postId, reaction)) {
                is FeedRepository.ReactionResult.Success -> {
                    _state.update { st ->
                        st.copy(posts = st.posts.map { p ->
                            if (p.id == postId) p.copy(
                                viewerReaction = result.reaction,
                                reactionCount  = result.count,
                            ) else p
                        })
                    }
                }
                is FeedRepository.ReactionResult.Failure -> {
                    // Revert
                    _state.update { st ->
                        st.copy(
                            posts = st.posts.map { p ->
                                if (p.id == postId) p.copy(
                                    viewerReaction = priorViewer,
                                    reactionCount  = priorCount,
                                ) else p
                            },
                            toast = result.message,
                            toastIsError = true,
                        )
                    }
                }
            }
        }
    }

    /* ══════════════════════════════════════════════════════════
     * COMMENTS
     * ========================================================== */

    fun toggleComments(postId: Int) {
        val current = _state.value.commentsByPost[postId] ?: PostCommentsState()

        val willExpand = !current.expanded
        val updated = current.copy(expanded = willExpand)

        _state.update { st ->
            st.copy(commentsByPost = st.commentsByPost + (postId to updated))
        }

        // Lazy-load on first expand
        if (willExpand && !current.loaded && !current.loading) {
            loadComments(postId)
        }
    }

    fun loadComments(postId: Int) {
        val current = _state.value.commentsByPost[postId] ?: PostCommentsState()
        if (current.loading) return

        _state.update { st ->
            st.copy(commentsByPost = st.commentsByPost + (postId to current.copy(loading = true)))
        }

        viewModelScope.launch {
            when (val result = repo.listComments(postId)) {
                is FeedRepository.CommentsResult.Success -> {
                    _state.update { st ->
                        val cur = st.commentsByPost[postId] ?: PostCommentsState()
                        st.copy(commentsByPost = st.commentsByPost + (postId to cur.copy(
                            loading  = false,
                            loaded   = true,
                            comments = result.comments,
                        )))
                    }
                }
                is FeedRepository.CommentsResult.Failure -> {
                    _state.update { st ->
                        val cur = st.commentsByPost[postId] ?: PostCommentsState()
                        st.copy(
                            commentsByPost = st.commentsByPost + (postId to cur.copy(loading = false)),
                            toast = result.message,
                            toastIsError = true,
                        )
                    }
                }
            }
        }
    }

    fun onCommentDraftChange(postId: Int, text: String) {
        _state.update { st ->
            val cur = st.commentsByPost[postId] ?: PostCommentsState()
            st.copy(commentsByPost = st.commentsByPost + (postId to cur.copy(draft = text)))
        }
    }

    fun submitComment(postId: Int, currentUser: UserDto?) {
        val cur = _state.value.commentsByPost[postId] ?: return
        val content = cur.draft.trim()
        if (content.isEmpty() || cur.submitting) return

        val isPremium = currentUser?.premium == true
        val maxChars  = TierLimits.commentMaxChars(isPremium)

        if (content.length > maxChars) {
            showToast("Comments are limited to $maxChars characters on your tier.", isError = true)
            return
        }

        _state.update { st ->
            val c = st.commentsByPost[postId] ?: return@update st
            st.copy(commentsByPost = st.commentsByPost + (postId to c.copy(submitting = true)))
        }

        viewModelScope.launch {
            when (val result = repo.createComment(postId, content, null)) {
                is FeedRepository.CreateCommentResult.Success -> {
                    val newComment = CommentDto(
                        id = result.commentId,
                        postId = postId,
                        parentId = null,
                        author = CommentAuthorDto(
                            id = currentUser?.id ?: 0,
                            username = currentUser?.username ?: "",
                            displayName = currentUser?.displayName ?: currentUser?.username,
                            avatarUrl = currentUser?.avatarUrl,
                            accentColor = null,
                        ),
                        content = content,
                        createdAt = java.time.Instant.now().toString(),
                        viewerCanDelete = true,
                    )
                    _state.update { st ->
                        val c = st.commentsByPost[postId] ?: PostCommentsState()
                        val updatedPosts = st.posts.map { p ->
                            if (p.id == postId) p.copy(commentCount = p.commentCount + 1) else p
                        }
                        st.copy(
                            posts = updatedPosts,
                            commentsByPost = st.commentsByPost + (postId to c.copy(
                                submitting = false,
                                draft      = "",
                                comments   = c.comments + newComment,
                            )),
                            toast = "Comment posted. +5 reputation ✓",
                            toastIsError = false,
                        )
                    }
                }
                is FeedRepository.CreateCommentResult.Failure -> {
                    _state.update { st ->
                        val c = st.commentsByPost[postId] ?: PostCommentsState()
                        st.copy(
                            commentsByPost = st.commentsByPost + (postId to c.copy(submitting = false)),
                            toast = result.message,
                            toastIsError = true,
                        )
                    }
                }
            }
        }
    }

    fun clearFocusedPost() {
        _state.update { it.copy(focusedPostId = null) }
        loadFeed(reset = true)
    }

    /* ── Toast ───────────────────────────────────────────────── */

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }

    private fun showToast(msg: String, isError: Boolean) {
        _state.update { it.copy(toast = msg, toastIsError = isError) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = factory(null)

        fun factory(focusedPostId: Int? = null): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyCitadelApp
                FeedViewModel(app.feedRepository, focusedPostId)
            }
        }
    }
}