package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.TextDim

@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String = "This screen is coming soon.",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        RuneDivider()

        Spacer(Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = CyanBright,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        CitadelPanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = TextDim,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ",
            style = MaterialTheme.typography.labelLarge,
            color = Gold,
            letterSpacing = 8.sp,
        )
    }
}