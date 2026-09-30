package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.EvidenceCard
import lol.mycitadel.app.ui.components.EvidenceSource
import lol.mycitadel.app.ui.components.GlitchText
import lol.mycitadel.app.ui.components.GuaranteeItem
import lol.mycitadel.app.ui.components.PillarCard
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.components.SectionHeader
import lol.mycitadel.app.ui.components.StepCard
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Rune
import lol.mycitadel.app.ui.theme.RuneBright
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.TextPrimary
import lol.mycitadel.app.ui.theme.Void

private const val SECTION_PADDING = 24

/* ═══════════════════════════════════════════════════════════════════════════
 * HOME SCREEN — v2
 * ---------------------------------------------------------------------------
 * Section order matches mycitadel.lol/index.php:
 *   01 Hero (with stats strip + fine print)
 *   02 Evidence
 *   03 Mechanism
 *   04 Arsenal (9 feature cards)
 *   05 Tiers (Free vs Premium — pricing cards + comparison rows)
 *   06 Stalker Shield preview
 *   07 How It Works
 *   08 Promises (5 items)
 *   09 Final CTA
 * ======================================================================== */

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Scroll targets: index of "Tiers" item in the LazyColumn
    // Hero=0, Evidence=1, Mechanism=2, Arsenal=3, Tiers=4
    val scrollToTiers: () -> Unit = {
        scope.launch { listState.animateScrollToItem(4) }
    }
    val scrollToEvidence: () -> Unit = {
        scope.launch { listState.animateScrollToItem(1) }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        item { HeroSection(onSeeTiers = scrollToTiers, onSeeEvidence = scrollToEvidence) }
        item { EvidenceSection() }
        item { MechanismSection() }
        item { ArsenalSection() }
        item { TiersSection() }
        item { ShieldPreviewSection() }
        item { HowItWorksSection() }
        item { PromisesSection() }
        item { FinalCtaSection() }
    }
}


/* ═══════════════════════════════════════════════════════════════════════════
 * 01 — HERO
 * ======================================================================== */

@Composable
private fun HeroSection(
    onSeeTiers: () -> Unit,
    onSeeEvidence: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 48.dp),
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
            text = "Every social platform you use sells your attention. " +
                    "We built the one that cannot — because we never see " +
                    "your data in the first place. Encrypted before it " +
                    "leaves your device. No trackers. No ad networks. " +
                    "No compromise.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextDim,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
        )

        Spacer(Modifier.height(32.dp))

        // ── CTA buttons ──────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CitadelButton(
                text = "Enter the Citadel",
                onClick = { /* TODO: navigate to /register */ },
                style = CitadelButtonStyle.Gold,
                modifier = Modifier.fillMaxWidth(),
            )
            CitadelButton(
                text = "See Free vs Premium",
                onClick = onSeeTiers,
                style = CitadelButtonStyle.Cyan,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(36.dp))

        // ── Stats strip ──────────────────────────────────────────
        HeroStatsStrip()

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Free forever. Premium at $10/month if you want higher limits.",
            style = MaterialTheme.typography.bodySmall,
            color = TextFaint,
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
private fun HeroStatsStrip() {
    val stats = listOf(
        "0"      to "third-party trackers",
        "0"      to "ad networks",
        "0"      to "data sold",
        "100%"   to "open source",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Two rows of two stats each — fits phone width cleanly
        stats.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { (value, label) ->
                    StatTile(
                        value = value,
                        label = label,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTile(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Void.copy(alpha = 0.5f))
            .border(1.dp, Cyan.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = CyanBright,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextFaint,
            letterSpacing = 1.sp,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
        )
    }
}


/* ═══════════════════════════════════════════════════════════════════════════
 * 02 — EVIDENCE
 * ======================================================================== */

@Composable
private fun EvidenceSection() {
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

        CitadelPanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "\"Why should you pay for a social media site, just to " +
                        "have them harvest your data, sell it to advertisers " +
                        "and political parties, and track your every move? " +
                        "They get your money and your data. That is not a " +
                        "business model. That is a shakedown.\"",
                style = MaterialTheme.typography.bodyLarge,
                color = Gold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )
        }
    }
}


