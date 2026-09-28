package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import lol.mycitadel.app.data.network.ActivityDto
import lol.mycitadel.app.data.network.BadgeDto
import lol.mycitadel.app.data.network.DashboardResponse
import lol.mycitadel.app.ui.dashboard.DashboardState
import lol.mycitadel.app.ui.dashboard.DashboardViewModel
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.GlitchText
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Rune
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.Void

private const val DEFAULT_AVATAR    = "https://mycitadel.lol/img/users/default/avatar.png"
private const val DEFAULT_BANNER    = "https://mycitadel.lol/img/users/default/banner.png"

@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    when (val s = state) {
        is DashboardState.Loading -> LoadingView(modifier)
        is DashboardState.Error   -> ErrorView(
            message = s.message,
            onRetry = { viewModel.load() },
            modifier = modifier,
        )
        is DashboardState.Success -> DashboardContent(
            data = s.data,
            refreshing = s.refreshing,
            onRefresh = { viewModel.refresh() },
            onLogout = onLogout,
            modifier = modifier,
        )
    }
}

/* ══════════════════════════════════════════════════════════════════
 * LOADING & ERROR
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "GATHERING THE CITADEL RECORDS…",
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                letterSpacing = 3.sp,
            )
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠", fontSize = 48.sp, color = Blood)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Could not load your dashboard",
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
 * MAIN CONTENT
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun DashboardContent(
    data: DashboardResponse,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 48.dp),
    ) {
        item { HeroSection(data, refreshing, onRefresh) }
        item { if (isNewUser(data)) OnboardingSection(data) }
        item { KpiStrip(data) }
        item { ChartsSection(data) }
        item { MilestonesSection(data) }
        item { StatusSplitSection(data) }
        item { BadgeGallerySection(data) }
        item { ActivityAndReferralSection(data) }
        item { ActionBar(onLogout) }
    }
}

private fun isNewUser(d: DashboardResponse): Boolean {
    val s = d.stats ?: return false
    val activityCount = d.activity.size
    return s.postCount == 0 && s.connectionCount == 0 && activityCount <= 2
}

/* ══════════════════════════════════════════════════════════════════
 * HERO
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun HeroSection(
    data: DashboardResponse,
    refreshing: Boolean,
    onRefresh: () -> Unit,
) {
    val id = data.identity ?: return
    val account = data.account
    val age = account?.age

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Void),
    ) {
        // Banner (bottom layer)
        AsyncImage(
            model = id.bannerUrl ?: DEFAULT_BANNER,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp)),
            alpha = 0.45f,
        )

        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                AsyncImage(
                    model = id.avatarUrl ?: DEFAULT_AVATAR,
                    contentDescription = id.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(2.dp, Gold, CircleShape),
                )
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = greeting().uppercase() + ",",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDim,
                        letterSpacing = 2.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = id.displayName ?: id.username,
                        style = MaterialTheme.typography.headlineSmall,
                        color = GoldBright,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!id.tagline.isNullOrBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = id.tagline,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDim,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                IconButton(onClick = onRefresh, enabled = !refreshing) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh",
                        tint = Cyan,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                account?.let {
                    StatusPill(
                        label = if (it.emailVerified) "✓ VERIFIED" else "UNVERIFIED",
                        color = if (it.emailVerified) Success else Gold,
                    )
                    if (it.isPremium) {
                        StatusPill("★ PREMIUM", Gold)
                    }
                    StatusPill(
                        label = if (it.isActive) "● ACTIVE" else "● INACTIVE",
                        color = if (it.isActive) Success else Blood,
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Citizen for ${age?.human ?: "today"} · " +
                        "Joined ${formatDate(account?.memberSince)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextFaint,
            )
        }
    }
}

@Composable
private fun StatusPill(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            letterSpacing = 1.sp,
            fontSize = 9.sp,
        )
    }
}

/* ══════════════════════════════════════════════════════════════════
 * ONBOARDING
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun OnboardingSection(data: DashboardResponse) {
    val s = data.stats ?: return
    val id = data.identity ?: return
    val acc = data.account

    val steps = listOf(
        Triple("Set a display name",          !id.displayName.isNullOrBlank(),      null),
        Triple("Add a tagline",                !id.tagline.isNullOrBlank(),         null),
        Triple("Verify your email",            acc?.emailVerified == true,          null),
        Triple("Create your first post",       s.postCount > 0,                     null),
        Triple("Make your first connection",   s.connectionCount > 0,               null),
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        CitadelPanel(modifier = Modifier.fillMaxWidth(), borderColor = Gold.copy(alpha = 0.4f)) {
            Text(
                text = "Welcome to the Citadel",
                style = MaterialTheme.typography.titleLarge,
                color = GoldBright,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Five quick steps to make this place yours.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDim,
            )
            Spacer(Modifier.height(16.dp))

            steps.forEach { (label, done, _) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (done) "✓" else "○",
                        color = if (done) Success else TextFaint,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (done) TextFaint else TextDim,
                        textDecoration = if (done) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                    )
                }
            }
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * KPI STRIP
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun KpiStrip(data: DashboardResponse) {
    val s = data.stats ?: return
    val r = data.rank ?: return

    val kpis = listOf(
        Kpi("REPUTATION", "%,d".format(s.reputation),
            if (r.aboveAvg) "▲ above avg (%,d)".format(r.avgRep) else "avg %,d".format(r.avgRep),
            Gold),
        Kpi("RANK", "#%,d".format(r.position),
            "of %,d · top %d%%".format(r.totalUsers, 100 - r.percentile),
            Cyan),
        Kpi("BADGES", s.badgeCount.toString(),
            "%,d total".format(data.community?.totalBadges ?: 0),
            Rune),
        Kpi("CONNECTIONS", s.connectionCount.toString(),
            if (s.connectionCount == 0) "Reach out" else "in your circle",
            Success),
        Kpi("POSTS", s.postCount.toString(),
            if (s.postCount == 0) "Share something" else "published",
            Cyan),
        Kpi("STREAK", s.checkinStreak.toString(),
            if (s.checkinLongestStreak > 0) "longest %,d".format(s.checkinLongestStreak) else "start today",
            Gold),
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        kpis.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { kpi ->
                    KpiTile(kpi, Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

private data class Kpi(
    val label: String,
    val value: String,
    val sub: String,
    val accent: Color,
)

@Composable
private fun KpiTile(kpi: Kpi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Void.copy(alpha = 0.6f))
            .border(
                width = 1.dp,
                color = kpi.accent.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(14.dp),
    ) {
        Text(
            text = kpi.label,
            style = MaterialTheme.typography.labelSmall,
            color = TextFaint,
            letterSpacing = 2.sp,
            fontSize = 9.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = kpi.value,
            style = MaterialTheme.typography.headlineMedium,
            color = kpi.accent,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = kpi.sub,
            style = MaterialTheme.typography.bodySmall,
            color = TextDim,
            fontSize = 11.sp,
        )
    }
}

/* ══════════════════════════════════════════════════════════════════
 * CHARTS
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun ChartsSection(data: DashboardResponse) {
    val r = data.rank ?: return
    val badges = data.badges

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        // ── Reputation Standing ─────────────────────────────────
        ChartCard(title = "Reputation Standing", subtitle = "You vs the platform") {
            BarRow("You", r.yourRep, r.yourRep.coerceAtLeast(r.avgRep), Gold)
            Spacer(Modifier.height(10.dp))
            BarRow("Platform Avg", r.avgRep, r.yourRep.coerceAtLeast(r.avgRep), Cyan)
            Spacer(Modifier.height(12.dp))
            val delta = r.yourRep - r.avgRep
            val sign = if (delta >= 0) "+" else ""
            Text(
                text = "$sign%,d vs platform average".format(delta),
                style = MaterialTheme.typography.bodySmall,
                color = if (delta >= 0) Success else Blood,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Badge Tier Distribution ─────────────────────────────
        ChartCard(title = "Badge Tiers", subtitle = "How your collection is distributed") {
            val tiers = listOf("bronze", "silver", "gold", "platinum", "valhalla")
            val counts = tiers.associateWith { t -> badges.count { it.tier == t } }
            val total = counts.values.sum()

            if (total == 0) {
                Text(
                    text = "Earn a badge to see your distribution.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextFaint,
                )
            } else {
                SegmentedTierBar(counts)
                Spacer(Modifier.height(12.dp))
                tiers.forEach { tier ->
                    val count = counts[tier] ?: 0
                    if (count > 0) {
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(tierColor(tier)),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = tier.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextDim,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = tierColor(tier),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Trajectory ─────────────────────────────────────────
        ChartCard(title = "Recent Trajectory", subtitle = "Your last ${data.activity.size} reputation events") {
            val events = data.activity.reversed()
            if (events.size < 2) {
                Text(
                    text = "Not enough activity yet. Come back after a few events.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextFaint,
                )
            } else {
                val series = buildTrajectory(r.yourRep, events)
                Sparkline(series, Modifier.fillMaxWidth().height(90.dp))
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "%,d".format(series.first()),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextFaint,
                    )
                    Text(
                        text = "%,d".format(series.last()),
                        style = MaterialTheme.typography.labelSmall,
                        color = Cyan,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    CitadelPanel(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = CyanBright,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextFaint,
            fontSize = 11.sp,
        )
        Spacer(Modifier.height(14.dp))
        content()
    }
}

@Composable
private fun BarRow(label: String, value: Int, maxValue: Int, color: Color) {
    val fraction = if (maxValue == 0) 0f else (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextDim,
            )
            Text(
                text = "%,d".format(value),
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(color.copy(alpha = 0.08f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(color),
            )
        }
    }
}

@Composable
private fun SegmentedTierBar(counts: Map<String, Int>) {
    val total = counts.values.sum().coerceAtLeast(1)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(999.dp)),
    ) {
        counts.forEach { (tier, count) ->
            if (count > 0) {
                Box(
                    modifier = Modifier
                        .weight(count.toFloat() / total.toFloat())
                        .height(14.dp)
                        .background(tierColor(tier)),
                )
            }
        }
    }
}

@Composable
private fun Sparkline(points: List<Int>, modifier: Modifier = Modifier) {
    val lineColor = Cyan
    val fillColor = Cyan.copy(alpha = 0.15f)

    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas
        val minV = points.minOrNull()?.toFloat() ?: 0f
        val maxV = points.maxOrNull()?.toFloat() ?: 1f
        val range = (maxV - minV).coerceAtLeast(1f)
        val stepX = size.width / (points.size - 1)

        val pts = points.mapIndexed { i, v ->
            Offset(i * stepX, size.height - ((v - minV) / range) * size.height)
        }

        // Filled area under the line
        val fill = Path().apply {
            moveTo(0f, size.height)
            pts.forEach { lineTo(it.x, it.y) }
            lineTo(size.width, size.height)
            close()
        }
        drawPath(fill, color = fillColor)

        // Line
        val line = Path().apply {
            moveTo(pts.first().x, pts.first().y)
            pts.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(line, color = lineColor, style = Stroke(width = 4f, cap = StrokeCap.Round))

        // Dots at each point
        pts.forEach { drawCircle(lineColor, radius = 4f, center = it) }
    }
}

private fun buildTrajectory(currentRep: Int, events: List<ActivityDto>): List<Int> {
    val windowSum = events.sumOf { it.delta }
    var running = currentRep - windowSum
    val series = mutableListOf(running)
    events.forEach {
        running += it.delta
        series.add(running)
    }
    return series
}

/* ══════════════════════════════════════════════════════════════════
 * MILESTONES
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun MilestonesSection(data: DashboardResponse) {
    val s = data.stats ?: return

    val thresholds = listOf(1, 5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 1000)

    val categories = listOf(
        Milestone("✎", "Posts",          s.postCount),
        Milestone("💬", "Comments",       s.commentGivenCount),
        Milestone("⚔", "Connections",    s.connectionCount),
        Milestone("👍", "Likes Given",    s.reactions.givenLike),
        Milestone("❤", "Hearts Given",   s.reactions.givenHeart),
        Milestone("📨", "Replies",        s.commentReceivedCount),
    )

    val cards = categories.mapNotNull { cat ->
        val next = thresholds.firstOrNull { it > cat.current } ?: return@mapNotNull null
        val prev = thresholds.lastOrNull { it <= cat.current } ?: 0
        val pct = if (next == prev) 1f else ((cat.current - prev).toFloat() / (next - prev).toFloat()).coerceIn(0f, 1f)
        MilestoneCard(cat.icon, cat.label, cat.current, next, pct)
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        SectionHeader("Next Milestones", "Progress toward your next badge")
        Spacer(Modifier.height(10.dp))

        if (cards.isEmpty()) {
            Text(
                text = "All standard milestones complete — well done, Citizen.",
                style = MaterialTheme.typography.bodySmall,
                color = TextFaint,
            )
        } else {
            cards.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    row.forEach { card -> Box(Modifier.weight(1f)) { card } }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

private data class Milestone(val icon: String, val label: String, val current: Int)

@Composable
private fun MilestoneCard(
    icon: String,
    label: String,
    current: Int,
    next: Int,
    progress: Float,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Void.copy(alpha = 0.6f))
            .border(1.dp, Cyan.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(Modifier.width(6.dp))
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                letterSpacing = 1.sp,
                fontSize = 9.sp,
            )
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Cyan.copy(alpha = 0.08f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Cyan),
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "$current / $next",
                style = MaterialTheme.typography.labelSmall,
                color = TextFaint,
                fontSize = 10.sp,
            )
            Text(
                text = "${next - current} to go",
                style = MaterialTheme.typography.labelSmall,
                color = Gold,
                fontSize = 10.sp,
            )
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * SECURITY + COMPLETENESS
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun StatusSplitSection(data: DashboardResponse) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        // Security
        SectionHeader("Account Security", "Protection status")
        Spacer(Modifier.height(10.dp))
        CitadelPanel(modifier = Modifier.fillMaxWidth()) {
            SecurityRow(
                label = "Email verification",
                ok = data.account?.emailVerified == true,
                okText = "Verified",
                failText = "Not verified",
            )
            SecurityRow(
                label = "Two-factor authentication",
                ok = false,
                okText = "Enabled",
                failText = "Disabled",
            )
            SecurityRow(
                label = "Account status",
                ok = data.account?.isActive == true,
                okText = "Active",
                failText = "Inactive",
            )
        }

        Spacer(Modifier.height(16.dp))

        // Completeness
        SectionHeader("Profile Completeness", "Flesh out your Citizen identity")
        Spacer(Modifier.height(10.dp))

        val items = listOf(
            "Display name"       to !data.identity?.displayName.isNullOrBlank(),
            "Tagline"            to !data.identity?.tagline.isNullOrBlank(),
            "Avatar"             to !data.identity?.avatarUrl.isNullOrBlank(),
            "Banner"             to !data.identity?.bannerUrl.isNullOrBlank(),
            "Email verified"     to (data.account?.emailVerified == true),
            "Made a post"        to ((data.stats?.postCount ?: 0) > 0),
            "Has a connection"   to ((data.stats?.connectionCount ?: 0) > 0),
        )
        val done = items.count { it.second }
        val pct = (done * 100) / items.size

        CitadelPanel(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CompletenessRing(pct)
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    items.forEach { (label, ok) ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = if (ok) "✓" else "○",
                                color = if (ok) Success else TextFaint,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (ok) TextFaint else TextDim,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityRow(
    label: String,
    ok: Boolean,
    okText: String,
    failText: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (ok) Success else Gold),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDim,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = (if (ok) okText else failText).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (ok) Success else Gold,
            letterSpacing = 1.sp,
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun CompletenessRing(pct: Int) {
    Box(
        modifier = Modifier.size(80.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 8f
            val diameter = size.minDimension - stroke
            // Background circle
            drawCircle(
                color = Cyan.copy(alpha = 0.15f),
                radius = diameter / 2,
                style = Stroke(width = stroke),
            )
            // Progress arc (starts at 12 o'clock, goes clockwise)
            drawArc(
                color = Cyan,
                startAngle = -90f,
                sweepAngle = 360f * (pct / 100f),
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        Text(
            text = "$pct%",
            style = MaterialTheme.typography.titleMedium,
            color = Cyan,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/* ══════════════════════════════════════════════════════════════════
 * BADGES
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun BadgeGallerySection(data: DashboardResponse) {
    val badges = data.badges
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        SectionHeader(
            title = "Earned Badges",
            meta = if (badges.isEmpty()) "None yet" else "${badges.size} earned",
        )
        Spacer(Modifier.height(10.dp))

        if (badges.isEmpty()) {
            CitadelPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "No badges yet. Your first one is waiting — post, " +
                            "comment, or make a connection to earn it.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextFaint,
                )
            }
        } else {
            badges.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    row.forEach { b -> BadgeCard(b, Modifier.weight(1f)) }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun BadgeCard(badge: BadgeDto, modifier: Modifier = Modifier) {
    val accent = tierColor(badge.tier)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Void.copy(alpha = 0.6f))
            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = badge.tier.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                letterSpacing = 1.sp,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            if (badge.isFeatured) {
                Text("★", color = Gold, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = badge.name,
            style = MaterialTheme.typography.titleSmall,
            color = GoldBright,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = badge.description,
            style = MaterialTheme.typography.bodySmall,
            color = TextDim,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            fontSize = 11.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Earned ${formatRelative(badge.earnedAt)}",
            style = MaterialTheme.typography.labelSmall,
            color = TextFaint,
            fontSize = 9.sp,
        )
    }
}

/* ══════════════════════════════════════════════════════════════════
 * ACTIVITY + REFERRAL
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun ActivityAndReferralSection(data: DashboardResponse) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        SectionHeader("Recent Activity", "Last 10 reputation events")
        Spacer(Modifier.height(10.dp))
        CitadelPanel(modifier = Modifier.fillMaxWidth()) {
            if (data.activity.isEmpty()) {
                Text(
                    text = "No reputation events yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextFaint,
                )
            } else {
                data.activity.forEach { a ->
                    ActivityRow(a)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        SectionHeader("Your Referral Link", "You both earn 500 rep")
        Spacer(Modifier.height(10.dp))
        val ref = data.stats?.referrals
        CitadelPanel(
            modifier = Modifier.fillMaxWidth(),
            borderColor = Gold.copy(alpha = 0.4f),
        ) {
            Text(
                text = ref?.code ?: "—",
                style = MaterialTheme.typography.headlineMedium,
                color = Gold,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 4.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ReferralStat(ref?.count ?: 0, "REFERRED", Modifier.weight(1f))
                ReferralStat(ref?.points ?: 0, "POINTS EARNED", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ActivityRow(a: ActivityDto) {
    val positive = a.delta > 0
    val sign = if (positive) "+" else ""
    val color = if (positive) Success else if (a.delta < 0) Blood else TextFaint

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$sign${a.delta}",
            style = MaterialTheme.typography.titleSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(56.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = a.reason.replace('_', ' ')
                    .replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
            )
            if (!a.note.isNullOrBlank()) {
                Text(
                    text = a.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextFaint,
                    fontSize = 11.sp,
                )
            }
        }
        Text(
            text = formatRelative(a.createdAt),
            style = MaterialTheme.typography.labelSmall,
            color = TextFaint,
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun ReferralStat(value: Int, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Cyan.copy(alpha = 0.04f))
            .border(1.dp, Cyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = CyanBright,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextFaint,
            letterSpacing = 1.sp,
            fontSize = 9.sp,
        )
    }
}

/* ══════════════════════════════════════════════════════════════════
 * ACTION BAR
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun ActionBar(onLogout: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CitadelButton(
                text = "Log Out",
                onClick = onLogout,
                style = CitadelButtonStyle.Danger,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * SHARED HELPERS
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun SectionHeader(title: String, meta: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = CyanBright,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        if (meta != null) {
            Text(
                text = meta.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextFaint,
                letterSpacing = 1.sp,
                fontSize = 10.sp,
            )
        }
    }
}

private fun greeting(): String {
    val h = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        h < 5  -> "The night is still young"
        h < 12 -> "Good morning"
        h < 17 -> "Good afternoon"
        h < 21 -> "Good evening"
        else   -> "Welcome back"
    }
}

private fun tierColor(tier: String): Color = when (tier.lowercase()) {
    "bronze"   -> Color(0xFFCD7F32)
    "silver"   -> Color(0xFFC0C0C0)
    "gold"     -> Color(0xFFFFC72C)
    "platinum" -> Color(0xFF00F0FF)
    "valhalla" -> Color(0xFFB026FF)
    else       -> Color(0xFF00E5FF)
}

private fun formatRelative(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    return try {
        val instant = java.time.Instant.parse(iso)
        val now = java.time.Instant.now()
        val diff = java.time.Duration.between(instant, now).seconds
        when {
            diff < 60         -> "just now"
            diff < 3600       -> "${diff / 60}m ago"
            diff < 86400      -> "${diff / 3600}h ago"
            diff < 604800     -> "${diff / 86400}d ago"
            else              -> {
                val fmt = java.time.format.DateTimeFormatter
                    .ofPattern("MMM d, yyyy")
                    .withZone(java.time.ZoneId.systemDefault())
                fmt.format(instant)
            }
        }
    } catch (_: Exception) {
        iso.take(10)
    }
}

private fun formatDate(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    return try {
        val instant = java.time.Instant.parse(iso)
        val fmt = java.time.format.DateTimeFormatter
            .ofPattern("MMMM d, yyyy")
            .withZone(java.time.ZoneId.systemDefault())
        fmt.format(instant)
    } catch (_: Exception) {
        iso.take(10)
    }
}