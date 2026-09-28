package lol.mycitadel.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.Void

/* ═══════════════════════════════════════════════════════════════════════════
 * LEGAL META — effective / last updated line
 * ========================================================================= */

@Composable
fun LegalMeta(
    effectiveDate: String,
    lastUpdated: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "EFFECTIVE",
                style = MaterialTheme.typography.labelSmall,
                color = TextFaint,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = effectiveDate,
                style = MaterialTheme.typography.labelSmall,
                color = Cyan,
                letterSpacing = 1.sp,
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "LAST UPDATED",
                style = MaterialTheme.typography.labelSmall,
                color = TextFaint,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = lastUpdated,
                style = MaterialTheme.typography.labelSmall,
                color = Cyan,
                letterSpacing = 1.sp,
            )
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * LEGAL SUBHEAD — small section subtitle
 * ========================================================================= */

@Composable
fun LegalSubhead(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = CyanBright,
        modifier = modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

/* ═══════════════════════════════════════════════════════════════════════════
 * LEGAL CALLOUT — info (cyan) or warning (blood) box with left bar
 * ========================================================================= */

@Composable
fun LegalCallout(
    text: String,
    modifier: Modifier = Modifier,
    isWarning: Boolean = false,
) {
    val accent = if (isWarning) Gold else Cyan

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(48.dp) // fixed marker height — reads as a bar
                .background(accent, RoundedCornerShape(2.dp)),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDim,
            modifier = Modifier.weight(1f),
        )
    }
}

// Compose doesn't support intrinsic height on a bare Box easily in this
// context — use a padded marker instead. Replaces the .height call above.
private val IntrinsicSizeCompat = 200.dp

/* ═══════════════════════════════════════════════════════════════════════════
 * LEGAL BULLET LIST
 * ========================================================================= */

@Composable
fun LegalBulletList(
    items: List<String>,
    modifier: Modifier = Modifier,
    ordered: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = if (ordered) "${index + 1}." else "•",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Cyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .width(24.dp)
                        .padding(top = 2.dp),
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * PROHIBITION CARD — for the 8 prohibited conduct rules
 * ========================================================================= */

@Composable
fun ProhibitionCard(
    title: String,
    body: String,
    severe: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val accent = if (severe) Blood else Gold

    CitadelPanel(
        modifier = modifier.fillMaxWidth(),
        borderColor = accent.copy(alpha = 0.45f),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.12f))
                    .border(1.dp, accent.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "⛔",
                    fontSize = 16.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (severe) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Blood.copy(alpha = 0.18f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "IMMEDIATE TERMINATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = Blood,
                            letterSpacing = 1.sp,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (severe) Blood else GoldBright,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDim,
                )
            }
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * IP CARD — allowed / forbidden
 * ========================================================================= */

@Composable
fun IpCard(
    badge: String,
    title: String,
    items: List<String>,
    allowed: Boolean,
    modifier: Modifier = Modifier,
) {
    val accent = if (allowed) Success else Blood

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(accent.copy(alpha = 0.04f))
            .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(20.dp),
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(accent)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Text(
                text = badge.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Void,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = if (allowed) Success else Blood,
            fontSize = 22.sp,
        )

        Spacer(Modifier.height(12.dp))

        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = if (allowed) "✓" else "✗",
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .width(20.dp)
                        .padding(top = 2.dp),
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDim,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * CONTACT DEPARTMENT ROW
 * ========================================================================= */

@Composable
fun ContactDepartment(
    label: String,
    email: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Gold,
            letterSpacing = 1.sp,
            modifier = Modifier.width(110.dp),
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = Cyan,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Cyan.copy(alpha = 0.06f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * LEGAL ALLCAPS — for the disclaimer section
 * ========================================================================= */

@Composable
fun LegalAllCaps(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = TextDim,
        fontStyle = FontStyle.Normal,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    )
}