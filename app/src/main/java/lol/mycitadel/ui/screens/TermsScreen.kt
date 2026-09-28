package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import lol.mycitadel.app.ui.components.AboutParagraph
import lol.mycitadel.app.ui.components.AboutQuote
import lol.mycitadel.app.ui.components.AboutSectionHeader
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.ContactDepartment
import lol.mycitadel.app.ui.components.GlitchText
import lol.mycitadel.app.ui.components.IpCard
import lol.mycitadel.app.ui.components.LegalBulletList
import lol.mycitadel.app.ui.components.LegalCallout
import lol.mycitadel.app.ui.components.LegalMeta
import lol.mycitadel.app.ui.components.LegalSubhead
import lol.mycitadel.app.ui.components.ProhibitionCard
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint

private const val EFFECTIVE_DATE = "2026-09-28"
private const val LAST_UPDATED   = "2026-09-28"

@Composable
fun TermsScreen(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        /* ═══════════════════════════════════════════════════════════════
         * HERO
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                RuneDivider()

                Spacer(Modifier.height(20.dp))

                GlitchText(
                    text = "Terms of Service",
                    style = MaterialTheme.typography.headlineLarge,
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Plain language. Real consequences. " +
                            "Read it before you click agree.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gold,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(20.dp))

                LegalMeta(
                    effectiveDate = EFFECTIVE_DATE,
                    lastUpdated = LAST_UPDATED,
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 01 — ACCEPTANCE
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "01", title = "Acceptance of These Terms")

                Spacer(Modifier.height(16.dp))

                AboutParagraph(
                    "By creating a MyCitadel account, accessing the platform, " +
                            "or using any part of our services, you agree to be " +
                            "bound by these Terms of Service and by our Privacy " +
                            "Policy and Security & Privacy Statement. If you do " +
                            "not agree, do not use MyCitadel.",
                )

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "These Terms form a legally binding agreement between " +
                            "you and the operator of MyCitadel (\"we\", \"us\", " +
                            "\"our\", or \"the operator\"). Your continued use " +
                            "of the platform constitutes acceptance of any future " +
                            "revisions.",
                )

                LegalCallout(
                    text = "Plain-language summary: If you use MyCitadel, " +
                            "you agree to play by these rules. If you break " +
                            "them, we can remove your account. If you break " +
                            "the law, we cooperate with law enforcement.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 02 — ELIGIBILITY
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "02", title = "Eligibility")

                Spacer(Modifier.height(16.dp))

                AboutParagraph(
                    "You must be at least 13 years of age to create a " +
                            "MyCitadel account. If you are under 18, you may only " +
                            "use the platform with the involvement of a parent or " +
                            "legal guardian. If you are under 13, you may not use " +
                            "MyCitadel at all — do not attempt to register.",
                )

                Spacer(Modifier.height(12.dp))

                AboutParagraph("You represent and warrant that:")

                LegalBulletList(
                    items = listOf(
                        "You have the legal capacity to enter into this agreement.",
                        "You are not barred from using the platform under any applicable law.",
                        "You have not previously been removed from MyCitadel for a violation of these Terms.",
                        "You are not located in a jurisdiction where the platform would be illegal for you to access.",
                    ),
                )

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "We may require age verification for accounts that appear " +
                            "to have been created by minors under 13, and we will " +
                            "remove any such account promptly upon discovery in " +
                            "compliance with COPPA and similar regulations.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 03 — YOUR ACCOUNT
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "03", title = "Your Account")

                LegalSubhead("Accurate information")
                AboutParagraph(
                    "You agree to provide accurate information during " +
                            "registration and to keep it current. A valid email " +
                            "address is required for account recovery and security " +
                            "notifications.",
                )

                LegalSubhead("One account per person")
                AboutParagraph(
                    "You may not create multiple accounts for the purpose of " +
                            "evading bans, manipulating reputation or voting, or " +
                            "impersonating others. We reserve the right to merge " +
                            "or terminate duplicate accounts.",
                )

                LegalSubhead("Credential security")
                AboutParagraph(
                    "You are responsible for maintaining the confidentiality " +
                            "of your password, recovery codes, and 2FA device. You " +
                            "agree to notify us immediately at " +
                            "security@mycitadel.lol if you believe your account " +
                            "has been compromised.",
                )

                LegalSubhead("You are responsible for your account")
                AboutParagraph(
                    "Activity conducted through your account is your " +
                            "responsibility. Sharing your credentials with another " +
                            "person transfers that responsibility to you, not us.",
                )

                LegalSubhead("Account deletion")
                AboutParagraph(
                    "You may destroy your account at any time from your " +
                            "account settings. Deletion is permanent and immediate: " +
                            "your posts, comments, reactions, connections, and " +
                            "personal data are destroyed on the server. There is " +
                            "no \"undo.\"",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 04 — PROHIBITED CONDUCT
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "04", title = "Prohibited Conduct")

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "The following activities will result in immediate " +
                            "account termination, without warning, at the sole " +
                            "discretion of the operator. Where applicable, " +
                            "evidence will be preserved and turned over to law " +
                            "enforcement.",
                )

                Spacer(Modifier.height(16.dp))

                ProhibitionCard(
                    title = "Sexual Harassment & Exploitation",
                    body = "Any content, message, comment, or behavior that " +
                            "constitutes sexual harassment; unsolicited sexual " +
                            "advances; sexual coercion; the sharing of intimate " +
                            "images without consent; sexual content involving " +
                            "minors (which will be reported to the National " +
                            "Center for Missing & Exploited Children immediately); " +
                            "or any conduct that degrades, objectifies, or " +
                            "exploits another person on the basis of sex or gender.",
                    severe = true,
                )
                Spacer(Modifier.height(10.dp))

                ProhibitionCard(
                    title = "Illegal Drug Use & Solicitation",
                    body = "Content that promotes, instructs, facilitates, " +
                            "or solicits the use, purchase, sale, or manufacture " +
                            "of illegal drugs or controlled substances. Discussion " +
                            "of drug policy in a political or academic context is " +
                            "permitted; content designed to enable drug activity " +
                            "is not.",
                    severe = true,
                )
                Spacer(Modifier.height(10.dp))

                ProhibitionCard(
                    title = "Harmful Content & Violence",
                    body = "Content that threatens violence against a person " +
                            "or group; content that promotes self-harm or suicide; " +
                            "content that depicts graphic violence for the purpose " +
                            "of glorification; content that incites hatred or " +
                            "violence on the basis of race, ethnicity, religion, " +
                            "sexual orientation, gender identity, or disability; " +
                            "or content that provides instructions for committing " +
                            "violent acts.",
                    severe = true,
                )
                Spacer(Modifier.height(10.dp))

                ProhibitionCard(
                    title = "Harassment & Stalking",
                    body = "Repeated unwanted contact, doxxing (sharing " +
                            "someone's personal information without consent), " +
                            "coordinated harassment campaigns, or using the " +
                            "platform to surveil, track, or intimidate another " +
                            "person.",
                )
                Spacer(Modifier.height(10.dp))

                ProhibitionCard(
                    title = "Fraud & Deception",
                    body = "Impersonating another person, organization, or " +
                            "MyCitadel staff; phishing; financial scams; romance " +
                            "scams; fraudulent premium subscription activity; " +
                            "reputation farming through automation; or any scheme " +
                            "intended to deceive other users.",
                )
                Spacer(Modifier.height(10.dp))

                ProhibitionCard(
                    title = "Illegal Activity",
                    body = "Using the platform to facilitate any act that " +
                            "violates applicable law, including but not limited " +
                            "to: distribution of child sexual abuse material " +
                            "(CSAM); human trafficking; terrorist activity; money " +
                            "laundering; and the sale of stolen goods.",
                )
                Spacer(Modifier.height(10.dp))

                ProhibitionCard(
                    title = "Platform Abuse",
                    body = "Attempting to access another user's account; " +
                            "bypassing rate limits or access controls; uploading " +
                            "malware; launching denial-of-service attacks; " +
                            "scraping data at scale; reverse-engineering the API " +
                            "for the purpose of building a competing service; or " +
                            "interfering with the platform's operation.",
                )
                Spacer(Modifier.height(10.dp))

                ProhibitionCard(
                    title = "Spam & Unauthorized Advertising",
                    body = "Mass-unsolicited messaging; link farming; " +
                            "affiliate-marketing schemes; crypto promotions sent " +
                            "to non-consenting users; or any commercial " +
                            "solicitation not authorized in writing by the operator.",
                )

                Spacer(Modifier.height(16.dp))

                LegalCallout(
                    isWarning = true,
                    text = "Our enforcement stance: MyCitadel is a small " +
                            "platform operated by one person. We do not have a " +
                            "moderation team, and we do not scan private content " +
                            "(we cannot — it is encrypted). Enforcement is " +
                            "report-driven. If you are the victim of prohibited " +
                            "conduct, report it to abuse@mycitadel.lol. Include " +
                            "the username, the nature of the conduct, and any " +
                            "evidence. We will investigate every report we receive.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 05 — YOUR CONTENT
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "05", title = "Your Content")

                LegalSubhead("You own what you post")
                AboutParagraph(
                    "You retain full ownership of all content you create on " +
                            "MyCitadel — posts, comments, profile text, uploaded " +
                            "images, and any other materials. We do not claim " +
                            "copyright over your content. We never have.",
                )

                LegalSubhead("Limited license to operate the platform")
                AboutParagraph(
                    "To display your content to your connections and to the " +
                            "platform features you enable, you grant MyCitadel a " +
                            "worldwide, non-exclusive, royalty-free license to " +
                            "store, transmit, and display your content solely for " +
                            "the purpose of operating the platform as you have " +
                            "configured it. This license terminates when you " +
                            "delete the content or your account.",
                )

                LegalSubhead("Your responsibility")
                AboutParagraph(
                    "You are solely responsible for your content. You " +
                            "represent that you own or have the necessary rights " +
                            "to any content you post, and that your content does " +
                            "not violate the rights of any third party (including " +
                            "copyright, trademark, privacy, or publicity rights).",
                )

                LegalSubhead("Content removal")
                AboutParagraph(
                    "We do not pre-screen content — it is technically " +
                            "infeasible with end-to-end encryption. However, upon " +
                            "receiving a valid report or legal order, we will " +
                            "remove or block content that violates these Terms or " +
                            "applicable law. Where the content is encrypted and " +
                            "cannot be read by us, we may be legally compelled to " +
                            "preserve it in its encrypted form for law enforcement.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 06 — INTELLECTUAL PROPERTY
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "06", title = "Intellectual Property")

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "MyCitadel's source code is published publicly for the " +
                            "purpose of transparency and audit. That is not the " +
                            "same as abandoning our rights. Here is exactly what " +
                            "you may and may not do.",
                )

                Spacer(Modifier.height(16.dp))

                IpCard(
                    badge = "Allowed",
                    title = "What You May Do",
                    allowed = true,
                    items = listOf(
                        "View the source code on GitHub",
                        "Read and audit the code for security purposes",
                        "Fork the repository for personal, non-commercial learning",
                        "Submit pull requests, bug reports, and improvements back to the project",
                        "Cite the project in academic work, articles, or discussions",
                        "Install a private instance for your own personal, non-public use",
                    ),
                )

                Spacer(Modifier.height(12.dp))

                IpCard(
                    badge = "Prohibited",
                    title = "What You May Not Do",
                    allowed = false,
                    items = listOf(
                        "Republish the code as your own product or under a different name",
                        "Operate a public-facing, commercial, or competing service derived from the code",
                        "Remove copyright notices, license headers, or author attribution",
                        "Use the name \"MyCitadel\", the rune mark, the logo, or any related branding",
                        "Sell, sublicense, or relicense the code to third parties",
                        "Deploy the code at scale for the purpose of serving users other than yourself",
                    ),
                )

                Spacer(Modifier.height(16.dp))

                LegalCallout(
                    isWarning = true,
                    text = "Brand and Trademark. The names \"MyCitadel\", " +
                            "\"mycitadel.lol\", the runic mark ᛗ, the shield logo, " +
                            "the glowing cyan aesthetic, and any related visual " +
                            "identity are trademarks of the operator. They are " +
                            "not licensed for use by anyone else. Using them to " +
                            "promote a fork, a competing service, or a product " +
                            "that implies endorsement or affiliation is prohibited " +
                            "and will result in legal action where appropriate.",
                )

                LegalCallout(
                    text = "License for the code. The MyCitadel source code " +
                            "is published under the MIT License. MIT permits " +
                            "broad reuse — including commercial reuse — with " +
                            "minimal restriction. If you intend to prevent " +
                            "republishing as a competing service, consult with " +
                            "an attorney before launch to pick the right option.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 07 — BUG BOUNTY PROTECTIONS
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "07", title = "Bug Bounty Hunter Protections")

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "We believe security researchers are allies, not " +
                            "adversaries. If you are conducting legitimate " +
                            "security research on MyCitadel, the following " +
                            "protections apply to you — provided you follow the " +
                            "Rules of Engagement published on the Security page.",
                )

                LegalSubhead("Safe Harbor")
                AboutParagraph(
                    "If you make a good-faith effort to comply with our " +
                            "published Rules of Engagement, we will consider " +
                            "your research authorized and will not pursue legal " +
                            "action against you. We will also make this position " +
                            "known to any third party that raises a complaint " +
                            "about your research.",
                )

                LegalSubhead("What is not covered")
                AboutParagraph(
                    "This safe harbor does not cover: accessing, " +
                            "exfiltrating, or modifying other users' data; " +
                            "denial-of-service attacks; social engineering of " +
                            "staff or users; physical attacks; or any activity " +
                            "that violates criminal law independent of our Terms.",
                )

                LegalSubhead("Your report is yours")
                AboutParagraph(
                    "You retain full credit for any vulnerability you " +
                            "discover. We will publicly acknowledge your finding " +
                            "in our Hall of Fame (once launched) unless you " +
                            "request otherwise. We will not claim authorship of " +
                            "your discovery.",
                )

                LegalSubhead("Our obligations")
                AboutParagraph("When you submit a valid report, we commit to:")

                LegalBulletList(
                    items = listOf(
                        "Acknowledging receipt within 72 hours.",
                        "Providing an initial severity assessment within 7 days.",
                        "Keeping you informed of remediation progress.",
                        "Crediting you publicly once the issue is fixed (unless you ask otherwise).",
                        "Rewarding per the tiers described on the Security page.",
                    ),
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 08 — PRIVACY
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "08", title = "Privacy")

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "Your privacy is governed by our Privacy Policy, which " +
                            "forms part of these Terms by reference. In summary:",
                )

                LegalBulletList(
                    items = listOf(
                        "We do not sell your data — we cannot, because we cannot read it.",
                        "PII is encrypted at rest with per-user keys.",
                        "We do not use third-party tracking scripts or analytics.",
                        "We retain only what is necessary to operate the platform.",
                        "Account deletion is permanent and destroys your data on the server.",
                    ),
                )

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "The full statement of mechanisms is published on the " +
                            "Security & Privacy page.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 09 — PREMIUM SUBSCRIPTIONS
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "09", title = "Premium Subscriptions")

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "MyCitadel Premium is a paid subscription priced at " +
                            "$10 USD per month, processed by Stripe. By " +
                            "subscribing, you agree to the following terms:",
                )

                LegalBulletList(
                    items = listOf(
                        "Billing cycle. Subscriptions renew automatically each month until canceled.",
                        "Cancellation. You may cancel at any time through the billing portal. Cancellation takes effect at the end of the current billing period.",
                        "Refunds. We do not offer refunds for partial months.",
                        "Price changes. We may change the price of Premium with at least 30 days' notice.",
                        "Payment failure. If your payment method fails, Stripe will retry for approximately three weeks. If payment is not recovered, your subscription will lapse.",
                    ),
                )

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "For billing and payment questions, contact " +
                            "info@mycitadel.lol.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 10 — ENFORCEMENT & TERMINATION
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "10", title = "Enforcement & Termination")

                LegalSubhead("Our right to terminate")
                AboutParagraph(
                    "We reserve the right to suspend or permanently terminate " +
                            "any account, at any time, for any reason, with or " +
                            "without notice. We will exercise this right when we " +
                            "have a good-faith belief that the account has " +
                            "violated these Terms, endangered other users, or " +
                            "exposed the platform to legal risk.",
                )

                LegalSubhead("Investigation process")
                AboutParagraph("When a report of prohibited conduct is received, we will:")

                LegalBulletList(
                    ordered = true,
                    items = listOf(
                        "Preserve any evidence available to us.",
                        "Notify the accused user where legally permissible.",
                        "Allow the accused an opportunity to respond, unless the conduct is so severe that immediate action is required.",
                        "Make a determination based on the evidence available.",
                        "Take action — which may include a warning, a temporary suspension, or permanent account destruction.",
                    ),
                )

                LegalSubhead("Cooperation with law enforcement")
                AboutParagraph(
                    "If we determine in good faith that a user has engaged " +
                            "in illegal activity, we will preserve all available " +
                            "evidence (in encrypted form where applicable) and " +
                            "cooperate fully with law enforcement, including " +
                            "local police, state authorities, and federal " +
                            "agencies such as the FBI, NCMEC, and the DOJ. " +
                            "The order is: preserve → report → cooperate → " +
                            "destroy.",
                )

                LegalCallout(
                    isWarning = true,
                    text = "What we can and cannot hand over. Because your " +
                            "private content is encrypted with keys we do not " +
                            "possess in usable form for individual user accounts, " +
                            "what we can provide to law enforcement is limited: " +
                            "account metadata, timestamps, IP hashes, and " +
                            "encrypted blobs. We cannot decrypt your private " +
                            "messages.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 11 — DISCLAIMERS
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "11", title = "Disclaimers & Limitation of Liability")

                Spacer(Modifier.height(12.dp))

                CitadelPanel(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "MYCITADEL IS PROVIDED \"AS IS\" AND \"AS " +
                                "AVAILABLE\" WITHOUT WARRANTY OF ANY KIND, EXPRESS " +
                                "OR IMPLIED, INCLUDING BUT NOT LIMITED TO " +
                                "WARRANTIES OF MERCHANTABILITY, FITNESS FOR A " +
                                "PARTICULAR PURPOSE, OR NON-INFRINGEMENT.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDim,
                        letterSpacing = 0.5.sp,
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE " +
                                "LAW, THE OPERATOR OF MYCITADEL SHALL NOT BE " +
                                "LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, " +
                                "CONSEQUENTIAL, OR PUNITIVE DAMAGES, OR ANY LOSS " +
                                "OF PROFITS OR REVENUES.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDim,
                        letterSpacing = 0.5.sp,
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "IN NO EVENT SHALL THE OPERATOR'S TOTAL " +
                                "LIABILITY EXCEED THE GREATER OF (A) THE AMOUNT " +
                                "YOU HAVE PAID US IN THE TWELVE MONTHS PRECEDING " +
                                "THE CLAIM, OR (B) ONE HUNDRED U.S. DOLLARS ($100).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDim,
                        letterSpacing = 0.5.sp,
                    )
                }

                LegalSubhead("Indemnification")
                AboutParagraph(
                    "You agree to indemnify, defend, and hold harmless the " +
                            "operator of MyCitadel from any claim, liability, " +
                            "damage, loss, or expense (including reasonable " +
                            "attorneys' fees) arising out of: (a) your use of " +
                            "the platform; (b) your content; (c) your violation " +
                            "of these Terms; or (d) your violation of any third " +
                            "party's rights.",
                )

                LegalSubhead("No guarantee of availability")
                AboutParagraph(
                    "MyCitadel is operated by one person on shared " +
                            "infrastructure. We do not guarantee uptime, " +
                            "uninterrupted service, or preservation of data " +
                            "beyond what is described in our Privacy Policy. Do " +
                            "not use MyCitadel as your only storage location for " +
                            "anything irreplaceable.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 12 — GOVERNING LAW
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "12", title = "Governing Law & Dispute Resolution")

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "These Terms shall be governed by and construed in " +
                            "accordance with the laws of the State of Texas, " +
                            "United States, without regard to its conflict of " +
                            "law principles. Any legal action arising out of or " +
                            "relating to these Terms or the platform shall be " +
                            "brought exclusively in the state or federal courts " +
                            "located in Tarrant County, Texas, and you consent " +
                            "to personal jurisdiction in those courts.",
                )

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "Operational note: The platform is temporarily operated " +
                            "from Sullivan, Illinois, pending relocation of the " +
                            "primary operations center to Fort Worth, Texas.",
                )

                LegalSubhead("Informal resolution first")
                AboutParagraph(
                    "Before filing a formal legal action, you agree to " +
                            "attempt to resolve any dispute informally by " +
                            "contacting info@mycitadel.lol with a written " +
                            "description of the issue. We will respond within " +
                            "30 days.",
                )

                LegalSubhead("Severability")
                AboutParagraph(
                    "If any provision of these Terms is held to be invalid " +
                            "or unenforceable, that provision shall be severed " +
                            "and the remaining provisions shall continue in full " +
                            "force and effect.",
                )

                LegalSubhead("Entire agreement")
                AboutParagraph(
                    "These Terms, together with the Privacy Policy and the " +
                            "Security & Privacy Statement, constitute the entire " +
                            "agreement between you and the operator concerning " +
                            "MyCitadel.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 13 — CHANGES
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "13", title = "Changes to These Terms")

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "We may update these Terms from time to time. When we " +
                            "make material changes, we will:",
                )

                LegalBulletList(
                    items = listOf(
                        "Update the \"Last Updated\" date at the top of this page.",
                        "Notify all users via email and in-app notification at least 14 days before the change takes effect.",
                        "Preserve a link to the previous version for at least 90 days so you can review what changed.",
                    ),
                )

                Spacer(Modifier.height(12.dp))

                AboutParagraph(
                    "Continued use of the platform after a change takes " +
                            "effect constitutes acceptance of the updated Terms. " +
                            "If you do not agree to a change, you may destroy " +
                            "your account at any time.",
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * 14 — CONTACT
         * =============================================================== */
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                AboutSectionHeader(number = "14", title = "Contact")

                Spacer(Modifier.height(12.dp))

                AboutParagraph("For any question about these Terms:")

                Spacer(Modifier.height(8.dp))

                ContactDepartment(
                    label = "General",
                    email = "info@mycitadel.lol",
                    onTap = { uriHandler.openUri("mailto:info@mycitadel.lol") },
                )
                ContactDepartment(
                    label = "Abuse Reports",
                    email = "abuse@mycitadel.lol",
                    onTap = { uriHandler.openUri("mailto:abuse@mycitadel.lol") },
                )
                ContactDepartment(
                    label = "Security",
                    email = "security@mycitadel.lol",
                    onTap = { uriHandler.openUri("mailto:security@mycitadel.lol") },
                )
                ContactDepartment(
                    label = "Legal",
                    email = "legal@mycitadel.lol",
                    onTap = { uriHandler.openUri("mailto:legal@mycitadel.lol") },
                )
            }
        }

        /* ═══════════════════════════════════════════════════════════════
         * FINAL CTA
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
                    text = "Agreement Understood",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "These are the rules. By using MyCitadel, you " +
                            "have accepted them.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                CitadelButton(
                    text = "Security & Privacy",
                    onClick = {
                        // TODO: navigate to Security tab
                    },
                    style = CitadelButtonStyle.Gold,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}