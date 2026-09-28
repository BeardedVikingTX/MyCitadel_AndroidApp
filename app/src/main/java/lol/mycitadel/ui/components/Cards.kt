package lol.mycitadel.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.TextPrimary

/* ═══════════════════════════════════════════════════════════════════════════
 * EVIDENCE CARD — used in the "Why We Built This" section
 * ========================================================================= */

data class EvidenceSource(
    val text: String,
    val url: String? = null,
)

@Composable
fun EvidenceCard(
    title: String,
    statNumber: String,
    statLabel: String,
    sources: List<EvidenceSource>,
    cost: String,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    CitadelPanel(modifier = modifier.fillMaxWidth()) {
        // ── Platform name (red) ───────────────────────────────────────
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Blood,
        )

        Spacer(Modifier.height(16.dp))

        // ── Big number stat ───────────────────────────────────────────
        Text(
            text = statNumber,
            style = MaterialTheme.typography.headlineLarge,
            color = Blood,
            fontSize = 34.sp,
            letterSpacing = 2.sp,
        )
        Text(
            text = statLabel.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = TextFaint,
            letterSpacing = 1.sp,
        )

        Spacer(Modifier.height(16.dp))

        // ── Source list ──────────────────────────────────────────────
        sources.forEach { src ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "✗",
                    color = Blood,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = src.text,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDim,
                    )
                    if (src.url != null) {
                        Text(
                            text = "Source ↗",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanBright.copy(alpha = 0.7f),
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .clickable { uriHandler.openUri(src.url) },
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Cost line ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(
                text = cost,
                style = MaterialTheme.typography.labelMedium,
                color = Blood.copy(alpha = 0.85f),
            )
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * PILLAR CARD — "The Mechanism" section
 * ========================================================================= */

@Composable
fun PillarCard(
    icon: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    CitadelPanel(modifier = modifier.fillMaxWidth()) {
        Text(
            text = icon,
            fontSize = 28.sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = CyanBright,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDim,
        )
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * STEP CARD — "How It Works" section
 * ========================================================================= */

@Composable
fun StepCard(
    number: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    CitadelPanel(modifier = modifier.fillMaxWidth()) {
        Text(
            text = number,
            style = MaterialTheme.typography.headlineMedium,
            color = CyanBright,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = GoldBright,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDim,
        )
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * GUARANTEE ITEM — "Our Promises" section
 * ========================================================================= */

@Composable
fun GuaranteeItem(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Green check circle
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Success.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "✓",
                color = Success,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }

        Spacer(Modifier.size(14.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = CyanBright,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
            )
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * PREMIUM TEASER — the $10/month callout
 * ========================================================================= */

@Composable
fun PremiumTeaser(
    onExplore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CitadelPanel(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Gold)
                    .padding(horizontal = 14.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "PREMIUM",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF05070A),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "$10 / month",
                style = MaterialTheme.typography.headlineLarge,
                color = GoldBright,
                letterSpacing = 2.sp,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Advanced features, priority support, higher limits, " +
                        "and exclusive badges. Cancel anytime, no hidden fees. " +
                        "Billed securely through Stripe.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
            )

            Spacer(Modifier.height(20.dp))

            CitadelButton(
                text = "Explore Premium",
                onClick = onExplore,
                style = CitadelButtonStyle.Gold,
            )
        }
    }
}