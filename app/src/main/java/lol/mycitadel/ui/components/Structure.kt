package lol.mycitadel.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.TextDim

/**
 * The Elder Futhark rune divider used throughout the platform.
 */
@Composable
fun RuneDivider(
    modifier: Modifier = Modifier,
    spacing: Int = 6,
) {
    Text(
        text = "ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ",
        style = MaterialTheme.typography.labelLarge,
        color = Gold,
        letterSpacing = spacing.sp,
        modifier = modifier,
    )
}

/**
 * Standard section heading with optional lede paragraph.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    lede: String? = null,
    centered: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = GoldBright,
            textAlign = if (centered) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )

        if (!lede.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = lede,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
                textAlign = if (centered) TextAlign.Center else TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}