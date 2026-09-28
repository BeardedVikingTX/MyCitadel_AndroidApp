package lol.mycitadel.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.Void
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import lol.mycitadel.app.R
/* ═══════════════════════════════════════════════════════════════════════════
 * SECTION HEADER — gold circle with number + heading
 * ========================================================================= */

@Composable
fun NumberedMark(number: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Gold.copy(alpha = 0.06f))
            .border(1.dp, Gold.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.labelLarge,
            color = Gold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
fun AboutSectionHeader(
    number: String,
    title: String,
    modifier: Modifier = Modifier,
    centered: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (centered) Arrangement.Center else Arrangement.Start,
    ) {
        NumberedMark(number)
        Spacer(Modifier.width(16.dp))
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            color = GoldBright,
            letterSpacing = 2.sp,
        )
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * STORY PARAGRAPHS
 * ========================================================================= */

@Composable
fun AboutParagraph(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = TextDim,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    )
}

@Composable
fun HighlightParagraph(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        // Cyan left border
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(IntrinsicSize.Min)
                .background(Cyan, RoundedCornerShape(2.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = CyanBright,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
fun AboutQuote(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(IntrinsicSize.Min)
                .background(Cyan, RoundedCornerShape(2.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = CyanBright.copy(alpha = 0.9f),
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * CHILD CARD — "For Our Children" section
 * ========================================================================= */

@Composable
fun ChildCard(
    icon: String,
    title: String,
    body: String,
    accentColor: Color = Cyan,
    modifier: Modifier = Modifier,
) {
    CitadelPanel(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(accentColor.copy(alpha = 0.15f), Color.Transparent),
                        ),
                    )
                    .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = icon, fontSize = 30.sp)
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * VALUE CARD — "What I Stand For" section
 * ========================================================================= */

@Composable
fun ValueCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    CitadelPanel(modifier = modifier.fillMaxWidth()) {
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
 * ROADMAP ITEM — "The Road Ahead" section
 * ========================================================================= */

enum class RoadmapStatus { Done, Current, Pending }

@Composable
fun RoadmapItem(
    title: String,
    body: String,
    status: RoadmapStatus,
    modifier: Modifier = Modifier,
) {
    val dotColor = when (status) {
        RoadmapStatus.Done    -> Success
        RoadmapStatus.Current -> Cyan
        RoadmapStatus.Pending -> TextFaint
    }

    val titleColor = when (status) {
        RoadmapStatus.Done    -> Success
        RoadmapStatus.Current -> CyanBright
        RoadmapStatus.Pending -> TextDim
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        // ── Left rail: vertical line + dot ─────────────────────────────
        Box(
            modifier = Modifier
                .width(24.dp)
                .fillMaxHeight(),
        ) {
            // Vertical line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                dotColor.copy(alpha = 0.6f),
                                dotColor.copy(alpha = 0.15f),
                            ),
                        ),
                    ),
            )
            // Dot
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = 4.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
        }

        // ── Right: content ────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, bottom = 24.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = titleColor,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
            )
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════════════
 * FOUNDER AVATAR — rotating dashed ring + rune medallion
 * ========================================================================= */

@Composable
fun FounderAvatar(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "avatar-ring")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 30_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "angle",
    )

    Box(
        modifier = modifier.size(150.dp),
        contentAlignment = Alignment.Center,
    ) {
        // ── Dashed rotating gold ring ───────────────────────────────────
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer { rotationZ = angle },
        ) {
            drawCircle(
                color = Gold.copy(alpha = 0.55f),
                radius = size.minDimension / 2 - 6f,
                style = Stroke(
                    width = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 22f), 0f),
                ),
            )
        }

        // ── Inner medallion with your photo ─────────────────────────────
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, Gold, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.owner_avatar),
                contentDescription = "Bearded Viking",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
