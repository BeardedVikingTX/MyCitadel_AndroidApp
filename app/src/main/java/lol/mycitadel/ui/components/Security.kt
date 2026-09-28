package lol.mycitadel.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import lol.mycitadel.app.ui.theme.Void

/* ═══════════════════════════════════════════════════════════════════════════
 * 01 — SECURITY FACT — big number stat
 * ========================================================================= */

@Composable
fun SecurityFact(
    value: String,
    unit: String? = null,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = CyanBright,
                fontSize = 30.sp,
                letterSpacing = 1.sp,
            )
            if (unit != null) {
                Spacer(Modifier.width(2.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelMedium,
                    color = Cyan,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextDim,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp,
            fontSize = 10.sp,
        )
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * 02 — MECHANISM CARD — icon + title + body
 * ========================================================================= */

@Composable
fun MechanismCard(
    icon: String,
    title: String,
    body: String,
    accentColor: Color = Cyan,
    modifier: Modifier = Modifier,
) {
    CitadelPanel(modifier = modifier.fillMaxWidth()) {
        Text(text = icon, fontSize = 26.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = accentColor,
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
 * 03 — SHIELD CARD — Stalker Shield items (violet theme)
 * ========================================================================= */

@Composable
fun ShieldCard(
    icon: String,
    title: String,
    body: String,
    accentColor: Color = Rune,
    modifier: Modifier = Modifier,
) {
    CitadelPanel(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(accentColor.copy(alpha = 0.15f), Color.Transparent),
                        ),
                    )
                    .border(1.dp, accentColor.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = icon, fontSize = 24.sp)
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDim,
        )
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * 04 — NO BACKDOOR ITEM — check icon + title + body (green theme)
 * ========================================================================= */

@Composable
fun NoBackdoorItem(
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
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Success.copy(alpha = 0.10f))
                .border(1.dp, Success.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "✗",
                color = Success,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = Success,
                letterSpacing = 1.sp,
                fontSize = 11.sp,
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
 * 05 — MATH CALLOUT — the Sigma panel
 * ========================================================================= */

@Composable
fun MathCallout(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    CitadelPanel(
        modifier = modifier.fillMaxWidth(),
        borderColor = Cyan.copy(alpha = 0.45f),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Σ",
                style = MaterialTheme.typography.displayLarge,
                color = CyanBright,
                fontSize = 56.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = CyanBright,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * 06 — BOUNTY TIER CARD
 * ========================================================================= */

@Composable
fun BountyTierCard(
    badge: String,
    title: String,
    intro: String,
    bullets: List<String>,
    note: String? = null,
    isCashTier: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val accent = if (isCashTier) Gold else Cyan

    CitadelPanel(
        modifier = modifier.fillMaxWidth(),
        borderColor = accent.copy(alpha = 0.45f),
    ) {
        // Badge
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
            color = if (isCashTier) GoldBright else CyanBright,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = intro,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDim,
        )

        Spacer(Modifier.height(12.dp))

        bullets.forEach { bullet ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "✓",
                    color = Success,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 10.dp),
                )
                Text(
                    text = bullet,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDim,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (note != null) {
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Gold.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(12.dp),
            ) {
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gold.copy(alpha = 0.85f),
                )
            }
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * 07 — BOUNTY SCOPE COLUMN
 * ========================================================================= */

@Composable
fun BountyScopeColumn(
    heading: String,
    items: List<String>,
    inScope: Boolean,
    modifier: Modifier = Modifier,
) {
    val accent = if (inScope) Success else Blood
    val marker = if (inScope) "✓" else "✗"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.04f))
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Text(
            text = heading.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = accent,
            letterSpacing = 1.sp,
        )
        Spacer(Modifier.height(12.dp))
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = marker,
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 10.dp),
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
 * 08 — BOUNTY RULE ITEM — numbered rule
 * ========================================================================= */

@Composable
fun BountyRuleItem(
    number: Int,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Cyan.copy(alpha = 0.10f))
                .border(1.dp, Cyan.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = CyanBright,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = CyanBright,
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = TextDim,
            )
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * 09 — LIMIT ITEM — red-bordered warning
 * ========================================================================= */

@Composable
fun LimitItem(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(IntrinsicSize.Min)
                .background(Blood, RoundedCornerShape(2.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFB0B0),
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = TextDim,
            )
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * 10 — REPORT CARD — email / HackerOne
 * ========================================================================= */

@Composable
fun ReportCard(
    badge: String,
    title: String,
    body: String,
    contactText: String,
    note: String,
    isPrimary: Boolean,
    onClickContact: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val accent = if (isPrimary) Cyan else Gold

    CitadelPanel(
        modifier = modifier.fillMaxWidth(),
        borderColor = accent.copy(alpha = 0.4f),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                color = if (isPrimary) CyanBright else GoldBright,
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(14.dp))

            if (onClickContact != null) {
                CitadelButton(
                    text = contactText,
                    onClick = onClickContact,
                    style = if (isPrimary) CitadelButtonStyle.Cyan else CitadelButtonStyle.Gold,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accent.copy(alpha = 0.08f))
                        .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = contactText,
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        letterSpacing = 1.sp,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = note,
                style = MaterialTheme.typography.bodySmall,
                color = TextFaint,
                textAlign = TextAlign.Center,
            )
        }
    }
}