/* ═══════════════════════════════════════════════════════════════════════════
 * 03 — MECHANISM
 * ======================================================================== */

@Composable
private fun MechanismSection() {
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
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


/* ═══════════════════════════════════════════════════════════════════════════
 * 04 — ARSENAL (new)
 * ======================================================================== */

@Composable
private fun ArsenalSection() {
    val features = listOf(
        Triple("⚔", "Connections, Not Followers",
            "Mutual by design. Nobody sees your profile, posts, or comments " +
                    "unless you accept them first — and vice versa. Blocking is " +
                    "silent, mutual, and permanent."),
        Triple("📡", "Posts With Real Privacy",
            "Every post carries its own visibility — public, connections-only, " +
                    "or private to yourself. Attach images, video, audio, " +
                    "documents, or archives. Edit freely. Delete for real."),
        Triple("💬", "Threaded Comments",
            "Reply to replies. The conversation stays coherent. Comment " +
                    "visibility follows the post's own visibility — no leaks, " +
                    "no gotchas."),
        Triple("❤", "Reactions That Say Something",
            "Like, dislike, heart, angry. Real emotional vocabulary, not " +
                    "just a binary thumb. Reaction counts on every post."),
        Triple("✉", "Encrypted Messaging",
            "Direct conversations with your connections. Attach files. " +
                    "Every message encrypted at rest. Sever a connection and " +
                    "the whole conversation is destroyed — not archived, destroyed."),
        Triple("🔔", "Real-Time Notifications",
            "Web Push to your browser. Know when someone connects, comments, " +
                    "or reacts — without a tab open. No tracking pixel hiding " +
                    "in the notification payload."),
        Triple("🏆", "Reputation & Badges",
            "Earn reputation for posting, commenting, connecting, and helping. " +
                    "Unlock badges for milestones. Every award is recorded in " +
                    "an immutable ledger you can inspect."),
        Triple("🎨", "Full Profile Control",
            "Avatar, banner, wallpaper, accent color, fonts, borders, " +
                    "background music. 60+ fields. Every piece of PII optional " +
                    "and encrypted. Your space, your rules."),
        Triple("🔍", "Discovery Without Exposure",
            "Search by username or display name. Blocked users never appear. " +
                    "Hidden accounts return 404 — never \"you cannot view this " +
                    "user.\" No existence leak."),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = SECTION_PADDING.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionHeader(
            title = "The Arsenal",
            lede = "Everything you need to actually live online — without " +
                    "giving your life away to do it. All features below are " +
                    "live today.",
        )

        Spacer(Modifier.height(24.dp))

        // Two cards per row — reads well even on small phones
        features.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                pair.forEach { (icon, title, body) ->
                    ArsenalCard(
                        icon = icon,
                        title = title,
                        body = body,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ArsenalCard(
    icon: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Void.copy(alpha = 0.55f))
            .border(1.dp, Cyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Cyan.copy(alpha = 0.08f))
                .border(1.dp, Cyan.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(icon, fontSize = 18.sp)
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = CyanBright,
            letterSpacing = 0.8.sp,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            lineHeight = 14.sp,
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            color = TextDim,
            fontSize = 11.sp,
            lineHeight = 16.sp,
        )
    }
}


/* ═══════════════════════════════════════════════════════════════════════════
 * 05 — TIERS (Free vs Premium)
 * ======================================================================== */

@Composable
private fun TiersSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = SECTION_PADDING.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionHeader(
            title = "Free vs Premium",
            lede = "MyCitadel is free forever. Premium is for Citizens who " +
                    "want more room, more reach, and more tools — and it is " +
                    "the only thing keeping this platform alive. No ads. No " +
                    "data sale. Just \$10/month if you choose it.",
        )

        Spacer(Modifier.height(24.dp))

        // ── Pricing cards ────────────────────────────────────────
        TierPricingCard(
            badge = "FREE",
            name = "Forever",
            price = "$0",
            priceUnit = "/month",
            tagline = "The full platform. Free is not a demo — it is " +
                    "the Citadel, and it always will be.",
            highlights = listOf(
                "Post up to 50 characters + 1 image",
                "Comment up to 25 characters",
                "Like & dislike reactions",
                "1-on-1 encrypted messaging",
                "All privacy & connection controls",
            ),
            ctaText = "Create free account",
            ctaStyle = CitadelButtonStyle.Cyan,
            isPremium = false,
            onCta = { /* TODO: navigate to /register */ },
        )

        Spacer(Modifier.height(16.dp))

        TierPricingCard(
            badge = "PREMIUM",
            name = "Citadel+",
            price = "$10",
            priceUnit = "/month",
            tagline = "Higher limits, all four reactions, group messaging " +
                    "with admin controls, and a badge that announces itself " +
                    "everywhere you appear.",
            highlights = listOf(
                "Post up to 1,500 chars + 10 attachments",
                "Comment up to 1,500 chars + 5 attachments",
                "All reactions: like, dislike, heart, angry",
                "Group messaging + admin moderation",
                "+2,500 reputation every month",
            ),
            ctaText = "Go Premium",
            ctaStyle = CitadelButtonStyle.Gold,
            isPremium = true,
            onCta = { /* TODO: navigate to /register */ },
        )

        Spacer(Modifier.height(36.dp))

        // ── Full comparison ──────────────────────────────────────
        Text(
            text = "FULL COMPARISON",
            style = MaterialTheme.typography.labelLarge,
            color = GoldBright,
            letterSpacing = 3.sp,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))

        // Posts
        TierGroupHeader("Posts")
        TierCompareRow("Post length",
            free = "50 characters",
            premium = "1,500 characters")
        TierCompareRow("Attachments per post",
            free = "1 image",
            premium = "Up to 10 — images, video, audio, documents, archives")
        TierCompareRow("Post visibility",
            free = "Public · Connections · Private",
            premium = "Public · Connections · Private",
            freeCheck = true,
            premiumCheck = true)

        // Comments
        Spacer(Modifier.height(12.dp))
        TierGroupHeader("Comments")
        TierCompareRow("Comment length",
            free = "25 characters",
            premium = "1,500 characters")
        TierCompareRow("Attachments per comment",
            free = "1 image",
            premium = "Up to 5 — all file types")

        // Reactions
        Spacer(Modifier.height(12.dp))
        TierGroupHeader("Reactions")
        TierCompareRow("Available reactions",
            free = "Like · Dislike",
            premium = "Like · Dislike · Heart · Angry")

        // Messaging
        Spacer(Modifier.height(12.dp))
        TierGroupHeader("Messaging")
        TierCompareRow("Start conversations",
            free = "1-on-1 only",
            premium = "1-on-1 and group")
        TierCompareRow("Be invited to groups",
            free = "Yes",
            premium = "Yes",
            freeCheck = true,
            premiumCheck = true)
        TierCompareRow("Attachments per message",
            free = "1 image",
            premium = "Up to 10 — all file types")
        TierCompareRow("Delete own messages",
            free = "Yes",
            premium = "Yes",
            freeCheck = true,
            premiumCheck = true)
        TierCompareRow("Delete conversations you created",
            free = "—",
            premium = "Full destruction for all participants")
        TierCompareRow("Group admins can delete any message",
            free = "—",
            premium = "Hard delete — gone for everyone")

        // Profile & Standing
        Spacer(Modifier.height(12.dp))
        TierGroupHeader("Profile & Standing")
        TierCompareRow("Premium badge",
            free = "—",
            premium = "Everywhere you appear")
        TierCompareRow("Monthly reputation bonus",
            free = "—",
            premium = "+2,500 rep on every renewal")
        TierCompareRow("Priority support",
            free = "Standard",
            premium = "Priority queue")

        Spacer(Modifier.height(24.dp))

        // ── Downgrade reassurance ────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Gold.copy(alpha = 0.04f))
                .border(1.dp, Gold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .padding(16.dp),
        ) {
            Column {
                Text(
                    text = "Downgrading is not punishment.",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldBright,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "If you cancel Premium, everything you created " +
                            "while subscribed stays exactly as it is — posts, " +
                            "comments, attachments, deleted conversations. " +
                            "You lose the badge, the monthly bonus, and the " +
                            "higher limits on new activity. Nothing you have " +
                            "made is destroyed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDim,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                )
            }
        }
    }
}

@Composable
private fun TierPricingCard(
    badge: String,
    name: String,
    price: String,
    priceUnit: String,
    tagline: String,
    highlights: List<String>,
    ctaText: String,
    ctaStyle: CitadelButtonStyle,
    isPremium: Boolean,
    onCta: () -> Unit,
) {
    val accent = if (isPremium) Gold else Cyan
    val borderAlpha = if (isPremium) 0.55f else 0.25f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isPremium)
                    Gold.copy(alpha = 0.04f).compositeOver(Void.copy(alpha = 0.85f))
                else
                    Void.copy(alpha = 0.75f)
            )
            .border(1.dp, accent.copy(alpha = borderAlpha), RoundedCornerShape(16.dp))
            .padding(20.dp),
    ) {
        // Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(accent.copy(alpha = 0.12f))
                .border(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(999.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp),
        ) {
            Text(
                text = badge,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontSize = 10.sp,
            )
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = TextDim,
            letterSpacing = 4.sp,
            fontSize = 12.sp,
        )

        Spacer(Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = price,
                style = MaterialTheme.typography.displayMedium,
                color = if (isPremium) GoldBright else CyanBright,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = priceUnit,
                style = MaterialTheme.typography.labelMedium,
                color = TextFaint,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = tagline,
            style = MaterialTheme.typography.bodySmall,
            color = TextDim,
            fontSize = 12.sp,
            lineHeight = 17.sp,
        )

        Spacer(Modifier.height(18.dp))

        highlights.forEach { h ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "✓",
                    color = if (isPremium) GoldBright else Success,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = h,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        CitadelButton(
            text = ctaText,
            onClick = onCta,
            style = ctaStyle,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TierGroupHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Gold.copy(alpha = 0.06f))
            .border(1.dp, Gold.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = GoldBright,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
        )
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun TierCompareRow(
    label: String,
    free: String,
    premium: String,
    freeCheck: Boolean = false,
    premiumCheck: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Void.copy(alpha = 0.5f))
            .border(1.dp, Cyan.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
            .padding(12.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.4.sp,
        )

        Spacer(Modifier.height(10.dp))

        TierValueLine(
            tag = "FREE",
            value = free,
            accent = Cyan,
            hasCheck = freeCheck,
        )
        Spacer(Modifier.height(6.dp))
        TierValueLine(
            tag = "PREMIUM",
            value = premium,
            accent = GoldBright,
            hasCheck = premiumCheck,
        )
    }
}

@Composable
private fun TierValueLine(
    tag: String,
    value: String,
    accent: Color,
    hasCheck: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        // Small colored tag
        Box(
            modifier = Modifier
                .padding(end = 10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(accent.copy(alpha = 0.12f))
                .padding(horizontal = 7.dp, vertical = 3.dp),
        ) {
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 9.sp,
            )
        }

        if (hasCheck) {
            Text(
                text = "✓",
                color = Success,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(end = 6.dp),
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = if (tag == "PREMIUM") GoldBright else TextDim,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f),
        )
    }
}


