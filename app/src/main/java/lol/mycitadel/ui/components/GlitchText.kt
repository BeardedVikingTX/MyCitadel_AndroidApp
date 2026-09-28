package lol.mycitadel.app.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.RuneBright

@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displayLarge,
    baseColor: Color = CyanBright,
    ghostColor1: Color = RuneBright,
    ghostColor2: Color = GoldBright,
) {
    val transition = rememberInfiniteTransition(label = "glitch")

    // Base layer: tiny jitter
    val baseX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4400
                0f at 0
                0f at 4100
                -1f at 4180
                1f at 4260
                0f at 4400
            },
        ),
        label = "baseX",
    )

    // Rune ghost
    val runeX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3600
                0f at 0
                0f at 3300
                3f at 3380
                -2f at 3460
                4f at 3540
                0f at 3600
            },
        ),
        label = "runeX",
    )
    val runeAlpha by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3600
                0f at 0
                0f at 3300
                0.85f at 3380
                0.9f at 3460
                0.7f at 3540
                0f at 3600
            },
        ),
        label = "runeAlpha",
    )

    // Gold ghost
    val goldX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5100
                0f at 0
                0f at 4800
                -4f at 4880
                2f at 4960
                -3f at 5030
                0f at 5100
            },
        ),
        label = "goldX",
    )
    val goldAlpha by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5100
                0f at 0
                0f at 4800
                0.75f at 4880
                0.8f at 4960
                0.65f at 5030
                0f at 5100
            },
        ),
        label = "goldAlpha",
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = style,
            color = baseColor,
            modifier = Modifier.graphicsLayer { translationX = baseX },
        )
        Text(
            text = text,
            style = style,
            color = ghostColor1,
            modifier = Modifier.graphicsLayer {
                translationX = runeX
                alpha = runeAlpha
            },
        )
        Text(
            text = text,
            style = style,
            color = ghostColor2,
            modifier = Modifier.graphicsLayer {
                translationX = goldX
                alpha = goldAlpha
            },
        )
    }
}