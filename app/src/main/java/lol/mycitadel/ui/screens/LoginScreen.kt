package lol.mycitadel.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
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
import lol.mycitadel.app.ui.auth.LoginFormState
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
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.TextPrimary

@Composable
fun LoginScreen(
    onLoggedIn: (UserDto) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
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

                // Animated error banner — enters from above, dismissible.
                AnimatedVisibility(
                    visible = state.errorMessage != null,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }),
                ) {
                    ErrorBanner(
                        message = state.errorMessage.orEmpty(),
                        onDismiss = { viewModel.clearError() },
                    )
                }

                AnimatedVisibility(
                    visible = state.errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Spacer(Modifier.height(16.dp))
                }

                when (state.step) {
                    LoginStep.Password -> PasswordStep(
                        state = state,
                        viewModel = viewModel,
                        onNavigateToRegister = onNavigateToRegister,
                        onNavigateToForgotPassword = onNavigateToForgotPassword,
                    )
                    LoginStep.TwoFactor -> TwoFactorStep(
                        state = state,
                        viewModel = viewModel,
                    )
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * ERROR BANNER
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Blood.copy(alpha = 0.08f))
            .border(1.dp, Blood.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .padding(start = 14.dp, end = 6.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "⚠",
            fontSize = 16.sp,
            color = Blood,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFFFB0B0),
            modifier = Modifier.weight(1f),
            lineHeight = 18.sp,
        )
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Dismiss error",
                tint = Blood.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * STEP 1 — IDENTIFIER + PASSWORD
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun PasswordStep(
    state: LoginFormState,
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    // Focus management: pressing "Next" on the identifier moves to password.
    val passwordFocus = remember { FocusRequester() }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Distinct error state for the two fields so we can highlight the
    // offending one. Currently unused by the VM but harmless — defaults false.
    val identifierError = state.errorMessage != null && state.identifier.isBlank()
    val passwordError   = state.errorMessage != null && !state.submitting

    OutlinedTextField(
        value = state.identifier,
        onValueChange = viewModel::onIdentifierChange,
        modifier = Modifier
            .fillMaxWidth(),
        enabled = !state.submitting,
        label = {
            Text(
                text = "USERNAME OR EMAIL",
                style = MaterialTheme.typography.labelSmall,
                color = if (identifierError) Blood else Cyan,
                letterSpacing = 1.sp,
            )
        },
        placeholder = {
            Text(text = "viking_42 or you@example.com", color = TextFaint)
        },
        singleLine = true,
        isError = identifierError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(
            onNext = { passwordFocus.requestFocus() },
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
    Spacer(Modifier.height(14.dp))

    OutlinedTextField(
        value = state.password,
        onValueChange = viewModel::onPasswordChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(passwordFocus),
        enabled = !state.submitting,
        label = {
            Text(
                text = "PASSWORD",
                style = MaterialTheme.typography.labelSmall,
                color = if (passwordError) Blood else Cyan,
                letterSpacing = 1.sp,
            )
        },
        placeholder = { Text(text = "Your password", color = TextFaint) },
        singleLine = true,
        isError = passwordError,
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboard?.hide()
                focusManager.clearFocus()
                if (!state.submitting) viewModel.submitPassword()
            },
        ),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                    else Icons.Filled.Visibility,
                    contentDescription = if (passwordVisible) "Hide password"
                    else "Show password",
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

    Spacer(Modifier.height(12.dp))

    // Forgot password — the whole point of the endpoint we just built.
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        Text(
            text = "Forgot password?",
            style = MaterialTheme.typography.bodySmall,
            color = Gold,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable(enabled = !state.submitting) {
                    keyboard?.hide()
                    focusManager.clearFocus()
                    onNavigateToForgotPassword()
                }
                .padding(vertical = 6.dp, horizontal = 4.dp),
        )
    }

    Spacer(Modifier.height(14.dp))

    CitadelButton(
        text = if (state.submitting) "Signing in…" else "Log In",
        onClick = {
            keyboard?.hide()
            focusManager.clearFocus()
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
            modifier = Modifier.size(28.dp),
        )
    }

    Spacer(Modifier.height(24.dp))

    // Divider with "or" in the middle — subtle visual separator.
    DividerWithLabel("or")

    Spacer(Modifier.height(24.dp))

    CitadelButton(
        text = "Create an Account",
        onClick = {
            keyboard?.hide()
            focusManager.clearFocus()
            onNavigateToRegister()
        },
        style = CitadelButtonStyle.Cyan,
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.submitting,
    )

    Spacer(Modifier.height(16.dp))

    Text(
        text = "New accounts get full access to posts, comments, connections, " +
                "and encrypted messaging. Free forever.",
        style = MaterialTheme.typography.bodySmall,
        color = TextFaint,
        textAlign = TextAlign.Center,
        lineHeight = 16.sp,
    )
}

