package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import lol.mycitadel.app.data.network.ConversationDto
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.messages.MessagesState
import lol.mycitadel.app.ui.messages.MessagesViewModel
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.TextPrimary
import lol.mycitadel.app.ui.theme.Void

private const val DEFAULT_AVATAR = "https://mycitadel.lol/img/users/default/avatar.png"

@Composable
fun MessagesScreen(
    onOpenChat: (Long) -> Unit,
    onNewConversation: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MessagesViewModel = viewModel(factory = MessagesViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.loading && state.conversations.isEmpty() -> Loading()
            state.fatalError != null && state.conversations.isEmpty() ->
                ErrorState(state.fatalError!!, viewModel::load)
            else -> InboxList(state, onOpenChat, onNewConversation)
        }

        state.toast?.let { msg ->
            ToastOverlay(msg, state.toastIsError, viewModel::clearToast)
        }
    }
}

@Composable
private fun Loading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp)
            Spacer(Modifier.height(14.dp))
            Text("LOADING MESSAGES…", color = TextDim, fontSize = 11.sp, letterSpacing = 3.sp)
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠", fontSize = 48.sp, color = Blood)
            Spacer(Modifier.height(16.dp))
            Text("Could not load messages", color = GoldBright, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(message, color = TextDim, fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            CitadelButton("Retry", onRetry, style = CitadelButtonStyle.Cyan)
        }
    }
}

@Composable
private fun InboxList(
    state: MessagesState,
    onOpenChat: (Long) -> Unit,
    onNewConversation: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        if (state.conversations.isEmpty()) {
            EmptyInbox(onNewConversation)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp),
            ) {
                items(state.conversations, key = { it.id }) { conv ->
                    ConversationRow(conv) { onOpenChat(conv.id) }
                }
            }
        }

        FloatingActionButton(
            onClick = onNewConversation,
            containerColor = Gold,
            contentColor = Void,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "New message")
        }
    }
}

@Composable
private fun EmptyInbox(onNew: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("💬", fontSize = 56.sp, color = Gold.copy(alpha = 0.6f))
            Spacer(Modifier.height(12.dp))
            Text("No conversations yet", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Tap the + button to start chatting with a connection.",
                color = TextDim, fontSize = 13.sp, textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ConversationRow(conv: ConversationDto, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val name = conv.other?.displayName ?: conv.other?.username ?: "Unknown"
        val avatarUrl = conv.other?.avatarUrl ?: DEFAULT_AVATAR

        AsyncImage(
            model = avatarUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(2.dp, Gold.copy(alpha = 0.5f), CircleShape),
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                conv.lastMessageAt?.let {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        fmtRelativeTime(it),
                        color = TextFaint,
                        fontSize = 10.sp,
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = conv.lastPreview ?: "No messages yet",
                    color = TextDim,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (conv.unreadCount > 0) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Blood),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (conv.unreadCount > 9) "9+" else conv.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToastOverlay(message: String, isError: Boolean, onDismiss: () -> Unit) {
    LaunchedEffect(message) {
        delay(2400)
        onDismiss()
    }
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
                text = message,
                color = if (isError) Color(0xFFFFB0B0) else Color(0xFFB0FFA8),
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/* Shared with other screens if needed — local for now */
internal fun fmtRelativeTime(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    return try {
        val instant = java.time.Instant.parse(iso)
        val diff = java.time.Duration.between(instant, java.time.Instant.now()).seconds
        when {
            diff < 60 -> "now"
            diff < 3600 -> "${diff / 60}m"
            diff < 86400 -> "${diff / 3600}h"
            diff < 604800 -> "${diff / 86400}d"
            else -> java.time.format.DateTimeFormatter.ofPattern("MMM d")
                .withZone(java.time.ZoneId.systemDefault())
                .format(instant)
        }
    } catch (_: Exception) { iso.take(10) }
}