package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import lol.mycitadel.app.data.network.UserDto
import lol.mycitadel.app.ui.auth.LoginStep
import lol.mycitadel.app.ui.auth.LoginViewModel
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.GlitchText
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint

@Composable
fun LoginScreen(
    onLoggedIn: (UserDto) -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.successUser) {
        state.successUser?.let(onLoggedIn)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                RuneDivider()
                Spacer(Modifier.height(16.dp))

                GlitchText(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineLarge,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "The gate is not locked. It never was — you have the key.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(28.dp))

                // ── Error banner ───────────────────────────────────
                state.errorMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Blood.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .background(Blood.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFB0B0),
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                when (state.step) {
                    LoginStep.Password -> PasswordStep(state, viewModel, onNavigateToRegister)
                    LoginStep.TwoFactor -> TwoFactorStep(state, viewModel)
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * STEP 1 — IDENTIFIER + PASSWORD
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun PasswordStep(
    state: lol.mycitadel.app.ui.auth.LoginFormState,
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = state.identifier,
        onValueChange = viewModel::onIdentifierChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = "USERNAME OR EMAIL",
                style = MaterialTheme.typography.labelSmall,
                color = Cyan,
                letterSpacing = 1.sp,
            )
        },
        placeholder = {
            Text(text = "viking_42 or you@example.com", color = TextFaint)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cyan,
            unfocusedBorderColor = Cyan.copy(alpha = 0.3f),
            focusedLabelColor = CyanBright,
            unfocusedLabelColor = Cyan.copy(alpha = 0.7f),
            cursorColor = CyanBright,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(10.dp),
    )
    Spacer(Modifier.height(16.dp))

    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = state.password,
        onValueChange = viewModel::onPasswordChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = "PASSWORD",
                style = MaterialTheme.typography.labelSmall,
                color = Cyan,
                letterSpacing = 1.sp,
            )
        },
        placeholder = { Text(text = "Your password", color = TextFaint) },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                    else Icons.Filled.Visibility,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = Cyan.copy(alpha = 0.7f),
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cyan,
            unfocusedBorderColor = Cyan.copy(alpha = 0.3f),
            focusedLabelColor = CyanBright,
            unfocusedLabelColor = Cyan.copy(alpha = 0.7f),
            cursorColor = CyanBright,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(10.dp),
    )

    Spacer(Modifier.height(20.dp))

    CitadelButton(
        text = if (state.submitting) "Signing in…" else "Log In",
        onClick = {
            keyboard?.hide()
            viewModel.submitPassword()
        },
        style = CitadelButtonStyle.Gold,
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.submitting,
    )

    if (state.submitting) {
        Spacer(Modifier.height(14.dp))
        CircularProgressIndicator(
            color = Cyan,
            strokeWidth = 2.dp,
            modifier = Modifier.width(28.dp).height(28.dp),
        )
    }

    Spacer(Modifier.height(18.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "No account yet? ",
            style = MaterialTheme.typography.bodySmall,
            color = TextDim,
        )
        Text(
            text = "Create one",
            style = MaterialTheme.typography.bodySmall,
            color = Cyan,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(onClick = onNavigateToRegister),
        )
    }
}

/* ══════════════════════════════════════════════════════════════════
 * STEP 2 — 2FA CODE
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun TwoFactorStep(
    state: lol.mycitadel.app.ui.auth.LoginFormState,
    viewModel: LoginViewModel,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    CitadelPanel(
        modifier = Modifier.fillMaxWidth(),
        borderColor = Gold.copy(alpha = 0.4f),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "🔐", fontSize = 40.sp)
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Two-Factor Required",
                style = MaterialTheme.typography.titleLarge,
                color = GoldBright,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.twoFaMessage
                    ?: "Enter the 6-digit code from your authenticator app.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDim,
                textAlign = TextAlign.Center,
            )
        }
    }

    Spacer(Modifier.height(20.dp))

    OutlinedTextField(
        value = state.twoFaCode,
        onValueChange = viewModel::onTwoFaCodeChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = "CODE",
                style = MaterialTheme.typography.labelSmall,
                color = Cyan,
                letterSpacing = 1.sp,
            )
        },
        placeholder = { Text(text = "123456", color = TextFaint) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done,
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cyan,
            unfocusedBorderColor = Cyan.copy(alpha = 0.3f),
            focusedLabelColor = CyanBright,
            unfocusedLabelColor = Cyan.copy(alpha = 0.7f),
            cursorColor = CyanBright,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(10.dp),
    )

    Spacer(Modifier.height(20.dp))

    CitadelButton(
        text = if (state.submitting) "Verifying…" else "Verify",
        onClick = {
            keyboard?.hide()
            viewModel.submitTwoFa()
        },
        style = CitadelButtonStyle.Gold,
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.submitting,
    )

    if (state.submitting) {
        Spacer(Modifier.height(14.dp))
        CircularProgressIndicator(
            color = Cyan,
            strokeWidth = 2.dp,
            modifier = Modifier.width(28.dp).height(28.dp),
        )
    }

    Spacer(Modifier.height(18.dp))

    Text(
        text = "← Back to password",
        style = MaterialTheme.typography.bodySmall,
        color = Cyan,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clickable { viewModel.backToPassword() }
            .padding(8.dp),
    )
}