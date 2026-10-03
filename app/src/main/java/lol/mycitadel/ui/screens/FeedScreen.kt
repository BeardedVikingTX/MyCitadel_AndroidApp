package lol.mycitadel.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import lol.mycitadel.app.data.network.AttachmentDto
import lol.mycitadel.app.data.network.CommentDto
import lol.mycitadel.app.data.network.PostDto
import lol.mycitadel.app.data.network.UserDto
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.feed.ComposerState
import lol.mycitadel.app.ui.feed.FeedScope
import lol.mycitadel.app.ui.feed.FeedState
import lol.mycitadel.app.ui.feed.FeedViewModel
import lol.mycitadel.app.ui.feed.PostCommentsState
import lol.mycitadel.app.ui.feed.TierLimits
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.Void

private const val DEFAULT_AVATAR = "https://mycitadel.lol/img/users/default/avatar.png"

/* ══════════════════════════════════════════════════════════════════
 * REACTION VOCABULARY
 * ════════════════════════════════════════════════════════════════ */

private val REACTION_ICONS = mapOf(
    "like"    to "👍",
    "dislike" to "👎",
    "heart"   to "❤",
    "angry"   to "😡",
)
private val REACTION_LABELS = mapOf(
    "like"    to "Like",
    "dislike" to "Dislike",
    "heart"   to "Heart",
    "angry"   to "Angry",
)


/* ══════════════════════════════════════════════════════════════════
 * ENTRY POINT
 * ════════════════════════════════════════════════════════════════ */

@Composable
fun FeedScreen(
    currentUser: UserDto?,
    focusedPostId: Int? = null,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(
        key = if (focusedPostId != null) "feed_post_$focusedPostId" else "feed_main",
        factory = FeedViewModel.factory(focusedPostId),
    ),
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {

        when {
            state.loading && state.posts.isEmpty() ->
                FeedLoading()

            state.fatalError != null && state.posts.isEmpty() ->
                FeedError(state.fatalError!!, onRetry = viewModel::refresh)

            else ->
                FeedMain(state, currentUser, viewModel)
        }

        state.toast?.let { msg ->
            ToastOverlay(
                message = msg,
                isError = state.toastIsError,
                onDismiss = viewModel::clearToast,
            )
        }
    }
}


