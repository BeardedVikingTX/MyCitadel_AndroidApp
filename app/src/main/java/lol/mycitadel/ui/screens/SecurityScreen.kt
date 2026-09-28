package lol.mycitadel.app.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.ui.components.AboutQuote
import lol.mycitadel.app.ui.components.AboutSectionHeader
import lol.mycitadel.app.ui.components.BountyRuleItem
import lol.mycitadel.app.ui.components.BountyScopeColumn
import lol.mycitadel.app.ui.components.BountyTierCard
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.GlitchText
import lol.mycitadel.app.ui.components.LimitItem
import lol.mycitadel.app.ui.components.MathCallout
import lol.mycitadel.app.ui.components.MechanismCard
import lol.mycitadel.app.ui.components.NoBackdoorItem
import lol.mycitadel.app.ui.components.ReportCard
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.components.SecurityFact
import lol.mycitadel.app.ui.components.ShieldCard
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Rune
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint

@Composable
fun SecurityScreen(modifier: Modifier = Modifier) {
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
                    text = "SECURITY & PRIVACY",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gold,
                    letterSpacing = 4.sp,
                    modifier = Modifier
                        .border(1.dp, Gold.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                )

                Spacer(Modifier.height(24.dp))

                GlitchText(
                    text = "Security",
                    style = MaterialTheme.typography.displayLarge,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Not Promises. Mechanism.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Gold,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Every claim on this page corresponds to a line of code " +
                            "you can audit on GitHub. Every protection has a defined " +
                            "failure mode. Every limit is written down. This is the " +
                            "threat model, published.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 2 — THE PROMISE
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "01", title = "The Promise")

                Spacer(Modifier.height(20.dp))

                AboutQuote(
                    "Your private data is encrypted before it reaches us. We " +
                            "cannot read your email address, your phone number, " +
                            "your real name, or your physical address. If our " +
                            "database is ever compromised, what leaks is ciphertext " +
                            "— useless without keys that live in a separate, " +
                            "isolated location.",
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "This is not a marketing bullet. It is the " +
                            "architecture. We built MyCitadel so that we cannot " +
                            "betray you even if we wanted to — because we do not " +
                            "have the ability to read the data in the first place.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDim,
                )

                Spacer(Modifier.height(24.dp))

                CitadelPanel(modifier = Modifier.fillMaxWidth()) {
                    Row2Facts(
                        left = { SecurityFact(value = "15", label = "PII fields encrypted") },
                        right = { SecurityFact(value = "256", unit = "MiB", label = "Argon2id memory cost") },
                    )
                    Spacer(Modifier.height(20.dp))
                    Row2Facts(
                        left = { SecurityFact(value = "0", label = "Third-party trackers") },
                        right = { SecurityFact(value = "0", label = "Admin spy panels") },
                    )
                }
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 3 — THE MECHANISMS
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AboutSectionHeader(number = "02", title = "The Mechanisms")

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Every item below is implemented. Every item is " +
                            "auditable. Nothing here is aspirational.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                MechanismCard(
                    icon = "🔐",
                    title = "Argon2id Password Hashing",
                    body = "Passwords are hashed with 256 MiB memory cost, " +
                            "4 iterations, 2 threads. Even with our entire users " +
                            "table in hand, recovering a single password requires " +
                            "infeasible compute. Silent rehash upgrades old hashes " +
                            "on next login.",
                )
                Spacer(Modifier.height(12.dp))
                MechanismCard(
                    icon = "🗝️",
                    title = "Envelope Encryption for PII",
                    body = "Every personal field — email, phone, real name, " +
                            "address, date of birth — is encrypted with a per-user " +
                            "key derived from a master key. The master key lives " +
                            "outside the database. A stolen DB dump yields " +
                            "ciphertext only.",
                )
                Spacer(Modifier.height(12.dp))
                MechanismCard(
                    icon = "🎯",
                    title = "Blind Index Lookups",
                    body = "We check \"is this email already registered?\" " +
                            "without ever storing the email in plaintext. Keyed " +
                            "HMAC indexes give us uniqueness enforcement and " +
                            "lookup — with zero recoverable plaintext on disk.",
                )
                Spacer(Modifier.height(12.dp))
                MechanismCard(
                    icon = "🧬",
                    title = "Session Fingerprinting",
                    body = "Every session is bound to your browser's " +
                            "User-Agent + Accept-Language + client hints. A cookie " +
                            "stolen from Chrome will not validate in Firefox. " +
                            "Session IDs rotate on every privilege change.",
                )
                Spacer(Modifier.height(12.dp))
                MechanismCard(
                    icon = "🔑",
                    title = "Two-Factor Authentication",
                    body = "TOTP-based, compatible with Google Authenticator, " +
                            "Microsoft Authenticator, Authy, 1Password, Bitwarden, " +
                            "and any RFC 6238 app. Secrets are encrypted at rest. " +
                            "Recovery codes are single-use and hashed.",
                )
                Spacer(Modifier.height(12.dp))
                MechanismCard(
                    icon = "🚫",
                    title = "No Third-Party Trackers",
                    body = "No Google Analytics. No Facebook Pixel. No " +
                            "Cloudflare Insights. No session-replay tools. The " +
                            "only third-party script on any page is Stripe.js, " +
                            "loaded exclusively during checkout.",
                )
                Spacer(Modifier.height(12.dp))
                MechanismCard(
                    icon = "🛡️",
                    title = "CSRF, XSS, SSRF, IDOR Defenses",
                    body = "Double-submit CSRF tokens on every unsafe request. " +
                            "Strict CSP without inline scripts. SSRF host allowlist " +
                            "on push subscriptions. Every mutation scoped to the " +
                            "session user — no client-supplied IDs trusted.",
                )
                Spacer(Modifier.height(12.dp))
                MechanismCard(
                    icon = "📋",
                    title = "Structured Audit Logging",
                    body = "Authentication events are logged with hashed IPs " +
                            "and per-request correlation IDs. Every log line is " +
                            "JSON-formatted and retained per published schedule. " +
                            "No plaintext PII appears in any log entry.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 4 — STALKER SHIELD
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AboutSectionHeader(number = "03", title = "Stalker Shield")

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "The most dangerous feature of any social platform " +
                            "is the ease with which a stranger can find, watch, and " +
                            "follow someone. We designed against that from day one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                ShieldCard(
                    icon = "🛡️",
                    title = "Visibility Is a Choice",
                    body = "Nothing about you is public by default. Your profile, " +
                            "posts, and connections are all gated behind accepted " +
                            "connection requests. You decide who sees what, per " +
                            "person, at any time.",
                    accentColor = Cyan,
                )
                Spacer(Modifier.height(12.dp))
                ShieldCard(
                    icon = "⛔",
                    title = "Silent, Permanent Blocks",
                    body = "Blocking is mutual, immediate, and silent. The " +
                            "blocked party gets no notification — they simply " +
                            "vanish from your world, and you from theirs.",
                    accentColor = Blood,
                )
                Spacer(Modifier.height(12.dp))
                ShieldCard(
                    icon = "👁️",
                    title = "Invisible Mode",
                    body = "Set your account to \"hidden\" and you become " +
                            "invisible to everyone except yourself. Even a direct " +
                            "ID guess returns 404 — no existence leak. Permanent " +
                            "option, not a temporary state.",
                    accentColor = Gold,
                )
                Spacer(Modifier.height(12.dp))
                ShieldCard(
                    icon = "ℹ️",
                    title = "Silent 404s, Never 403s",
                    body = "When you cannot view something, you get a " +
                            "404 Not Found — never a \"You are not allowed\" " +
                            "message. Attackers cannot enumerate who exists.",
                    accentColor = Success,
                )
                Spacer(Modifier.height(12.dp))
                ShieldCard(
                    icon = "⚔️",
                    title = "Sever Destroys Everything",
                    body = "End a connection and every message between the " +
                            "two of you is destroyed. Not hidden, not archived, " +
                            "not soft-deleted. Gone from the servers.",
                    accentColor = Rune,
                )
                Spacer(Modifier.height(12.dp))
                ShieldCard(
                    icon = "✋",
                    title = "No Contact Without Consent",
                    body = "You cannot see another user's profile, posts, or " +
                            "comments unless you are connected. No cold DMs. Ever.",
                    accentColor = Cyan,
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 5 — NO BACKDOORS
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "04", title = "No Backdoors")

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Most platforms quietly build an \"admin panel\" " +
                            "that lets staff read user messages, view private posts, " +
                            "and inspect profiles. They call it \"trust and safety.\" " +
                            "Regardless of the label, the outcome is the same: your " +
                            "private data has more readers than you think.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDim,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "MyCitadel has no such panel. There is no " +
                            "administrative interface to read user data — because " +
                            "there is no way for us to read it in the first place.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CyanBright,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(Modifier.height(20.dp))

                NoBackdoorItem(
                    title = "No admin \"view user's email\" tool",
                    body = "Emails are encrypted with a key we cannot derive " +
                            "without the user's ID and the master key.",
                )
                NoBackdoorItem(
                    title = "No private message reader",
                    body = "Messages are end-to-end encrypted in the current " +
                            "architecture. Staff cannot see them.",
                )
                NoBackdoorItem(
                    title = "No \"search all users by real name\" tool",
                    body = "Real names are encrypted. We cannot build a search " +
                            "index on data we cannot read.",
                )
                NoBackdoorItem(
                    title = "No shadow profile building",
                    body = "We do not accept third-party data feeds, purchase " +
                            "marketing lists, or infer user traits from browsing " +
                            "patterns. We only know what you tell us — and even " +
                            "that is encrypted.",
                )

                Spacer(Modifier.height(20.dp))

                AboutQuote(
                    "We do not want to see your private data. We cannot see " +
                            "your private data. Those two facts are the same design " +
                            "decision, made twice.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 6 — WE CANNOT SELL YOUR DATA
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "05", title = "We Cannot Sell Your Data")

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Other platforms promise they will not sell your " +
                            "data. We do not have to promise — because we do not " +
                            "have anything to sell.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDim,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Your email address is ciphertext. Your real name " +
                            "is ciphertext. Your phone number is ciphertext. Your " +
                            "address is ciphertext. There is no advertiser willing " +
                            "to pay for a base64 string that even we cannot read.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDim,
                )

                Spacer(Modifier.height(20.dp))

                MathCallout(
                    title = "The only thing we could ever sell is mathematics.",
                    body = "And nobody wants to buy our equations. They want " +
                            "your behavior, your attention, your identity — none " +
                            "of which we have access to. This is what \"privacy " +
                            "by architecture\" actually means.",
                )

                Spacer(Modifier.height(20.dp))

                CitadelPanel(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = Gold.copy(alpha = 0.4f),
                ) {
                    Text(
                        text = "How We Stay Alive",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldBright,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "MyCitadel is funded entirely by $10/month " +
                                "premium subscriptions and donations. We do not " +
                                "run ads. We do not accept sponsored content. We " +
                                "do not have investors demanding growth-at-any-cost.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDim,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "A platform funded by users cannot betray users " +
                                "without losing its users. A platform funded by " +
                                "advertisers cannot serve users without betraying " +
                                "them. That is the entire difference between the " +
                                "two models.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gold.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Light,
                    )
                }
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 7 — BUG BOUNTY
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AboutSectionHeader(number = "06", title = "Bug Bounty Program")

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "We believe security researchers should be allies, " +
                            "not adversaries. If you find a vulnerability in " +
                            "MyCitadel, tell us first. We will treat you with " +
                            "respect, credit you publicly, and reward you.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(20.dp))

                // Status pill
                Box(
                    modifier = Modifier
                        .border(1.dp, Success.copy(alpha = 0.4f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "●  OPEN VIA EMAIL · HACKERONE SOON",
                        style = MaterialTheme.typography.labelSmall,
                        color = Success,
                        letterSpacing = 1.sp,
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Rewards header
                Text(
                    text = "REWARDS",
                    style = MaterialTheme.typography.headlineSmall,
                    color = GoldBright,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                BountyTierCard(
                    badge = "Low → Critical",
                    title = "Merch + Reputation",
                    intro = "All valid vulnerabilities earn:",
                    bullets = listOf(
                        "Recognition in our public Hall of Fame",
                        "Reputation points on MyCitadel",
                        "Exclusive \"Bug Hunter\" badge — never available otherwise",
                        "MyCitadel merchandise for High/Critical findings",
                    ),
                    isCashTier = false,
                )

                Spacer(Modifier.height(16.dp))

                BountyTierCard(
                    badge = "Rare / Extreme",
                    title = "Up to $1,000 USD",
                    intro = "For truly exceptional findings, a financial " +
                            "reward may be offered — up to $1,000, at the owner's " +
                            "discretion. This applies when:",
                    bullets = listOf(
                        "The impact is proven and severe",
                        "Steps to reproduce are complete and verified",
                        "A remediation is identified or recommended",
                        "The finding is novel — not a duplicate",
                    ),
                    note = "Honest disclosure: MyCitadel is built by one " +
                            "person with a full-time job. Financial rewards are " +
                            "possible but not guaranteed. Do not spend a week on " +
                            "an audit expecting a payout.",
                    isCashTier = true,
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "WHAT WE'RE LOOKING FOR",
                    style = MaterialTheme.typography.headlineSmall,
                    color = GoldBright,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                BountyScopeColumn(
                    heading = "In Scope",
                    inScope = true,
                    items = listOf(
                        "api.mycitadel.lol — the entire API surface",
                        "mycitadel.lol — the web frontend",
                        "Authentication, session management, 2FA flows",
                        "Authorization / IDOR vulnerabilities",
                        "Encryption weaknesses",
                        "SQL injection, XSS, CSRF, SSRF",
                        "Business logic flaws",
                        "Privacy leaks",
                        "Rate-limit bypasses",
                        "Upload-handling vulnerabilities",
                    ),
                )

                Spacer(Modifier.height(12.dp))

                BountyScopeColumn(
                    heading = "Out of Scope",
                    inScope = false,
                    items = listOf(
                        "Denial of Service / volumetric attacks",
                        "Social engineering of staff or users",
                        "Physical attacks against infrastructure",
                        "Third-party services (Stripe, cPanel, LiteSpeed)",
                        "Reports generated purely by automated scanners",
                        "Missing security headers with no impact",
                        "Self-XSS requiring DevTools paste",
                        "Theoretical vulnerabilities without PoC",
                    ),
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "RULES OF ENGAGEMENT",
                    style = MaterialTheme.typography.headlineSmall,
                    color = GoldBright,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                BountyRuleItem(
                    number = 1,
                    title = "Do not test on real users.",
                    body = "Create your own test accounts. Never attempt to " +
                            "access another user's data, even if you think you can.",
                )
                BountyRuleItem(
                    number = 2,
                    title = "Do not disrupt the service.",
                    body = "No DoS. No load testing. No aggressive fuzzing " +
                            "that degrades performance for others.",
                )
                BountyRuleItem(
                    number = 3,
                    title = "Report privately first.",
                    body = "Give us time to fix before you publish. We aim to " +
                            "acknowledge within 72 hours and remediate critical " +
                            "findings within 7 days.",
                )
                BountyRuleItem(
                    number = 4,
                    title = "Do not exfiltrate data.",
                    body = "If you find a leak, prove it with minimal sample " +
                            "data. Do not download the whole database.",
                )
                BountyRuleItem(
                    number = 5,
                    title = "One finding per report.",
                    body = "Do not bundle five unrelated issues into a single " +
                            "submission. We will not be able to reward them properly.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 8 — LIMITS
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "07", title = "What We Do Not Promise")

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Honesty matters more than marketing. These are the " +
                            "boundaries of what MyCitadel can and cannot protect " +
                            "you from.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDim,
                )

                Spacer(Modifier.height(16.dp))

                LimitItem(
                    title = "We are not zero-knowledge in v1.",
                    body = "Email is decrypted server-side to send verification " +
                            "links and password resets. The server holds the master " +
                            "key. True zero-knowledge requires client-held keys — " +
                            "which breaks account recovery. That is a v2 feature " +
                            "for the native mobile apps.",
                )
                LimitItem(
                    title = "If your email account is compromised, so is your MyCitadel account.",
                    body = "Email is the recovery channel. Anyone with access " +
                            "to your inbox can trigger a password reset. Use a " +
                            "strong, unique password on your email provider, and " +
                            "enable 2FA there too.",
                )
                LimitItem(
                    title = "We cannot protect you from a compromised device.",
                    body = "If malware runs on your phone or laptop, no " +
                            "server-side defense can help. Keep your OS and " +
                            "browser updated. Use a password manager.",
                )
                LimitItem(
                    title = "We will comply with valid legal requests.",
                    body = "If we receive a lawful order, we cooperate within " +
                            "the bounds of the law. What we can hand over is " +
                            "limited by design: ciphertext, hashes, and timestamps " +
                            "— never plaintext.",
                )
                LimitItem(
                    title = "We are one person.",
                    body = "MyCitadel is built and operated by a single " +
                            "developer with a full-time job. That is a strength " +
                            "for privacy and a limit for scale. We grow carefully, " +
                            "not recklessly.",
                )
                LimitItem(
                    title = "We cannot guarantee 100% uptime.",
                    body = "Shared hosting, one operator, no SRE team. We aim " +
                            "for reliability but cannot match the SLAs of a " +
                            "hyperscale provider. If uptime is critical to you, " +
                            "this may not be the right platform.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 9 — REPORT A VULNERABILITY
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AboutSectionHeader(number = "08", title = "Report a Vulnerability")

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Two ways to reach us. Both are monitored. Both " +
                            "are welcome.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(20.dp))

                ReportCard(
                    badge = "Preferred",
                    title = "Email Disclosure",
                    body = "Send your report directly to our security inbox. " +
                            "Include the affected endpoint, the impact, and " +
                            "reproduction steps. Attach screenshots or PoC code " +
                            "if relevant.",
                    contactText = "security@mycitadel.lol",
                    note = "Acknowledgment target: 72 hours · " +
                            "Remediation target for critical: 7 days",
                    isPrimary = true,
                    onClickContact = {
                        uriHandler.openUri(
                            "mailto:security@mycitadel.lol?subject=Security%20Disclosure%20-%20MyCitadel"
                        )
                    },
                )

                Spacer(Modifier.height(12.dp))

                ReportCard(
                    badge = "Coming Soon",
                    title = "HackerOne",
                    body = "We are in the process of onboarding to HackerOne. " +
                            "Once live, this page will link directly to our " +
                            "program profile with full scope and reward " +
                            "documentation.",
                    contactText = "hackerone.com/mycitadel",
                    note = "Until then: email works fine. " +
                            "We will not lose your report.",
                    isPrimary = false,
                    onClickContact = null,
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 10 — FINAL CTA
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
                    text = "Security You Can Verify",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Our code is public. Our threat model is published. " +
                            "Audit us.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                CitadelButton(
                    text = "View Source on GitHub",
                    onClick = {
                        uriHandler.openUri("https://github.com/BeardedVikingTX")
                    },
                    style = CitadelButtonStyle.Cyan,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(10.dp))

                CitadelButton(
                    text = "Report a Bug",
                    onClick = {
                        uriHandler.openUri("mailto:security@mycitadel.lol")
                    },
                    style = CitadelButtonStyle.Gold,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

/* ── Helper: two facts side by side ─────────────────────────────────────── */
@Composable
private fun Row2Facts(
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(modifier = Modifier.weight(1f)) { left() }
        Box(modifier = Modifier.weight(1f)) { right() }
    }
}