/* ══════════════════════════════════════════════════════════════════
 * STEP 2 — 2FA CODE
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun TwoFactorStep(
    state: LoginFormState,
    viewModel: LoginViewModel,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    // Auto-submit when the code reaches 6 digits (TOTP) OR the user
    // pastes a full recovery code. The VM handles both formats.
    LaunchedEffect(state.twoFaCode) {
        val code = state.twoFaCode.trim()
        // Only auto-submit for 6-digit numeric — recovery codes are longer
        // and the user should press the button explicitly for those.
        if (code.length == 6 && code.all { it.isDigit() } && !state.submitting) {
            keyboard?.hide()
            focusManager.clearFocus()
            viewModel.submitTwoFa()
        }
    }

    CitadelPanel(
        modifier = Modifier.fillMaxWidth(),
        borderColor = Gold.copy(alpha = 0.45f),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Locked shield icon inside a gold-ringed circle
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Gold.copy(alpha = 0.08f))
                    .border(1.dp, Gold.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🔐", fontSize = 32.sp)
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Two-Factor Required",
                style = MaterialTheme.typography.titleLarge,
                color = GoldBright,
                letterSpacing = 1.sp,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = state.twoFaMessage
                    ?: "Enter the 6-digit code from your authenticator app, " +
                    "or one of your recovery codes.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDim,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )
        }
    }

    Spacer(Modifier.height(20.dp))

    OutlinedTextField(
        value = state.twoFaCode,
        onValueChange = { new ->
            // The VM sanitizes further, but we pre-filter here for
            // immediate feedback: digits + alphanumerics for recovery codes,
            // hard-capped at 32 chars to match the backend.
            val filtered = new.filter { it.isLetterOrDigit() || it == '-' }
            if (filtered.length <= 32) {
                viewModel.onTwoFaCodeChange(filtered)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.submitting,
        label = {
            Text(
                text = "CODE OR RECOVERY CODE",
                style = MaterialTheme.typography.labelSmall,
                color = Cyan,
                letterSpacing = 1.sp,
            )
        },
        placeholder = {
            Text(text = "123456", color = TextFaint, letterSpacing = 4.sp)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            // Number keyboard for TOTP, but recovery codes need letters too.
            // Use Password keyboard so we get the numeric row but still accept
            // alphanumerics on most soft keyboards.
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboard?.hide()
                focusManager.clearFocus()
                if (!state.submitting) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.submitTwoFa()
                }
            },
        ),
        textStyle = MaterialTheme.typography.titleMedium.copy(
            letterSpacing = if (state.twoFaCode.length <= 6) 6.sp else 2.sp,
            textAlign = TextAlign.Center,
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

    Spacer(Modifier.height(10.dp))

    // Live hint under the field: shows progress toward 6 digits, or
    // acknowledges a recovery code shape.
    val code = state.twoFaCode.trim()
    val hint = when {
        code.isEmpty() -> "Codes are 6 digits. Recovery codes use letters and dashes."
        code.length < 6 && code.all { it.isDigit() } ->
            "${6 - code.length} more digit${if (6 - code.length == 1) "" else "s"}…"
        code.length == 6 && code.all { it.isDigit() } && state.submitting ->
            "Verifying…"
        code.length == 6 && code.all { it.isDigit() } ->
            "Submitting…"
        code.any { it.isLetter() } ->
            "Looks like a recovery code — press Verify to submit."
        else -> ""
    }
    if (hint.isNotEmpty()) {
        Text(
            text = hint,
            style = MaterialTheme.typography.bodySmall,
            color = TextFaint,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    Spacer(Modifier.height(18.dp))

    CitadelButton(
        text = if (state.submitting) "Verifying…" else "Verify",
        onClick = {
            keyboard?.hide()
            focusManager.clearFocus()
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            viewModel.submitTwoFa()
        },
        style = CitadelButtonStyle.Gold,
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.submitting && state.twoFaCode.isNotBlank(),
    )

    if (state.submitting) {
        Spacer(Modifier.height(14.dp))
        CircularProgressIndicator(
            color = Cyan,
            strokeWidth = 2.dp,
            modifier = Modifier.size(28.dp),
        )
    }

    Spacer(Modifier.height(20.dp))

    Text(
        text = "← Back to password",
        style = MaterialTheme.typography.bodySmall,
        color = Cyan,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clickable(enabled = !state.submitting) {
                keyboard?.hide()
                focusManager.clearFocus()
                viewModel.backToPassword()
            }
            .padding(vertical = 8.dp, horizontal = 12.dp),
    )
}

/* ══════════════════════════════════════════════════════════════════
 * UTILITIES
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun DividerWithLabel(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Cyan.copy(alpha = 0.15f)),
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextFaint,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Cyan.copy(alpha = 0.15f)),
        )
    }
}