/* ══════════════════════════════════════════════════════════════════
 * LOADING / ERROR
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun FeedLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "GATHERING THE TIMELINE…",
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                letterSpacing = 3.sp,
            )
        }
    }
}

@Composable
private fun FeedError(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠", fontSize = 48.sp, color = Blood)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Could not load your feed",
                style = MaterialTheme.typography.titleLarge,
                color = GoldBright,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            CitadelButton(
                text = "Retry",
                onClick = onRetry,
                style = CitadelButtonStyle.Cyan,
            )
        }
    }
}


/* ══════════════════════════════════════════════════════════════════
 * MAIN LAYOUT
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun FeedMain(
    state: FeedState,
    currentUser: UserDto?,
    vm: FeedViewModel,
) {
    val isPremium = currentUser?.premium == true

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        if (state.focusedPostId != null) {
            item(key = "focused_post_banner") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Gold.copy(alpha = 0.12f))
                        .border(1.dp, Gold.copy(alpha = 0.4f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = "VIEWING SPECIFIC POST",
                            color = GoldBright,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                        )
                        Text(
                            text = "Showing post #${state.focusedPostId} and comments",
                            color = TextDim,
                            fontSize = 11.sp,
                        )
                    }
                    CitadelButton(
                        text = "View All",
                        onClick = vm::clearFocusedPost,
                        style = CitadelButtonStyle.Cyan,
                    )
                }
            }
        }

        item(key = "scope_row") {
            ScopeRow(
                current = state.scope,
                onSelect = vm::changeScope,
            )
        }

        if (state.focusedPostId == null) {
            item(key = "composer") {
                Composer(
                    user = currentUser,
                    composer = state.composer,
                    isPremium = isPremium,
                    onTextChange = vm::onComposerText,
                    onVisibilityChange = vm::onComposerVisibility,
                    onPickAttachments = { uri -> vm.addAttachment(uri, isPremium) },
                    onRemoveAttachment = vm::removeAttachment,
                    onSubmit = { vm.submitPost(currentUser) },
                )
            }
        }

        if (state.posts.isEmpty()) {
            item(key = "empty_feed") {
                EmptyFeed()
            }
        } else {
            items(
                items = state.posts,
                key = { it.id },
            ) { post ->
                PostCard(
                    post = post,
                    currentUser = currentUser,
                    commentsState = state.commentsByPost[post.id] ?: PostCommentsState(),
                    onEdit = { content, vis -> vm.updatePost(post.id, content, vis) },
                    onDelete = { vm.deletePost(post.id) },
                    onToggleReaction = { reaction -> vm.toggleReaction(post.id, reaction) },
                    onToggleComments = { vm.toggleComments(post.id) },
                    onCommentDraftChange = { text -> vm.onCommentDraftChange(post.id, text) },
                    onSubmitComment = { vm.submitComment(post.id, currentUser) },
                )
            }

            if (state.hasMore) {
                item(key = "load_more") {
                    LoadMoreRow(
                        loading = state.loadingMore,
                        onClick = { vm.loadFeed(reset = false) },
                    )
                }
            }
        }
    }
}


/* ══════════════════════════════════════════════════════════════════
 * SCOPE ROW
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun ScopeRow(
    current: FeedScope,
    onSelect: (FeedScope) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FeedScope.entries.forEach { scope ->
            val active = scope == current
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) Cyan else Color.Transparent)
                    .border(1.dp, Cyan.copy(alpha = 0.6f), RoundedCornerShape(999.dp))
                    .clickable { onSelect(scope) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(
                    text = scope.label.uppercase(),
                    color = if (active) Void else Cyan,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}


/* ══════════════════════════════════════════════════════════════════
 * COMPOSER — tier-aware
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun Composer(
    user: UserDto?,
    composer: ComposerState,
    isPremium: Boolean,
    onTextChange: (String) -> Unit,
    onVisibilityChange: (String) -> Unit,
    onPickAttachments: (Uri) -> Unit,
    onRemoveAttachment: (Int) -> Unit,
    onSubmit: () -> Unit,
) {
    val maxChars       = remember(isPremium) { TierLimits.postMaxChars(isPremium) }
    val maxAttachments = remember(isPremium) { TierLimits.postMaxAttachments(isPremium) }
    val warnAt         = (maxChars * 0.9).toInt()

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        uris.forEach { onPickAttachments(it) }
    }

    CitadelPanel(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            val avatarUrl = user?.avatarUrl ?: DEFAULT_AVATAR
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, Gold.copy(alpha = 0.5f), CircleShape),
            )
            Spacer(Modifier.width(10.dp))

            OutlinedTextField(
                value = composer.text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 80.dp, max = 220.dp),
                placeholder = {
                    Text(
                        text = "What's on your mind, Citizen?",
                        color = TextFaint,
                        fontSize = 14.sp,
                    )
                },
                maxLines = 8,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cyan,
                    unfocusedBorderColor = Cyan.copy(alpha = 0.25f),
                    cursorColor = CyanBright,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(10.dp),
            )
        }

        // Attachment preview chips
        if (composer.attachments.isNotEmpty() || composer.uploading > 0) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                composer.attachments.forEachIndexed { i, att ->
                    AttachmentChip(att = att, onRemove = { onRemoveAttachment(i) })
                }
                repeat(composer.uploading) {
                    UploadingChip()
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Visibility pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf(
                "public" to "🌐 Public",
                "connections" to "⚔ Circle",
                "private" to "🔒 Private",
            ).forEach { (key, label) ->
                val active = composer.visibility == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active) Cyan else Color.Transparent)
                        .border(1.dp, Cyan.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                        .clickable { onVisibilityChange(key) }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = label,
                        color = if (active) Void else Cyan,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp,
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Actions row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val attachDisabled = composer.attachments.size >= maxAttachments || composer.uploading > 0
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .border(
                        1.dp,
                        Cyan.copy(alpha = if (attachDisabled) 0.15f else 0.5f),
                        RoundedCornerShape(999.dp),
                    )
                    .clickable(enabled = !attachDisabled) {
                        picker.launch(arrayOf(
                            "image/*",
                            "video/*",
                            "audio/*",
                            "application/pdf",
                            "application/zip",
                            "text/plain",
                            "text/markdown",
                        ))
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Attach",
                        tint = if (attachDisabled) TextFaint else Cyan,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Attach",
                        color = if (attachDisabled) TextFaint else Cyan,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "${composer.text.length} / $maxChars",
                color = if (composer.text.length > warnAt) Gold else TextFaint,
                fontSize = 10.sp,
                fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
            )

            Spacer(Modifier.width(10.dp))

            CitadelButton(
                text = if (composer.submitting) "Posting…" else "Post",
                onClick = onSubmit,
                style = CitadelButtonStyle.Gold,
                enabled = composer.canSubmit,
            )
        }
    }
}


@Composable
private fun AttachmentChip(att: AttachmentDto, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Cyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .background(Void),
    ) {
        when (att.kind) {
            "image" -> AsyncImage(
                model = att.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            "video" -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Cyan)
            }
            else -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Image, contentDescription = null, tint = Cyan.copy(alpha = 0.6f))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.7f))
                .border(1.dp, Blood.copy(alpha = 0.7f), CircleShape)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Text("×", color = Blood, fontSize = 11.sp, lineHeight = 11.sp)
        }
    }
}

@Composable
private fun UploadingChip() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Void)
            .border(1.dp, Cyan.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = Cyan,
            strokeWidth = 2.dp,
            modifier = Modifier.size(24.dp),
        )
    }
}


/* ══════════════════════════════════════════════════════════════════
 * POST CARD — with reactions + comments
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun PostCard(
    post: PostDto,
    currentUser: UserDto?,
    commentsState: PostCommentsState,
    onEdit: (content: String, visibility: String) -> Unit,
    onDelete: () -> Unit,
    onToggleReaction: (String) -> Unit,
    onToggleComments: () -> Unit,
    onCommentDraftChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
) {
    var editing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(post.content) }
    var editVis by remember { mutableStateOf(post.visibility) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val isPremium = currentUser?.premium == true
    val allowedReactions = remember(isPremium) { TierLimits.reactions(isPremium) }

    CitadelPanel(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp),
        borderColor = if (post.isOwn) Cyan.copy(alpha = 0.25f) else Cyan.copy(alpha = 0.15f),
    ) {
        // ── Header ─────────────────────────────────────────────
        Row(verticalAlignment = Alignment.Top) {
            val avatarUrl = post.authorAvatarUrl ?: DEFAULT_AVATAR
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(1.dp, Gold.copy(alpha = 0.4f), CircleShape),
            )
            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.authorDisplayName ?: post.authorUsername.ifEmpty { "Citizen" },
                    color = GoldBright,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "@${post.authorUsername}",
                        color = Cyan,
                        fontSize = 10.sp,
                    )
                    Text("·", color = TextFaint, fontSize = 10.sp)
                    Text(
                        text = fmtRelativeTime(post.createdAt),
                        color = TextFaint,
                        fontSize = 10.sp,
                    )
                    if (post.isEdited) {
                        Text(
                            "· edited",
                            color = TextFaint,
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic,
                        )
                    }
                }
            }

            if (post.isOwn && !editing) {
                IconButton(
                    onClick = { editing = true; editText = post.content; editVis = post.visibility },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = Cyan,
                        modifier = Modifier.size(16.dp),
                    )
                }
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Blood,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── Body ──────────────────────────────────────────────
        if (editing) {
            OutlinedTextField(
                value = editText,
                onValueChange = { editText = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp),
                maxLines = 8,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cyan,
                    unfocusedBorderColor = Cyan.copy(alpha = 0.3f),
                    cursorColor = CyanBright,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(8.dp),
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "public" to "🌐 Public",
                    "connections" to "⚔ Circle",
                    "private" to "🔒 Private",
                ).forEach { (key, label) ->
                    val active = editVis == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (active) Cyan else Color.Transparent)
                            .border(1.dp, Cyan.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                            .clickable { editVis = key }
                            .padding(horizontal = 9.dp, vertical = 4.dp),
                    ) {
                        Text(label, color = if (active) Void else Cyan, fontSize = 9.sp)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { editing = false }) {
                    Text("Cancel", color = TextDim, fontSize = 12.sp)
                }
                CitadelButton(
                    text = "Save",
                    onClick = {
                        onEdit(editText.trim(), editVis)
                        editing = false
                    },
                    style = CitadelButtonStyle.Cyan,
                    enabled = editText.isNotBlank() || post.attachments.isNotEmpty(),
                )
            }
        } else {
            if (post.content.isNotBlank()) {
                Text(
                    text = post.content,
                    color = Color(0xFFE0E6ED),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }
            if (post.attachments.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                AttachmentGrid(post.attachments)
            }
        }

        // ── Reaction bar + comment toggle (hidden during edit) ──
        if (!editing) {
            Spacer(Modifier.height(10.dp))
            ReactionBar(
                viewerReaction = post.viewerReaction,
                reactionCount  = post.reactionCount,
                allowed        = allowedReactions,
                onToggle       = onToggleReaction,
            )

            Spacer(Modifier.height(8.dp))
            CommentToggleRow(
                commentCount = post.commentCount,
                expanded     = commentsState.expanded,
                onClick      = onToggleComments,
            )
        }
    }

    // ── Comment thread (outside the panel, expands below) ───────
    if (commentsState.expanded && !editing) {
        CommentThread(
            state = commentsState,
            isPremium = isPremium,
            onDraftChange = onCommentDraftChange,
            onSubmit = onSubmitComment,
        )
    }

    // ── Delete confirmation ─────────────────────────────────────
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = Void,
            titleContentColor = GoldBright,
            textContentColor = TextDim,
            title = { Text("Delete this post?") },
            text = { Text("This cannot be undone. −10 reputation.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDelete()
                }) {
                    Text("Delete", color = Blood)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = TextDim)
                }
            },
        )
    }
}


/* ══════════════════════════════════════════════════════════════════
 * REACTION BAR
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun ReactionBar(
    viewerReaction: String?,
    reactionCount: Int,
    allowed: List<String>,
    onToggle: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        allowed.forEach { reaction ->
            ReactionButton(
                reaction = reaction,
                active   = viewerReaction == reaction,
                onClick  = { onToggle(reaction) },
            )
        }

        Spacer(Modifier.weight(1f))

        if (reactionCount > 0) {
            Text(
                text = "$reactionCount reaction${if (reactionCount == 1) "" else "s"}",
                color = TextFaint,
                fontSize = 10.sp,
                fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
            )
        }
    }
}

@Composable
private fun ReactionButton(
    reaction: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val accent: Color = when (reaction) {
        "like"    -> Cyan
        "dislike" -> Blood
        "heart"   -> Color(0xFFEC4899)
        "angry"   -> Color(0xFFF97316)
        else      -> Cyan
    }
    val bg        = if (active) accent.copy(alpha = 0.14f) else Color.Transparent
    val fg        = if (active) accent else TextDim
    val borderCol = if (active) accent else Cyan.copy(alpha = 0.15f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = REACTION_ICONS[reaction] ?: "•",
                fontSize = 13.sp,
                lineHeight = 13.sp,
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = REACTION_LABELS[reaction] ?: reaction,
                color = fg,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp,
            )
        }
    }
}


/* ══════════════════════════════════════════════════════════════════
 * COMMENT TOGGLE + THREAD
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun CommentToggleRow(
    commentCount: Int,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (expanded) Cyan.copy(alpha = 0.08f) else Color.Transparent)
                .border(
                    1.dp,
                    if (expanded) Cyan else Cyan.copy(alpha = 0.15f),
                    RoundedCornerShape(999.dp),
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💬", fontSize = 12.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Comments ($commentCount)",
                    color = if (expanded) CyanBright else TextDim,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                )
            }
        }
    }
}

@Composable
private fun CommentThread(
    state: PostCommentsState,
    isPremium: Boolean,
    onDraftChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val maxChars = remember(isPremium) { TierLimits.commentMaxChars(isPremium) }
    val counter  = state.draft.length
    val warnAt   = (maxChars * 0.9).toInt()

    CitadelPanel(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        borderColor = Cyan.copy(alpha = 0.12f),
    ) {
        when {
            state.loading -> {
                Box(
                    Modifier.fillMaxWidth().padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = Cyan,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            state.comments.isEmpty() && state.loaded -> {
                Text(
                    text = "No comments yet. Be the first.",
                    color = TextFaint,
                    fontSize = 11.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                )
            }
            else -> {
                state.comments.forEach { c ->
                    CommentItem(c)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = state.draft,
            onValueChange = onDraftChange,
            modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 140.dp),
            placeholder = {
                Text(
                    text = "Write a comment…",
                    color = TextFaint,
                    fontSize = 13.sp,
                )
            },
            maxLines = 6,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Cyan,
                unfocusedBorderColor = Cyan.copy(alpha = 0.25f),
                cursorColor = CyanBright,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(10.dp),
        )

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$counter / $maxChars",
                color = if (counter > warnAt) Gold else TextFaint,
                fontSize = 10.sp,
                fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
            )
            Spacer(Modifier.weight(1f))
            CitadelButton(
                text = if (state.submitting) "Posting…" else "Comment",
                onClick = onSubmit,
                style = CitadelButtonStyle.Gold,
                enabled = state.draft.isNotBlank() && !state.submitting,
            )
        }
    }
}

@Composable
private fun CommentItem(c: CommentDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top,
    ) {
        val avatarUrl = c.author.avatarUrl ?: DEFAULT_AVATAR
        AsyncImage(
            model = avatarUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .border(1.dp, Gold.copy(alpha = 0.35f), CircleShape),
        )
        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = c.author.displayName ?: c.author.username,
                    color = if (c.viewerCanDelete) CyanBright else Color(0xFFE0E6ED),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "@${c.author.username}",
                    color = Cyan,
                    fontSize = 10.sp,
                    fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                )
                Text("·", color = TextFaint, fontSize = 10.sp)
                Text(
                    text = fmtRelativeTime(c.createdAt),
                    color = TextFaint,
                    fontSize = 10.sp,
                )
            }
            Spacer(Modifier.height(3.dp))
            Text(
                text = c.content,
                color = TextDim,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }
    }
}


/* ══════════════════════════════════════════════════════════════════
 * ATTACHMENT GRID
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun AttachmentGrid(attachments: List<AttachmentDto>) {
    val ctx = LocalContext.current

    fun open(url: String) {
        try {
            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) { /* no app to handle */ }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        when (attachments.size) {
            1 -> SingleAttachment(attachments[0], ::open)
            2 -> Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                attachments.forEach { a -> AttachmentCell(a, Modifier.weight(1f), ::open) }
            }
            3 -> Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                attachments.forEach { a -> AttachmentCell(a, Modifier.weight(1f), ::open) }
            }
            else -> attachments.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    row.forEach { a -> AttachmentCell(a, Modifier.weight(1f), ::open) }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SingleAttachment(att: AttachmentDto, onOpen: (String) -> Unit) {
    when (att.kind) {
        "image" -> {
            val ratio = remember(att.width, att.height) {
                val w = att.width ?: 1
                val h = att.height ?: 1
                (w.toFloat() / h.toFloat()).coerceIn(0.6f, 1.8f)
            }
            AsyncImage(
                model = att.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Cyan.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .clickable { onOpen(att.url) },
            )
        }
        else -> FileRow(att, onOpen)
    }
}

