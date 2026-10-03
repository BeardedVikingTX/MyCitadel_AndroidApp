package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.ui.components.AboutParagraph
import lol.mycitadel.app.ui.components.AboutQuote
import lol.mycitadel.app.ui.components.AboutSectionHeader
import lol.mycitadel.app.ui.components.ChildCard
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.FounderAvatar
import lol.mycitadel.app.ui.components.GlitchText
import lol.mycitadel.app.ui.components.HighlightParagraph
import lol.mycitadel.app.ui.components.RoadmapItem
import lol.mycitadel.app.ui.components.RoadmapStatus
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.components.ValueCard
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Rune
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    onNavigateToRegister: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
) {
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
                // Eyebrow pill
                Text(
                    text = "THE STORY",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gold,
                    letterSpacing = 4.sp,
                    modifier = Modifier
                        .border(1.dp, Gold.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                )

                Spacer(Modifier.height(24.dp))

                GlitchText(
                    text = "MyCitadel",
                    style = MaterialTheme.typography.displayLarge,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "A fortress built by a father",
                    style = MaterialTheme.typography.titleMedium,
                    color = Gold,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "This is not a startup. There is no venture capital, " +
                            "no board of directors, no growth-at-any-cost mandate. " +
                            "This is one person who decided the internet his children " +
                            "inherit should be better than the one we were given.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 2 — THE STORY
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "01", title = "The Story")

                Spacer(Modifier.height(24.dp))

                AboutParagraph(
                    "I am a father of four. Three daughters and a son. The oldest " +
                            "is old enough to use social media. The youngest is not yet. " +
                            "Every single day I watch the older ones navigate a world " +
                            "where the apps on their phones are designed to extract as " +
                            "much of their attention, identity, and behavior as possible " +
                            "— and sell it to the highest bidder.",
                )

                AboutParagraph(
                    "I have spent my career in technology. I know what happens " +
                            "under the hood. I know how the tracking pixels work. I " +
                            "know how the recommendation algorithms are tuned. I know " +
                            "what a \"shadow profile\" is, and I know that even people " +
                            "who have never created a Facebook account have one — " +
                            "built from browsing data, phone contacts, and inference.",
                )

                AboutParagraph(
                    "For years, I told myself I was overreacting. I told myself " +
                            "that \"everyone uses these platforms\" and that my concern " +
                            "was paranoia. Then I read a leaked document. Then another. " +
                            "Then I watched a regulator fine a company the size of a " +
                            "small country for what they had done to children. And I " +
                            "stopped telling myself anything.",
                )

                HighlightParagraph("I built the alternative because my kids deserve one.")

                AboutParagraph(
                    "Not a louder platform. Not a prettier one. A different one. " +
                            "One where the business model does not depend on knowing " +
                            "anything about you. One where a stranger cannot look up " +
                            "your daughter by name and find her school. One where the " +
                            "servers themselves cannot read what you wrote to the " +
                            "people you love.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 3 — FOR OUR CHILDREN
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AboutSectionHeader(number = "02", title = "For Our Children")

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "A platform where a parent does not have to wonder what " +
                            "is being done with their kid's data — because nothing is " +
                            "being done with it at all.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                ChildCard(
                    icon = "🛡️",
                    title = "Their identity stays theirs",
                    body = "No behavioral fingerprints. No shadow profiles. No " +
                            "third-party analytics watching how long they hover over " +
                            "a photo. What they see, they see. That is all.",
                    accentColor = Cyan,
                )
                Spacer(Modifier.height(12.dp))
                ChildCard(
                    icon = "📡",
                    title = "Visibility is a choice",
                    body = "Nothing about your child is visible to strangers by " +
                            "default. Connections are mutual. Profiles are gated. " +
                            "Blocks are silent. Hidden means hidden — even from us.",
                    accentColor = Gold,
                )
                Spacer(Modifier.height(12.dp))
                ChildCard(
                    icon = "🔒",
                    title = "Private by architecture",
                    body = "Messages are encrypted before they leave the device. " +
                            "Photos are encrypted at rest. Even if our servers were " +
                            "seized tomorrow, what would be found is unreadable.",
                    accentColor = Rune,
                )
                Spacer(Modifier.height(12.dp))
                ChildCard(
                    icon = "☀️",
                    title = "Yours to leave",
                    body = "When they grow up and want out, deletion is real. " +
                            "Not soft-deleted, not archived, not \"hidden.\" " +
                            "Destroyed — posts, comments, connections, and identity. Gone.",
                    accentColor = Success,
                )

                Spacer(Modifier.height(24.dp))

                AboutQuote(
                    "The internet did not have to become a surveillance machine. " +
                            "It became one because we let it. It does not have to stay " +
                            "this way — and this is my small attempt at making sure " +
                            "it does not.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 4 — WHAT I STAND FOR
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AboutSectionHeader(number = "03", title = "What I Stand For")

                Spacer(Modifier.height(24.dp))

                ValueCard(
                    title = "Privacy is not a feature.",
                    body = "It is the default. Anything less is a compromise " +
                            "dressed up as a setting, waiting to be quietly reversed " +
                            "by a policy update.",
                )
                Spacer(Modifier.height(12.dp))
                ValueCard(
                    title = "Security through mathematics.",
                    body = "Encryption is not a marketing bullet. It is the " +
                            "reason a stolen database is worthless. We do not trust " +
                            "promises. We trust proofs.",
                )
                Spacer(Modifier.height(12.dp))
                ValueCard(
                    title = "Transparency earns trust.",
                    body = "Our code is open source. Our threat model is " +
                            "documented. Our limits are published. If we ever betray " +
                            "these principles, the record will show it.",
                )
                Spacer(Modifier.height(12.dp))
                ValueCard(
                    title = "Slow is fine.",
                    body = "We would rather be correct than fast. Platforms that " +
                            "chase growth at any cost end up selling users to survive. " +
                            "That is not a trap we intend to walk into.",
                )
                Spacer(Modifier.height(12.dp))
                ValueCard(
                    title = "You can always leave.",
                    body = "No hostage data. No retention games. No soft delete. " +
                            "Deleting your account destroys everything, permanently, " +
                            "on the day you ask.",
                )
                Spacer(Modifier.height(12.dp))
                ValueCard(
                    title = "Communities over algorithms.",
                    body = "We do not manipulate your feed to maximize engagement. " +
                            "You see what your connections post, in order. Nothing " +
                            "more, nothing less.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 5 — THE ROAD AHEAD
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AboutSectionHeader(number = "04", title = "The Road Ahead")

                Spacer(Modifier.height(24.dp))

                RoadmapItem(
                    title = "Backend — Complete",
                    body = "Authentication, 2FA, profiles, uploads, posts, " +
                            "comments, reactions, connections, notifications, " +
                            "Web Push, and premium subscriptions.",
                    status = RoadmapStatus.Done,
                )
                RoadmapItem(
                    title = "Web Frontend — In Progress",
                    body = "Marketing pages, account flows, dashboard, profile " +
                            "editor, feed. Public launch preparation.",
                    status = RoadmapStatus.Current,
                )
                RoadmapItem(
                    title = "Android App",
                    body = "Native client for Android devices, sharing the same " +
                            "zero-knowledge API. Push notifications via FCM.",
                    status = RoadmapStatus.Current,
                )
                RoadmapItem(
                    title = "Bug Bounty Program",
                    body = "HackerOne launch with reputation and merchandise " +
                            "rewards for responsible disclosure. Security researchers " +
                            "as allies, not adversaries.",
                    status = RoadmapStatus.Pending,
                )
                RoadmapItem(
                    title = "iOS App",
                    body = "Apple client for iOS devices. Requires Apple " +
                            "Developer Program enrollment.",
                    status = RoadmapStatus.Pending,
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 6 — WHO BUILT THIS
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AboutSectionHeader(number = "05", title = "Who Built This")

                Spacer(Modifier.height(24.dp))

                CitadelPanel(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        FounderAvatar()

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = "Bearded Viking",
                            style = MaterialTheme.typography.headlineMedium,
                            color = GoldBright,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "Self-taught builder. Father of four. Privacy absolutist.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gold,
                            textAlign = TextAlign.Center,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Light,
                        )

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = "Building MyCitadel alone, one commit at a " +
                                    "time, between school runs and bedtime stories. " +
                                    "Not because it is easy — but because the " +
                                    "platform that should exist does not yet, and " +
                                    "I am not willing to wait for someone else to " +
                                    "build it.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextDim,
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "If any of this resonates with you — if you " +
                                    "are a parent who has felt the same unease, a " +
                                    "developer who sees the same rot, or just " +
                                    "someone who thinks the internet can be " +
                                    "better — reach out. This is a long road, and " +
                                    "it is one worth walking together.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextDim,
                        )

                        Spacer(Modifier.height(24.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            CitadelButton(
                                text = "BeardedViking.org",
                                onClick = { /* TODO: open beardedviking.org */ },
                                style = CitadelButtonStyle.Cyan,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            CitadelButton(
                                text = "GitHub",
                                onClick = { /* TODO: open GitHub */ },
                                style = CitadelButtonStyle.Cyan,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            CitadelButton(
                                text = "Reach Out",
                                onClick = { /* TODO: navigate to Contact */ },
                                style = CitadelButtonStyle.Gold,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 7 — FINAL CTA
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
                    text = "Build It With Us.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "A better internet is not a slogan. It is a choice, " +
                            "made one user at a time.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                CitadelButton(
                    text = "Enter the Citadel",
                    onClick = onNavigateToRegister,
                    style = CitadelButtonStyle.Gold,
                    modifier = Modifier.fillMaxWidth(),
                )
                TextButton(
                    onClick = onNavigateToLogin,
                ) {
                    Text(
                        text = "Already a Citizen? Log In",
                        color = Cyan,
                        fontSize = 13.sp,
                    )
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

