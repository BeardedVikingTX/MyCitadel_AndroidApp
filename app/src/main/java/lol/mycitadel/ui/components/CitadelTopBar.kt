package lol.mycitadel.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.data.network.UserDto
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.Void

/**
 * The top bar shown on every screen.
 *
 * Two states:
 *   • Guest  — Log In + Register buttons
 *   • Authed — User's username as a Dashboard button
 *
 * The `onLogoutComplete` callback is accepted now so the call site
 * doesn't need changing when the Login screen ships. It's not yet
 * wired to a real logout flow (that lands with the Login screen).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitadelTopBar(
    currentUser: UserDto?,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onLogoutComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ᛗ",
                    fontSize = 26.sp,
                    color = Gold,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "MyCitadel",
                    style = MaterialTheme.typography.titleLarge,
                    color = CyanBright,
                    letterSpacing = 1.sp,
                )
            }
        },
        actions = {
            if (currentUser == null) {
                // ── Guest state ────────────────────────────────────
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "LOG IN",
                        style = MaterialTheme.typography.labelMedium,
                        color = Cyan,
                        letterSpacing = 1.sp,
                    )
                }
                Button(
                    onClick = onRegisterClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold.copy(alpha = 0.12f),
                        contentColor = Gold,
                    ),
                    modifier = Modifier.width(110.dp),
                ) {
                    Text(
                        text = "REGISTER",
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 1.sp,
                    )
                }
            } else {
                // ── Authenticated state ────────────────────────────
                TextButton(onClick = onDashboardClick) {
                    Text(
                        text = currentUser.username.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Cyan,
                        letterSpacing = 1.sp,
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Void,
            titleContentColor = CyanBright,
            actionIconContentColor = Cyan,
        ),
    )
}