/* ═══════════════════════════════════════════════════════════════════════════
 * 06 — STALKER SHIELD PREVIEW
 * ======================================================================== */

@Composable
private fun ShieldPreviewSection() {
    val cards = listOf(
        "Visibility Is a Choice" to
                "Nothing about you is public by default. Every profile, " +
                "every post, every comment sits behind an accepted " +
                "connection. You decide who sees what, per person, at " +
                "any time.",
        "Silent, Permanent Blocks" to
                "Blocking is mutual, immediate, and silent. The blocked " +
                "party gets no notification — they simply vanish from " +
                "your world, and you from theirs. No escalation, no " +
                "\"user X blocked you\" message.",
        "Invisible Mode" to
                "Set your account to hidden and you become invisible to " +
                "everyone but yourself. Even a direct ID guess returns " +
                "404 — no existence leak. This is a permanent option, " +
                "not a temporary state.",
        "No Cold Contact" to
                "Messaging requires an accepted connection. Connection " +
                "requests require mutual acceptance. There is no way " +
                "for a stranger to reach you. Not one. No cold DMs, ever.",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = SECTION_PADDING.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionHeader(
            title = "Stalker Shield",
            lede = "The most dangerous feature of any social platform is " +
                    "the ease with which a stranger can find, watch, and " +
                    "follow someone. We built against that from day one.",
        )

        Spacer(Modifier.height(24.dp))

        cards.forEach { (title, body) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Void.copy(alpha = 0.5f))
                    .border(1.dp, Rune.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
            ) {
                // Left rune accent bar
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .width(3.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Rune),
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = RuneBright,
                        letterSpacing = 0.6.sp,
                        fontSize = 13.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDim,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(14.dp))

        CitadelButton(
            text = "Read the Full Stalker Shield",
            onClick = { /* TODO: navigate to /security#stalker-shield */ },
            style = CitadelButtonStyle.Cyan,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}


/* ═══════════════════════════════════════════════════════════════════════════
 * 07 — HOW IT WORKS
 * ======================================================================== */

@Composable
private fun HowItWorksSection() {
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


/* ═══════════════════════════════════════════════════════════════════════════
 * 08 — PROMISES
 * ======================================================================== */

@Composable
private fun PromisesSection() {
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
                    "limbo, no data retention games. When we say delete, we mean it.",
        )
        GuaranteeItem(
            title = "Free is not a demo.",
            body = "Everything you need to actually use the platform is " +
                    "available for free, forever. Premium exists for people " +
                    "who want more — not to hold the basic experience hostage.",
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


/* ═══════════════════════════════════════════════════════════════════════════
 * 09 — FINAL CTA
 * ======================================================================== */

@Composable
private fun FinalCtaSection() {
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
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Join the citizens building a better internet.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextDim,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CitadelButton(
                text = "Enter the Citadel",
                onClick = { /* TODO: navigate to /register */ },
                style = CitadelButtonStyle.Gold,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(48.dp))
    }
}


/* ═══════════════════════════════════════════════════════════════════════════
 * UTILITY — Color compositing (used in TierPricingCard)
 * ---------------------------------------------------------------------------
 * Blends a translucent color over an opaque backdrop. Compose's Color
 * supports `compositeOver` natively, but this keeps the intent obvious.
 * ======================================================================== */

private fun Color.compositeOver(background: Color): Color {
    val fgA = alpha
    val bgA = background.alpha
    val outA = fgA + bgA * (1 - fgA)
    if (outA == 0f) return Color.Transparent
    val r = (red * fgA + background.red * bgA * (1 - fgA)) / outA
    val g = (green * fgA + background.green * bgA * (1 - fgA)) / outA
    val b = (blue * fgA + background.blue * bgA * (1 - fgA)) / outA
    return Color(r, g, b, outA)
}