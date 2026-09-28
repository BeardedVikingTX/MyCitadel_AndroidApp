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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import lol.mycitadel.app.ui.auth.AuthViewModel
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
import lol.mycitadel.app.ui.theme.Void

@Composable
fun RegisterScreen(
    onRegistered: (UserDto) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    val state by viewModel.form.collectAsState()

    LaunchedEffect(state.successUser) {
        state.successUser?.let(onRegistered)
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
                    text = "Enter the Citadel",
                    style = MaterialTheme.typography.headlineLarge,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "No trackers. No ads. No data sale. Just a place " +
                            "where your private life stays private.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(28.dp))

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

                CitadelTextField(
                    value = state.username,
                    onValueChange = viewModel::onUsernameChange,
                    label = "Username",
                    placeholder = "viking_42",
                    keyboardType = KeyboardType.Text,
                )
                Spacer(Modifier.height(6.dp))
                HelperText("3–32 characters. Letters, numbers, and underscores only.")
                Spacer(Modifier.height(16.dp))

                CitadelTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "Email Address",
                    placeholder = "you@example.com",
                    keyboardType = KeyboardType.Email,
                )
                Spacer(Modifier.height(6.dp))
                HelperText("Used for account recovery. Encrypted at rest — we cannot read it.")
                Spacer(Modifier.height(16.dp))

                PasswordField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = "Password",
                    placeholder = "At least 12 characters",
                )
                Spacer(Modifier.height(8.dp))
                PasswordStrengthMeter(state.passwordStrength)
                Spacer(Modifier.height(6.dp))
                HelperText(passwordHint(state.password.length, state.passwordStrength))
                Spacer(Modifier.height(16.dp))

                PasswordField(
                    value = state.passwordConfirm,
                    onValueChange = viewModel::onPasswordConfirmChange,
                    label = "Confirm Password",
                    placeholder = "Type it again",
                )
                Spacer(Modifier.height(16.dp))

                CitadelTextField(
                    value = state.referralCode,
                    onValueChange = viewModel::onReferralChange,
                    label = "Referral Code (optional)",
                    placeholder = "VIKING-A7X9",
                    keyboardType = KeyboardType.Text,
                )
                Spacer(Modifier.height(6.dp))
                HelperText("If someone invited you, enter their code — you both earn 500 rep.")
                Spacer(Modifier.height(20.dp))

                CheckRow(
                    checked = state.acceptedTerms,
                    onCheckedChange = viewModel::onTermsChange,
                    label = "I agree to the Terms of Service and Privacy Policy.",
                )
                CheckRow(
                    checked = state.acceptedAge,
                    onCheckedChange = viewModel::onAgeChange,
                    label = "I confirm I am at least 13 years old.",
                )

                Spacer(Modifier.height(20.dp))

                CitadelButton(
                    text = if (state.submitting) "Creating account…" else "Create Account",
                    onClick = { if (!state.submitting) viewModel.submit() },
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
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDim,
                    )
                    Text(
                        text = "Log in instead",
                        style = MaterialTheme.typography.bodySmall,
                        color = Cyan,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onNavigateToLogin),
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "What Awaits Inside",
                    style = MaterialTheme.typography.titleLarge,
                    color = GoldBright,
                )
                Spacer(Modifier.height(20.dp))

                PerkCard("🔐", "Encrypted at Rest",
                    "Your email, name, and personal fields are encrypted " +
                            "with keys tied to your account.")
                Spacer(Modifier.height(12.dp))
                PerkCard("🛡️", "Stalker Shield",
                    "Visibility is a choice. No cold DMs. Blocking is " +
                            "silent, permanent, and mutual.")
                Spacer(Modifier.height(12.dp))
                PerkCard("🚫", "No Trackers",
                    "No Google Analytics, no Facebook Pixel, no session " +
                            "replay, no third-party scripts.")
                Spacer(Modifier.height(12.dp))
                PerkCard("⚡", "Two-Factor Available",
                    "Enable TOTP in seconds — compatible with any " +
                            "authenticator app.")
            }
        }

        item { Spacer(Modifier.height(48.dp)) }
    }
}

@Composable
private fun HelperText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = TextFaint,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun CitadelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Cyan,
                letterSpacing = 1.sp,
            )
        },
        placeholder = { Text(text = placeholder, color = TextFaint) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
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
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
) {
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Cyan,
                letterSpacing = 1.sp,
            )
        },
        placeholder = { Text(text = placeholder, color = TextFaint) },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
        ),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Filled.VisibilityOff
                    else Icons.Filled.Visibility,
                    contentDescription = if (visible) "Hide password" else "Show password",
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
}

@Composable
private fun PasswordStrengthMeter(strength: Int) {
    val color = when (strength) {
        0, 1, 2 -> Blood
        3, 4    -> Gold
        else    -> Success
    }
    val fill = (strength.coerceAtMost(6) / 6f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Cyan.copy(alpha = 0.1f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fill)
                .height(4.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(color),
        )
    }
}

private fun passwordHint(length: Int, strength: Int): String = when {
    length == 0 -> "Minimum 12 characters. A passphrase of three or four random words is stronger than a short complex string."
    length < 12 -> "${12 - length} more characters needed."
    strength <= 2 -> "Good length. Add variety or more words to strengthen."
    else -> "Strong."
}

@Composable
private fun CheckRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Cyan,
                uncheckedColor = Cyan.copy(alpha = 0.4f),
                checkmarkColor = Void,
            ),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextDim,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun PerkCard(icon: String, title: String, body: String) {
    CitadelPanel(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 26.sp)
            Spacer(Modifier.width(12.dp))
            Column {
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
}