@Composable
private fun AttachmentCell(
    att: AttachmentDto,
    modifier: Modifier,
    onOpen: (String) -> Unit,
) {
    when (att.kind) {
        "image" -> AsyncImage(
            model = att.url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Cyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                .clickable { onOpen(att.url) },
        )
        "video" -> Box(
            modifier = modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Void)
                .border(1.dp, Cyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .clickable { onOpen(att.url) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Cyan,
                modifier = Modifier.size(32.dp),
            )
        }
        else -> FileCard(att, modifier, onOpen)
    }
}

@Composable
private fun FileRow(att: AttachmentDto, onOpen: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Void)
            .border(1.dp, Cyan.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .clickable { onOpen(att.url) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("📎", fontSize = 22.sp)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = att.name.ifBlank { "File" },
                color = CyanBright,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = formatBytes(att.size),
                color = TextFaint,
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
private fun FileCard(att: AttachmentDto, modifier: Modifier, onOpen: (String) -> Unit) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Void)
            .border(1.dp, Cyan.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .clickable { onOpen(att.url) }
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(iconFor(att.mime), fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            text = att.name.ifBlank { "File" },
            color = CyanBright,
            fontSize = 9.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        Text(
            text = formatBytes(att.size),
            color = TextFaint,
            fontSize = 8.sp,
        )
    }
}


/* ══════════════════════════════════════════════════════════════════
 * LOAD MORE + EMPTY
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun LoadMoreRow(loading: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = Cyan,
                strokeWidth = 2.dp,
                modifier = Modifier.size(28.dp),
            )
        } else {
            CitadelButton(
                text = "Load More",
                onClick = onClick,
                style = CitadelButtonStyle.Cyan,
            )
        }
    }
}

@Composable
private fun EmptyFeed() {
    Box(
        Modifier.fillMaxSize().padding(40.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            RuneDivider()
            Spacer(Modifier.height(20.dp))
            Text(
                text = "The timeline is quiet.",
                color = GoldBright,
                fontSize = 16.sp,
                fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Post something, or connect with another Citizen to see their thoughts here.",
                color = TextDim,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}


/* ══════════════════════════════════════════════════════════════════
 * TOAST OVERLAY
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun ToastOverlay(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit,
) {
    LaunchedEffect(message) {
        delay(2400)
        onDismiss()
    }

    val accent = if (isError) Blood else Success

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Void)
                .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                .padding(horizontal = 18.dp, vertical = 10.dp),
        ) {
            Text(
                text = message,
                color = if (isError) Color(0xFFFFB0B0) else Color(0xFFB0FFA8),
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}


/* ══════════════════════════════════════════════════════════════════
 * HELPERS
 * ════════════════════════════════════════════════════════════════ */

private fun formatBytes(n: Long): String {
    if (n <= 0) return "0 B"
    if (n < 1024) return "$n B"
    if (n < 1048576) return "%.1f KB".format(n / 1024.0)
    if (n < 1073741824L) return "%.1f MB".format(n / 1048576.0)
    return "%.2f GB".format(n / 1073741824.0)
}

private fun iconFor(mime: String): String = when {
    mime.startsWith("video/") -> "🎬"
    mime.startsWith("audio/") -> "🎵"
    mime == "application/pdf" -> "📄"
    mime == "application/zip" -> "🗜"
    else -> "📎"
}