package lol.mycitadel.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint

enum class CitizenAction { Cyan, Gold, Danger, Ghost, Muted }

/**
 * Shared action button used in both the Citizens grid and the profile view.
 *
 * Extends RowScope so callers can chain Modifier.weight(1f) — the button
 * is always rendered inside a Row in practice.
 */
@Composable
fun RowScope.CitizenActionButton(
    label: String,
    style: CitizenAction,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val (border, text) = when (style) {
        CitizenAction.Cyan   -> Cyan to Cyan
        CitizenAction.Gold   -> Gold to Gold
        CitizenAction.Danger -> Blood to Blood
        CitizenAction.Ghost  -> TextDim.copy(alpha = 0.55f) to TextDim
        CitizenAction.Muted  -> TextFaint.copy(alpha = 0.5f) to TextFaint
    }
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .clickable(enabled = enabled && style != CitizenAction.Muted, onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = text,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}