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
import androidx.compose.material.icons.filled.Refresh
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

@Composable
fun FeedScreen(
    currentUser: UserDto?,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(factory = FeedViewModel.Factory),
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

        // Toast overlay — auto-dismisses after 2.4s
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
 * MAIN
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun FeedMain(
    state: FeedState,
    currentUser: UserDto?,
    vm: FeedViewModel,
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // ── Scope tabs ────────────────────────────────────────────
        ScopeRow(
            current = state.scope,
            onSelect = vm::changeScope,
        )

        // ── Composer ──────────────────────────────────────────────
        Composer(
            user = currentUser,
            composer = state.composer,
            onTextChange = vm::onComposerText,
            onVisibilityChange = vm::onComposerVisibility,
            onPickAttachments = { uri -> vm.addAttachment(uri) },
            onRemoveAttachment = vm::removeAttachment,
            onSubmit = { vm.submitPost(currentUser) },
        )

        // ── Posts list ────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            if (state.posts.isEmpty()) {
                EmptyFeed()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    items(
                        items = state.posts,
                        key = { it.id },
                    ) { post ->
                        PostCard(
                            post = post,
                            onEdit = { content, vis -> vm.updatePost(post.id, content, vis) },
                            onDelete = { vm.deletePost(post.id) },
                        )
                    }

                    if (state.hasMore) {
                        item {
                            LoadMoreRow(
                                loading = state.loadingMore,
                                onClick = { vm.loadFeed(reset = false) },
                            )
                        }
                    }
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
 * COMPOSER
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun Composer(
    user: UserDto?,
    composer: ComposerState,
    onTextChange: (String) -> Unit,
    onVisibilityChange: (String) -> Unit,
    onPickAttachments: (Uri) -> Unit,
    onRemoveAttachment: (Int) -> Unit,
    onSubmit: () -> Unit,
) {
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
            // Attach button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .border(1.dp, Cyan.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                    .clickable(enabled = composer.attachments.size < 6 && composer.uploading == 0) {
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
                        tint = Cyan,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Attach",
                        color = Cyan,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "${composer.text.length} / 2000",
                color = if (composer.text.length > 1800) Gold else TextFaint,
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

        // Remove ×
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
 * POST CARD
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun PostCard(
    post: PostDto,
    onEdit: (content: String, visibility: String) -> Unit,
    onDelete: () -> Unit,
) {
    var editing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(post.content) }
    var editVis by remember { mutableStateOf(post.visibility) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

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
                        Text("· edited", color = TextFaint, fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    }
                }
            }

            // Own-post actions
            if (post.isOwn && !editing) {
                IconButton(
                    onClick = { editing = true; editText = post.content; editVis = post.visibility },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Cyan, modifier = Modifier.size(16.dp))
                }
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Blood, modifier = Modifier.size(16.dp))
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
    }

    // ── Delete confirmation dialog ────────────────────────────────
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
            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Cyan, modifier = Modifier.size(32.dp))
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
            CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
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

private fun fmtRelativeTime(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    return try {
        val instant = java.time.Instant.parse(iso)
        val diff = java.time.Duration.between(instant, java.time.Instant.now()).seconds
        when {
            diff < 60     -> "just now"
            diff < 3600   -> "${diff / 60}m"
            diff < 86400  -> "${diff / 3600}h"
            diff < 604800 -> "${diff / 86400}d"
            else          -> java.time.format.DateTimeFormatter
                .ofPattern("MMM d")
                .withZone(java.time.ZoneId.systemDefault())
                .format(instant)
        }
    } catch (_: Exception) {
        iso.take(10)
    }
}

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