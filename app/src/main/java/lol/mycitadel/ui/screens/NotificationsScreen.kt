package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.jsonPrimitive
import lol.mycitadel.app.data.network.NotificationActorDto
import lol.mycitadel.app.data.network.NotificationDto
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.notifications.NotifTab
import lol.mycitadel.app.ui.notifications.NotificationsViewModel
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Rune
import lol.mycitadel.app.ui.theme.RuneBright
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.TextPrimary
import lol.mycitadel.app.ui.theme.Void
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val DEFAULT_AVATAR = "https://mycitadel.lol/img/users/default/avatar.png"

@Composable
fun NotificationsScreen(
    onOpenUser: (Int) -> Unit = {},
    onOpenPost: (Int) -> Unit = {},
    onOpenChat: (Long) -> Unit = {},
    onOpenMessages: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = viewModel(factory = NotificationsViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            Hero(unreadCount = state.unreadCount)

            Toolbar(
                current = state.tab,
                unreadCount = state.unreadCount,
                onTabChange = viewModel::switchTab,
                onMarkAllRead = viewModel::markAllRead,
            )

            when {
                state.loading && state.notifications.isEmpty() ->
                    LoadingState()

                state.fatalError != null && state.notifications.isEmpty() ->
                    ErrorState(state.fatalError!!, viewModel::load)

                state.notifications.isEmpty() ->
                    EmptyState(state.tab)

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.notifications, key = { it.id }) { n ->
                        NotificationRow(
                            n = n,
                            busy = n.id in state.busyIds,
                            onClick = { viewModel.markRead(n.id) },
                            onAccept = { actor ->
                                viewModel.acceptConnection(n.id, actor.id, actor.displayName ?: actor.username)
                            },
                            onDeny = { actor ->
                                viewModel.denyConnection(n.id, actor.id, actor.displayName ?: actor.username)
                            },
                            onOpenUser = onOpenUser,
                            onOpenPost = onOpenPost,
                            onOpenChat = onOpenChat,
                            onOpenMessages = onOpenMessages,
                        )
                    }
                }
            }
        }

        state.toast?.let { msg ->
            ToastOverlay(msg, state.toastIsError, viewModel::clearToast)
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * HERO + TOOLBAR
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun Hero(unreadCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RuneDivider()
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "NOTIFICATIONS",
                color = GoldBright,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
            )
            if (unreadCount > 0) {
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Blood)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = unreadCount.toString(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = "Connection requests, replies, and reactions",
            color = TextDim,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Toolbar(
    current: NotifTab,
    unreadCount: Int,
    onTabChange: (NotifTab) -> Unit,
    onMarkAllRead: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        NotifTab.entries.forEach { tab ->
            val active = tab == current
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) Cyan else Color.Transparent)
                    .border(1.dp, Cyan.copy(alpha = 0.6f), RoundedCornerShape(999.dp))
                    .clickable { onTabChange(tab) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(
                    text = tab.label.uppercase(),
                    color = if (active) Void else Cyan,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .border(1.dp, TextFaint.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                    .clickable(onClick = onMarkAllRead)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "Mark all read",
                    color = TextDim,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                )
            }
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * NOTIFICATION ROW
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun NotificationRow(
    n: NotificationDto,
    busy: Boolean,
    onClick: () -> Unit,
    onAccept: (actor: NotificationActorDto) -> Unit,
    onDeny: (actor: NotificationActorDto) -> Unit,
    onOpenUser: (Int) -> Unit,
    onOpenPost: (Int) -> Unit,
    onOpenChat: (Long) -> Unit,
    onOpenMessages: () -> Unit,
) {
    val actor = n.actor
    val payload = extractPayload(n.payload)
    val postId = extractInt(payload, "post_id", "postId")
    val conversationId = extractLong(payload, "conversation_id", "conversationId", "chat_id", "thread_id")
    val isMessage = n.type.contains("message") || conversationId != null

    val accent = when {
        n.type == "connection_request"  -> Cyan
        n.type == "connection_accepted" -> Success
        n.type == "connection_denied"   -> TextFaint
        n.type.contains("reaction")     -> GoldBright
        n.type.contains("comment")      -> RuneBright
        isMessage                       -> CyanBright
        else                            -> CyanBright
    }

    val icon = when {
        n.type == "connection_request"  -> "⚔"
        n.type == "connection_accepted" -> "✓"
        n.type == "connection_denied"   -> "✗"
        n.type.contains("comment")      -> "💬"
        n.type.contains("reaction")     -> reactionIcon(payload?.get("reaction")?.jsonPrimitive?.content)
        isMessage                       -> "💬"
        else                            -> "◈"
    }

    val borderColor = if (!n.read) Gold else accent
    val bg = if (!n.read) Gold.copy(alpha = 0.04f) else Void.copy(alpha = 0.4f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, borderColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable {
                onClick()
                when {
                    conversationId != null -> onOpenChat(conversationId)
                    isMessage -> onOpenMessages()
                    postId != null -> onOpenPost(postId)
                    actor != null -> onOpenUser(actor.id)
                }
            }
            .padding(12.dp),
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(borderColor),
        )
        Spacer(Modifier.width(10.dp))

        // Icon
        Text(
            text = icon,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            modifier = Modifier.padding(top = 2.dp),
        )
        Spacer(Modifier.width(10.dp))

        // Avatar
        val avatarUrl = actor?.avatarUrl ?: DEFAULT_AVATAR
        AsyncImage(
            model = avatarUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .border(1.dp, Gold.copy(alpha = 0.35f), CircleShape),
        )
        Spacer(Modifier.width(12.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            // Name + handle line
            if (actor != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = actor.displayName ?: actor.username,
                        color = if (!n.read) GoldBright else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "@${actor.username}",
                        color = Cyan,
                        fontSize = 10.sp,
                    )
                }
            }

            // Body copy — depends on type
            Spacer(Modifier.height(3.dp))
            NotificationMessage(n, actor?.displayName ?: actor?.username ?: "Someone")

            // Timestamp
            Spacer(Modifier.height(4.dp))
            Text(
                text = fmtRelTime(n.createdAt),
                color = TextFaint,
                fontSize = 10.sp,
            )

            // Actions
            val hasActions = n.type == "connection_request" ||
                    (n.type == "connection_accepted" && actor != null) ||
                    postId != null ||
                    isMessage

            if (hasActions) {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    when {
                        n.type == "connection_request" && actor != null -> {
                            NotifActionButton(
                                label = "Accept",
                                accent = Cyan,
                                enabled = !busy,
                                onClick = { onAccept(actor) },
                            )
                            NotifActionButton(
                                label = "Deny",
                                accent = Blood,
                                enabled = !busy,
                                onClick = { onDeny(actor) },
                            )
                        }
                        n.type == "connection_accepted" && actor != null -> {
                            NotifActionButton(
                                label = "View Profile",
                                accent = Cyan,
                                enabled = true,
                                onClick = { onOpenUser(actor.id) },
                            )
                        }
                        isMessage -> {
                            NotifActionButton(
                                label = "View Message",
                                accent = Cyan,
                                enabled = true,
                                onClick = {
                                    if (conversationId != null) {
                                        onOpenChat(conversationId)
                                    } else {
                                        onOpenMessages()
                                    }
                                },
                            )
                        }
                        postId != null -> {
                            NotifActionButton(
                                label = "View Post",
                                accent = Cyan,
                                enabled = true,
                                onClick = { onOpenPost(postId) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationMessage(n: NotificationDto, actorName: String) {
    val text = when {
        n.type == "connection_request"  -> "$actorName wants to connect with you."
        n.type == "connection_accepted" -> "$actorName accepted your connection request."
        n.type == "connection_denied"   -> "Your connection request was not accepted."
        n.type.contains("comment")      -> "$actorName commented on your post."
        n.type.contains("reaction")     -> "$actorName reacted to your post."
        n.type.contains("message")      -> "$actorName sent you a message."
        else -> n.body ?: n.title ?: "New notification."
    }
    Text(
        text = text,
        color = TextDim,
        fontSize = 12.sp,
        lineHeight = 17.sp,
    )
}

@Composable
private fun NotifActionButton(
    label: String,
    accent: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .border(
                1.dp,
                accent.copy(alpha = if (enabled) 0.6f else 0.2f),
                RoundedCornerShape(999.dp),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            color = if (enabled) accent else accent.copy(alpha = 0.4f),
            fontSize = 10.sp,
            letterSpacing = 0.5.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/* ══════════════════════════════════════════════════════════════════
 * HELPERS
 * ════════════════════════════════════════════════════════════════ */

private val payloadJson = Json { ignoreUnknownKeys = true; isLenient = true }

private fun extractPayload(el: JsonElement?): JsonObject? {
    if (el == null) return null
    return when (el) {
        is JsonObject -> el
        is JsonPrimitive -> if (el.isString) {
            try { payloadJson.parseToJsonElement(el.content) as? JsonObject }
            catch (_: Exception) { null }
        } else null
        else -> null
    }
}

private fun extractLong(obj: JsonObject?, vararg keys: String): Long? {
    if (obj == null) return null
    for (key in keys) {
        val el = obj[key] ?: continue
        val primitive = el as? JsonPrimitive ?: continue
        primitive.longOrNull?.let { return it }
        primitive.content.toLongOrNull()?.let { return it }
    }
    return null
}

private fun extractInt(obj: JsonObject?, vararg keys: String): Int? {
    if (obj == null) return null
    for (key in keys) {
        val el = obj[key] ?: continue
        val primitive = el as? JsonPrimitive ?: continue
        primitive.intOrNull?.let { return it }
        primitive.content.toIntOrNull()?.let { return it }
    }
    return null
}

private fun reactionIcon(reaction: String?): String = when (reaction) {
    "like"    -> "👍"
    "heart"   -> "❤"
    "dislike" -> "👎"
    "angry"   -> "😡"
    else      -> "★"
}

private fun fmtRelTime(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    return try {
        val instant = Instant.parse(iso)
        val diff = Duration.between(instant, Instant.now()).seconds
        when {
            diff < 60     -> "just now"
            diff < 3600   -> "${diff / 60}m ago"
            diff < 86400  -> "${diff / 3600}h ago"
            diff < 604800 -> "${diff / 86400}d ago"
            else -> DateTimeFormatter.ofPattern("MMM d")
                .withZone(ZoneId.systemDefault())
                .format(instant)
        }
    } catch (_: Exception) { iso.take(10) }
}

/* ══════════════════════════════════════════════════════════════════
 * STATES
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp)
            Spacer(Modifier.height(14.dp))
            Text(
                text = "LOADING NOTIFICATIONS…",
                color = TextDim,
                fontSize = 11.sp,
                letterSpacing = 3.sp,
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠", fontSize = 48.sp, color = Blood)
            Spacer(Modifier.height(16.dp))
            Text("Could not load notifications", color = GoldBright, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(message, color = TextDim, fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            CitadelButton("Retry", onRetry, style = CitadelButtonStyle.Cyan)
        }
    }
}

@Composable
private fun EmptyState(tab: NotifTab) {
    Box(Modifier.fillMaxSize().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("◈", fontSize = 48.sp, color = Gold.copy(alpha = 0.5f))
            Spacer(Modifier.height(14.dp))
            Text(
                text = if (tab == NotifTab.Unread) "You're all caught up." else "All quiet.",
                color = GoldBright,
                fontSize = 16.sp,
                fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (tab == NotifTab.Unread)
                    "Nothing unread right now."
                else
                    "Connection requests, comments, and reactions will show up here.",
                color = TextDim,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
            )
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
    Box(
        modifier = Modifier.fillMaxSize().padding(20.dp),
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
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
