package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.ui.components.AboutQuote
import lol.mycitadel.app.ui.components.AboutSectionHeader
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.GlitchText
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.Void

@Composable
fun ContactScreen(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        /* ═══════════════════════════════════════════════════════════════
         * 1 — HERO
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "GET IN TOUCH",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gold,
                    letterSpacing = 4.sp,
                    modifier = Modifier
                        .border(1.dp, Gold.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                )

                Spacer(Modifier.height(24.dp))

                GlitchText(
                    text = "Contact",
                    style = MaterialTheme.typography.displayLarge,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "The inbox is real. So is the person on the other end.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Gold,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Whether you have a question, a bug report, a " +
                            "partnership idea, or just want to say the thing you " +
                            "wish existed is being built — write to us. Every " +
                            "message gets read.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 2 — DIRECT CHANNELS (now first — this is how you reach us)
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AboutSectionHeader(number = "01", title = "Reach Us Directly")

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Two inboxes. Both monitored. Both read by a " +
                            "human — not a ticket queue.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(20.dp))

                DirectChannelCard(
                    badge = "General",
                    title = "info@mycitadel.lol",
                    body = "Questions, feedback, partnership ideas, " +
                            "press inquiries, or anything else.",
                    accentColor = Cyan,
                    onClick = {
                        uriHandler.openUri("mailto:info@mycitadel.lol")
                    },
                )

                Spacer(Modifier.height(12.dp))

                DirectChannelCard(
                    badge = "Security",
                    title = "security@mycitadel.lol",
                    body = "Vulnerability disclosures and bug bounty " +
                            "submissions. See the Security page for rules.",
                    accentColor = Gold,
                    onClick = {
                        uriHandler.openUri(
                            "mailto:security@mycitadel.lol?subject=Security%20Disclosure"
                        )
                    },
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "A web contact form is on the roadmap and will " +
                            "be added once the API endpoint is live. Until then, " +
                            "email is the way.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextFaint,
                    textAlign = TextAlign.Center,
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 3 — BEFORE YOU WRITE
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "02", title = "Before You Write")

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "A few things you might find answers to already " +
                            "— so we do not waste your time.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDim,
                )

                Spacer(Modifier.height(16.dp))

                AboutQuote(
                    "Check the Security page first if your question is " +
                            "about how your data is protected. The full threat " +
                            "model is already published there.",
                )

                Spacer(Modifier.height(12.dp))

                AboutQuote(
                    "Check the About page if you want the story behind " +
                            "MyCitadel — why it exists, who is building it, and " +
                            "where it is going.",
                )

                Spacer(Modifier.height(12.dp))

                AboutQuote(
                    "If you are reporting a security issue, please use " +
                            "security@mycitadel.lol instead of the general " +
                            "inbox. It routes to the same person, but faster.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 4 — WHAT TO EXPECT
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "03", title = "What to Expect")

                Spacer(Modifier.height(20.dp))

                CitadelPanel(modifier = Modifier.fillMaxWidth()) {
                    ExpectationRow(
                        icon = "⏱",
                        title = "48 hours",
                        body = "Standard response time for general messages.",
                    )
                    Spacer(Modifier.height(16.dp))
                    ExpectationRow(
                        icon = "🚨",
                        title = "72 hours",
                        body = "Acknowledgment for security disclosures.",
                    )
                    Spacer(Modifier.height(16.dp))
                    ExpectationRow(
                        icon = "🛠",
                        title = "7 days",
                        body = "Remediation target for critical findings.",
                    )
                    Spacer(Modifier.height(16.dp))
                    ExpectationRow(
                        icon = "🧑‍💻",
                        title = "One person",
                        body = "Every reply comes from the founder, not a bot.",
                    )
                }
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 5 — FINAL CTA
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                RuneDivider()

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "No Message Gets Ignored",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "You will hear back from a human, not a ticket " +
                            "number.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                CitadelButton(
                    text = "Read the About Page",
                    onClick = {
                        // TODO: navigate to About tab
                    },
                    style = CitadelButtonStyle.Cyan,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

/* ── Direct channel card ───────────────────────────────────────────────── */
@Composable
private fun DirectChannelCard(
    badge: String,
    title: String,
    body: String,
    accentColor: Color,
    onClick: () -> Unit,
) {
    CitadelPanel(
        modifier = Modifier.fillMaxWidth(),
        borderColor = accentColor.copy(alpha = 0.4f),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(accentColor)
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
                style = MaterialTheme.typography.titleLarge,
                color = accentColor,
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(14.dp))

            CitadelButton(
                text = "Open Mail App",
                onClick = onClick,
                style = if (accentColor == Gold) CitadelButtonStyle.Gold else CitadelButtonStyle.Cyan,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/* ── Expectation row ───────────────────────────────────────────────────── */
@Composable
private fun ExpectationRow(
    icon: String,
    title: String,
    body: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = icon, fontSize = 28.sp)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = CyanBright,
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