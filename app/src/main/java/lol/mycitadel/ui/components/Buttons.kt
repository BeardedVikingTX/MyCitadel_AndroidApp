package lol.mycitadel.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Rune
import lol.mycitadel.app.ui.theme.RuneBright

enum class CitadelButtonStyle { Cyan, Gold, Rune, Danger }

/**
 * The MyCitadel call-to-action button. Four color variants match
 * the web platform's .btn-cyber / .btn-gold / .btn-rune / .btn-danger.
 *
 * The `enabled` parameter is optional (defaults to true) so existing
 * call sites continue to work unchanged. When disabled, the button
 * greys out and stops accepting taps.
 */
@Composable
fun CitadelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CitadelButtonStyle = CitadelButtonStyle.Cyan,
    enabled: Boolean = true,
) {
    val (foreground, background, border) = when (style) {
        CitadelButtonStyle.Cyan   -> Triple(CyanBright,        Cyan.copy(alpha = 0.08f), Cyan)
        CitadelButtonStyle.Gold   -> Triple(GoldBright,        Gold.copy(alpha = 0.08f), Gold)
        CitadelButtonStyle.Rune   -> Triple(RuneBright,        Rune.copy(alpha = 0.08f), Rune)
        CitadelButtonStyle.Danger -> Triple(Color(0xFFFFD0D0), Blood.copy(alpha = 0.10f), Blood)
    }

    // When disabled, fade all three visual tokens
    val fg = if (enabled) foreground else foreground.copy(alpha = 0.4f)
    val bg = if (enabled) background else background.copy(alpha = 0.4f)
    val bd = if (enabled) border     else border.copy(alpha = 0.3f)

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = bg,
            contentColor = fg,
            disabledContainerColor = bg,
            disabledContentColor = fg,
        ),
        border = BorderStroke(1.dp, bd),
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}