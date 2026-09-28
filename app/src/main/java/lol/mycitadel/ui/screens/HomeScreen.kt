package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.EvidenceCard
import lol.mycitadel.app.ui.components.EvidenceSource
import lol.mycitadel.app.ui.components.GlitchText
import lol.mycitadel.app.ui.components.GuaranteeItem
import lol.mycitadel.app.ui.components.PillarCard
import lol.mycitadel.app.ui.components.PremiumTeaser
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.components.SectionHeader
import lol.mycitadel.app.ui.components.StepCard
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.TextDim

private const val SECTION_PADDING = 24

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Helper: scroll to the evidence section (index 1)
    val scrollToEvidence: () -> Unit = {
        scope.launch { listState.animateScrollToItem(1) }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        /* ═══════════════════════════════════════════════════════════════
         * SECTION 1 — HERO
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                RuneDivider()

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

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Social media has become surveillance. We built the " +
                            "alternative — a platform where your data is encrypted " +
                            "before it ever leaves your device. No trackers. " +
                            "No ad networks. No compromise.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CitadelButton(
                        text = "Enter the Citadel",
                        onClick = { /* TODO: navigate to /register */ },
                        style = CitadelButtonStyle.Gold,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    CitadelButton(
                        text = "See the Evidence",
                        onClick = scrollToEvidence,
                        style = CitadelButtonStyle.Cyan,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * SECTION 2 — THE EVIDENCE
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = SECTION_PADDING.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SectionHeader(
                    title = "Why We Built This",
                    lede = "Every social platform you have ever used sells your " +
                            "attention. Here is what the public record shows about " +
                            "how they treat your data — and what it costs them when " +
                            "they get caught.",
                )

                Spacer(Modifier.height(24.dp))

                // ── Meta ────────────────────────────────────────────────
                EvidenceCard(
                    title = "Meta (Facebook & Instagram)",
                    statNumber = "43.8M",
                    statLabel = "Privacy Violations Found by Jury",
                    sources = listOf(
                        EvidenceSource(
                            "Jury found Facebook liable for 43.8 million violations of consumer protection law.",
                            "https://nmdoj.gov/press-release/jury-finds-facebook-violated-new-mexico-consumer-protection-law-faces-billions-in-potential-civil-penalties/",
                        ),
                        EvidenceSource(
                            "Ranked the most invasive app, collecting 32 of 35 possible data types.",
                            "https://techround.co.uk/news/facebook-instagram-invasive-apps-privacy-study/",
                        ),
                        EvidenceSource(
                            "Caught using a covert tracking tool on Android to link browsing data to identities even in incognito mode.",
                            "https://www.euractiv.com/section/tech/news/spain-probes-meta-over-privacy-abuse-of-millions-of-android-users/",
                        ),
                        EvidenceSource(
                            "Fined €1.2 billion for illegally transferring user data to the US.",
                            "https://gdprlocal.com/metas-e1-2-billion-gdpr-fine-why-it-still-matters-in-2025/",
                        ),
                    ),
                    cost = "Their Premium Fee: $11.99 – $14.99 / month",
                )

                Spacer(Modifier.height(16.dp))

                // ── TikTok ──────────────────────────────────────────────
                EvidenceCard(
                    title = "TikTok",
                    statNumber = "$400M",
                    statLabel = "Settlement for Children's Privacy Violations",
                    sources = listOf(
                        EvidenceSource(
                            "Agreed to a \$400 million settlement for violating children's privacy laws.",
                            "https://hunton.sitepilot11.firmseek.com/2026/09/doj-reaches-400-million-settlement-with-tiktok-over-childrens-privacy-litigation/",
                        ),
                        EvidenceSource(
                            "Fined €345 million under GDPR for its handling of children's personal data.",
                            "https://www.lexology.com/library/detail.aspx?g=8dd1b9fc-5e82-43ed-89b3-b1c34a2708ae",
                        ),
                    ),
                    cost = "Their Ad-Free Fee: ~\$5.50 / month",
                )

                Spacer(Modifier.height(16.dp))

                // ── X ───────────────────────────────────────────────────
                EvidenceCard(
                    title = "X (formerly Twitter)",
                    statNumber = "$150M",
                    statLabel = "FTC Fine for Deceptive Data Use",
                    sources = listOf(
                        EvidenceSource(
                            "Fined \$150 million for using phone numbers and emails provided for security to target ads, affecting 140 million users.",
                            "https://therecord.media/ftc-considers-modifying-150-million-twitter-privacy-fine",
                        ),
                        EvidenceSource(
                            "Under an FTC order that requires independent audits until 2042.",
                            "https://arstechnica.com/tech-policy/2026/06/elon-musk-tries-again-to-escape-ftc-audits-of-x-data-handling/",
                        ),
                    ),
                    cost = "Their Premium Fee: \$3 – \$40 / month",
                )

                Spacer(Modifier.height(16.dp))

                // ── LinkedIn ────────────────────────────────────────────
                EvidenceCard(
                    title = "LinkedIn",
                    statNumber = "€310M",
                    statLabel = "GDPR Fine for Targeted Advertising",
                    sources = listOf(
                        EvidenceSource(
                            "Fined €310 million for unlawfully processing personal data for behavioral analysis and targeted advertising without consent.",
                            "https://www.gdprbuzz.com/news/linkedin-fined-e310-million-for-misusing-personal-data/",
                        ),
                    ),
                    cost = "Their Premium Fee: \$29.99+ / month",
                )

                Spacer(Modifier.height(24.dp))

                // ── Quote ───────────────────────────────────────────────
                CitadelPanel(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "\"Why should you pay for a social media site, " +
                                "just to have them harvest your data, sell it to " +
                                "advertisers and political parties, and track your " +
                                "every move? They get your money and your data. " +
                                "That is not a business model. That is a shakedown.\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * SECTION 3 — THE MECHANISM
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = SECTION_PADDING.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SectionHeader(
                    title = "The Mechanism",
                    lede = "This is not marketing. This is how the platform is " +
                            "actually built. Every claim below has a corresponding " +
                            "line of code you can audit on GitHub.",
                )

                Spacer(Modifier.height(24.dp))

                PillarCard(
                    icon = "🔐",
                    title = "Encrypted at Rest",
                    body = "Email, phone, real name, address — every piece of " +
                            "personal data is envelope-encrypted with a key derived " +
                            "just for you. A stolen database yields nothing but ciphertext.",
                )

                Spacer(Modifier.height(12.dp))

                PillarCard(
                    icon = "🛡️",
                    title = "Argon2id Auth",
                    body = "256 MiB of memory cost per password hash. Even with " +
                            "our entire database in hand, a password cannot be " +
                            "recovered. Sessions are fingerprinted and rotate on " +
                            "every privilege change.",
                )

                Spacer(Modifier.height(12.dp))

                PillarCard(
                    icon = "⚔️",
                    title = "Zero Tracking",
                    body = "No analytics scripts. No third-party cookies. " +
                            "No fingerprinting. The only cookie we set is the one " +
                            "that keeps you logged in — and it never leaves our domain.",
                )

                Spacer(Modifier.height(12.dp))

                PillarCard(
                    icon = "👁️",
                    title = "Open Source",
                    body = "Every line of code is public on GitHub. Audit it " +
                            "yourself, or hire someone to. Security through " +
                            "architecture, not promises.",
                )

                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CitadelButton(
                        text = "View Our Source Code",
                        onClick = { /* TODO: open GitHub */ },
                        style = CitadelButtonStyle.Cyan,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    CitadelButton(
                        text = "Read Our Security Promise",
                        onClick = { /* TODO: navigate to /security */ },
                        style = CitadelButtonStyle.Cyan,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * SECTION 4 — HOW IT WORKS
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = SECTION_PADDING.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SectionHeader(
                    title = "How It Works",
                    lede = "Four steps from outsider to citizen.",
                )

                Spacer(Modifier.height(24.dp))

                StepCard(
                    number = "01",
                    title = "Register",
                    body = "Choose a username, provide an email, set a strong " +
                            "password. Verify ownership through a signed link.",
                )
                Spacer(Modifier.height(12.dp))
                StepCard(
                    number = "02",
                    title = "Harden",
                    body = "Enable two-factor authentication. Scan the QR code " +
                            "with any TOTP app. Save your recovery codes offline.",
                )
                Spacer(Modifier.height(12.dp))
                StepCard(
                    number = "03",
                    title = "Connect",
                    body = "Send connection requests to people you trust. " +
                            "Nobody sees your profile until you accept them — and vice versa.",
                )
                Spacer(Modifier.height(12.dp))
                StepCard(
                    number = "04",
                    title = "Engage",
                    body = "Post, comment, react. Every interaction is " +
                            "connection-gated and every privacy setting is honored completely.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * SECTION 5 — GUARANTEES
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = SECTION_PADDING.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SectionHeader(title = "Our Promises")

                Spacer(Modifier.height(16.dp))

                GuaranteeItem(
                    title = "We do not sell your data. Ever.",
                    body = "We have no business model that depends on knowing " +
                            "anything about you. Premium subscriptions are how this " +
                            "platform stays alive.",
                )
                GuaranteeItem(
                    title = "You can leave at any time.",
                    body = "Account deletion destroys your posts, comments, " +
                            "reactions, and connections — permanently. No soft-delete " +
                            "limbo, no data retention games.",
                )
                GuaranteeItem(
                    title = "We pay for bugs.",
                    body = "Once we launch, we will run a HackerOne bug bounty " +
                            "program. Security researchers are invited — and rewarded.",
                )
                GuaranteeItem(
                    title = "Security you can verify.",
                    body = "Our code is public on GitHub. Our threat model is " +
                            "documented. Our limits are published. Audit us.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * SECTION 6 — PREMIUM TEASER
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = SECTION_PADDING.dp),
            ) {
                PremiumTeaser(
                    onExplore = { /* TODO: navigate to /premium */ },
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * SECTION 7 — FINAL CTA
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
                    text = "Your Data Should Be Yours Alone.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Join the citizens building a better internet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                CitadelButton(
                    text = "Enter the Citadel",
                    onClick = { /* TODO: navigate to /register */ },
                    style = CitadelButtonStyle.Gold,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}