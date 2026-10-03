package lol.mycitadel.app.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import lol.mycitadel.app.data.network.UserDto
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.GlitchText
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.premium.PremiumViewModel
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint

@Composable
fun PremiumScreen(
    currentUser: UserDto?,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PremiumViewModel = viewModel(factory = PremiumViewModel.Factory),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val isPremiumUser = currentUser?.premium == true || state.isPremium

    LaunchedEffect(state.checkoutUrl) {
        val url = state.checkoutUrl
        if (!url.isNullOrBlank()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (_: Exception) { }
            viewModel.clearCheckoutUrl()
        }
    }

    LaunchedEffect(state.portalUrl) {
        val url = state.portalUrl
        if (!url.isNullOrBlank()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (_: Exception) { }
            viewModel.clearPortalUrl()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            RuneDivider()
            Spacer(Modifier.height(8.dp))
            GlitchText(
                text = "Citadel+",
                style = MaterialTheme.typography.displayMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Upgrade Your Fortress Access",
                style = MaterialTheme.typography.titleMedium,
                color = GoldBright,
                textAlign = TextAlign.Center,
            )
        }

        if (isPremiumUser) {
            item {
                CitadelPanel(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = GoldBright,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "Premium Citizen",
                            tint = GoldBright,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "YOU ARE A CITADEL+ CITIZEN",
                                color = GoldBright,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                            )
                            Text(
                                text = "All higher limits, extra reactions, and group features are active.",
                                color = TextDim,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
        }

        item {
            CitadelPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CITADEL+ BENEFITS",
                    color = CyanBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
                Spacer(Modifier.height(12.dp))

                BenefitRow("1,500 Character Limit on Posts & Comments")
                BenefitRow("Attach Up to 10 Files/Media per Post")
                BenefitRow("Attach Up to 5 Files/Media per Comment")
                BenefitRow("All 4 Reactions Unlocked (👍, 👎, ❤, 😡)")
                BenefitRow("Group Messaging with Admin Moderation")
                BenefitRow("Exclusive Citadel+ Citizen Badge")
                BenefitRow("+2,500 Monthly Reputation Bonus")
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (state.loading) {
                    CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp)
                } else if (isPremiumUser) {
                    CitadelButton(
                        text = "Manage Subscription (Stripe Portal)",
                        onClick = viewModel::openPortal,
                        style = CitadelButtonStyle.Gold,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    CitadelButton(
                        text = "Upgrade to Citadel+ ($10.00 / Month)",
                        onClick = viewModel::startCheckout,
                        style = CitadelButtonStyle.Gold,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CitadelButton(
                        text = "Refresh Status",
                        onClick = viewModel::checkStatus,
                        style = CitadelButtonStyle.Cyan,
                    )
                }

                state.errorMessage?.let { err ->
                    Text(
                        text = err,
                        color = Color(0xFFFFB0B0),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    )
                }

                Text(
                    text = "Secure checkout hosted by Stripe. Includes promo code 'FIRST200' support.",
                    color = TextFaint,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun BenefitRow(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Cyan.copy(alpha = 0.15f))
                .border(1.dp, Cyan, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = CyanBright,
                modifier = Modifier.size(12.dp),
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            color = Color(0xFFE0E6ED),
            fontSize = 13.sp,
        )
    }
}
