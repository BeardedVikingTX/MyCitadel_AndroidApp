package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.GlitchText
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.TextDim

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(Modifier.height(60.dp))

        Text(
            text = "ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ",
            style = MaterialTheme.typography.labelLarge,
            color = Gold,
            letterSpacing = 8.sp,
        )

        Spacer(Modifier.height(24.dp))

        GlitchText(
            text = "MyCitadel",
            style = MaterialTheme.typography.displayLarge,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Your Digital Fortress",
            style = MaterialTheme.typography.titleMedium,
            color = Gold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(40.dp))

        CitadelPanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "A social platform built the way it should have been " +
                        "from the start. Encrypted before your data leaves your " +
                        "device. No trackers. No compromise.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextDim,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(16.dp))

        CitadelPanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "🔐  Encrypted at Rest",
                style = MaterialTheme.typography.titleMedium,
                color = CyanBright,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Every piece of personal data is envelope-encrypted " +
                        "with a key derived just for you.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
            )
        }

        Spacer(Modifier.height(16.dp))

        CitadelPanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "🛡️  Argon2id Auth",
                style = MaterialTheme.typography.titleMedium,
                color = CyanBright,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "256 MiB of memory cost per password hash. " +
                        "Unrecoverable even from a stolen database.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
            )
        }

        Spacer(Modifier.height(16.dp))

        CitadelPanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "⚔️  Zero Tracking",
                style = MaterialTheme.typography.titleMedium,
                color = CyanBright,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "No analytics scripts. No third-party cookies. " +
                        "No fingerprinting. Ever.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
            )
        }

        Spacer(Modifier.height(48.dp))
    }
}