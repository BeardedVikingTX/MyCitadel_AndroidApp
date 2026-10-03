package lol.mycitadel.app.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import lol.mycitadel.app.ui.auth.ForgotPasswordState
import lol.mycitadel.app.ui.auth.ForgotPasswordViewModel
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
import lol.mycitadel.app.ui.theme.Void

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = viewModel(factory = ForgotPasswordViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                RuneDivider()
                Spacer(Modifier.height(16.dp))

                if (state.submitted) {
                    SuccessState(
                        typedEmail = state.typedEmail,
                        onTryAgain = viewModel::tryAgain,
                        onBackToLogin = onBackToLogin,
                    )
                } else {
                    FormState(state, viewModel, onBackToLogin)
                }
            }
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * FORM STATE
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun FormState(
    state: ForgotPasswordState,
    vm: ForgotPasswordViewModel,
    onBackToLogin: () -> Unit,
) {
    GlitchText(
        text = "Forgot Password",
        style = MaterialTheme.typography.headlineLarge,
    )

    Spacer(Modifier.height(12.dp))

    Text(
        text = "Enter the email address on your account. If it matches, " +
                "we'll send a single-use link that expires in 15 minutes.",
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

    OutlinedTextField(
        value = state.email,
        onValueChange = vm::onEmailChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.submitting,
        label = {
            Text(
                text = "EMAIL ADDRESS",
                style = MaterialTheme.typography.labelSmall,
                color = Cyan,
                letterSpacing = 1.sp,
            )
        },
        placeholder = {
            Text(text = "you@example.com", color = TextFaint)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
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

    Spacer(Modifier.height(8.dp))

    Text(
        text = "We'll send the reset link to this address.",
        style = MaterialTheme.typography.bodySmall,
        color = TextFaint,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(20.dp))

    CitadelButton(
        text = if (state.submitting) "Sending…" else "Send Reset Link",
        onClick = vm::submit,
        style = CitadelButtonStyle.Gold,
        modifier = Modifier.fillMaxWidth(),
        enabled = state.canSubmit,
    )

    if (state.submitting) {
        Spacer(Modifier.height(14.dp))
        CircularProgressIndicator(
            color = Cyan,
            strokeWidth = 2.dp,
            modifier = Modifier.size(28.dp),
        )
    }

    Spacer(Modifier.height(18.dp))

    Text(
        text = "← Back to login",
        style = MaterialTheme.typography.bodySmall,
        color = Cyan,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clickable(onClick = onBackToLogin)
            .padding(8.dp),
    )
}

/* ══════════════════════════════════════════════════════════════════
 * SUCCESS STATE
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun SuccessState(
    typedEmail: String,
    onTryAgain: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    val ctx = LocalContext.current

    // Big success check
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Success.copy(alpha = 0.08f))
            .border(2.dp, Success.copy(alpha = 0.55f), RoundedCornerShape(999.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text("✓", color = Success, fontSize = 44.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(Modifier.height(20.dp))

    Text(
        text = "Check your inbox",
        style = MaterialTheme.typography.headlineMedium,
        color = GoldBright,
        textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(12.dp))

    Text(
        text = "If an account exists for $typedEmail, we've sent a reset " +
                "link. It expires in 15 minutes and can only be used once.",
        style = MaterialTheme.typography.bodyMedium,
        color = TextDim,
        textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(24.dp))

    // Troubleshooting panel
    CitadelPanel(
        modifier = Modifier.fillMaxWidth(),
        borderColor = Cyan.copy(alpha = 0.18f),
    ) {
        Text(
            text = "NOT SEEING IT?",
            style = MaterialTheme.typography.labelSmall,
            color = Cyan,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        TipsRow("Check your spam folder", "Reset emails often land there.")
        Spacer(Modifier.height(8.dp))
        TipsRow("Look in Promotions/Updates", "Gmail and Outlook sometimes sort them there.")
        Spacer(Modifier.height(8.dp))
        TipsRow("Give it a minute", "Delivery usually completes in under 60 seconds.")
    }

    Spacer(Modifier.height(20.dp))

    // "Open mail app" — tries to launch the default email client.
    CitadelButton(
        text = "Open Mail App",
        onClick = {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_EMAIL)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                ctx.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                // No email app configured — silently ignore. The user can
                // still open their mail manually.
            }
        },
        style = CitadelButtonStyle.Gold,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(10.dp))

    CitadelButton(
        text = "Try a Different Email",
        onClick = onTryAgain,
        style = CitadelButtonStyle.Cyan,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(10.dp))

    CitadelButton(
        text = "Back to Login",
        onClick = onBackToLogin,
        style = CitadelButtonStyle.Cyan,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(16.dp))

    Text(
        text = "Reset the password in your browser, then return here " +
                "and log in with the new one.",
        style = MaterialTheme.typography.bodySmall,
        color = TextFaint,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun TipsRow(title: String, body: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("›", color = Cyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = TextDim,
                fontSize = 11.sp,
            )
        }
    }
}