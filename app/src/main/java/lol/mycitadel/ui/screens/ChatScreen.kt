package lol.mycitadel.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import lol.mycitadel.app.data.network.MessageDto
import lol.mycitadel.app.data.network.UserDto
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.messages.ChatState
import lol.mycitadel.app.ui.messages.ChatViewModel
import lol.mycitadel.app.ui.theme.*

private const val DEFAULT_AVATAR = "https://mycitadel.lol/img/users/default/avatar.png"

@Composable
fun ChatScreen(
    conversationId: Long,
    currentUser: UserDto?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel(factory = ChatViewModel.factory(conversationId)),
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.loading && state.messages.isEmpty() -> Loading()
            state.fatalError != null && state.messages.isEmpty() ->
                ErrorState(state.fatalError!!, onBack)
            else -> ChatContent(state, currentUser, onBack, viewModel)
        }

        state.toast?.let { msg ->
            ToastOverlay(msg, state.toastIsError, viewModel::clearToast)
        }
    }
}

@Composable
private fun Loading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp)
    }
}

@Composable
private fun ErrorState(message: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠", fontSize = 40.sp, color = Blood)
            Spacer(Modifier.height(12.dp))
            Text(message, color = TextDim, fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            CitadelButton("Back", onBack, style = CitadelButtonStyle.Cyan)
        }
    }
}

@Composable
private fun ChatContent(
    state: ChatState,
    currentUser: UserDto?,
    onBack: () -> Unit,
    vm: ChatViewModel,
) {
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Column(Modifier.fillMaxSize()) {

        // ── Header ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Void)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "←",
                color = Cyan,
                fontSize = 20.sp,
                modifier = Modifier.clickable(onClick = onBack).padding(6.dp),
            )
            Spacer(Modifier.width(6.dp))

            val name = state.other?.displayName ?: state.other?.username ?: "Chat"
            val avatarUrl = state.other?.avatarUrl ?: DEFAULT_AVATAR

            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(2.dp, Gold.copy(alpha = 0.5f), CircleShape),
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                state.other?.username?.let {
                    Text(
                        "@$it",
                        color = Cyan,
                        fontSize = 11.sp,
                        fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(1.dp)
                .background(Cyan.copy(alpha = 0.12f)),
        )

        // ── Messages list ──────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Void.copy(alpha = 0.35f)),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(state.messages, key = { it.id }) { msg ->
                MessageBubble(msg, currentUser?.id == msg.senderId)
            }
        }

        // ── Composer ───────────────────────────────────────────
        Composer(state, vm)
    }
}

@Composable
private fun MessageBubble(msg: MessageDto, mine: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (mine) Cyan.copy(alpha = 0.12f)
                    else Void.copy(alpha = 0.8f)
                )
                .border(
                    width = 1.dp,
                    color = if (mine) Cyan.copy(alpha = 0.4f) else Cyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            when {
                msg.deleted -> Text(
                    "message deleted",
                    color = TextFaint, fontSize = 12.sp, fontStyle = FontStyle.Italic,
                )
                msg.encrypted -> Text(
                    "[unable to decrypt]",
                    color = TextFaint, fontSize = 12.sp, fontStyle = FontStyle.Italic,
                )
                else -> if (msg.body.isNotBlank()) Text(
                    msg.body,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                )
            }

            if (msg.attachments.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                AttachmentGrid(msg)
            }

            Spacer(Modifier.height(4.dp))
            Text(
                fmtRelativeTime(msg.createdAt),
                color = TextFaint,
                fontSize = 9.sp,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

@Composable
private fun AttachmentGrid(msg: MessageDto) {
    val atts = msg.attachments
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        atts.forEach { a ->
            when (a.kind) {
                "image" -> AsyncImage(
                    model = a.url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                "video" -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🎬 ${a.name.ifBlank { "video" }}", color = Cyan, fontSize = 12.sp)
                }
                else -> Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("📎", fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(a.name.ifBlank { "File" }, color = Cyan, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun Composer(state: ChatState, vm: ChatViewModel) {
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> -> uris.forEach { vm.addAttachment(it) } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Void.copy(alpha = 0.9f))
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        if (state.pendingAttachments.isNotEmpty() || state.uploading > 0) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                state.pendingAttachments.forEachIndexed { i, a ->
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Cyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                    ) {
                        if (a.kind == "image") {
                            AsyncImage(
                                model = a.url, contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("📎", fontSize = 20.sp)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.7f))
                                .clickable { vm.removeAttachment(i) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("×", color = Blood, fontSize = 10.sp)
                        }
                    }
                }
                repeat(state.uploading) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Cyan.copy(alpha = 0.25f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, Cyan.copy(alpha = 0.4f), CircleShape)
                    .clickable { picker.launch(arrayOf(
                        "image/*","video/*","audio/*",
                        "application/pdf","application/zip",
                        "text/plain","text/markdown"
                    )) },
                contentAlignment = Alignment.Center,
            ) {
                Text("📎", fontSize = 16.sp, color = Cyan)
            }

            Spacer(Modifier.width(6.dp))

            OutlinedTextField(
                value = state.composerText,
                onValueChange = vm::onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 42.dp, max = 140.dp),
                placeholder = { Text("Message…", color = TextFaint, fontSize = 14.sp) },
                maxLines = 6,
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

            Spacer(Modifier.width(6.dp))

            CitadelButton(
                text = if (state.sending) "…" else "Send",
                onClick = {
                    val uid = 0 // passed in? we use senderId fallback
                    vm.send(state.messages.lastOrNull()?.senderId ?: uid)
                },
                style = CitadelButtonStyle.Gold,
                enabled = state.canSend,
            )
        }
    }
}

@Composable
private fun ToastOverlay(message: String, isError: Boolean, onDismiss: () -> Unit) {
    LaunchedEffect(message) { delay(2400); onDismiss() }
    val accent = if (isError) Blood else Success
    Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Void)
                .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                .padding(horizontal = 18.dp, vertical = 10.dp),
        ) {
            Text(
                message,
                color = if (isError) Color(0xFFFFB0B0) else Color(0xFFB0FFA8),
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            )
        }
    }
}