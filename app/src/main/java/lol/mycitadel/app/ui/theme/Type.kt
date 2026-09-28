package lol.mycitadel.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import lol.mycitadel.app.R

// ── Font families ────────────────────────────────────────────────────────
val CinzelFamily = FontFamily(
    Font(R.font.cinzel_regular, FontWeight.Normal),
    Font(R.font.cinzel_bold,    FontWeight.Bold),
)

val OrbitronFamily = FontFamily(
    Font(R.font.orbitron_variable, FontWeight.Normal),
    Font(R.font.orbitron_variable, FontWeight.Medium),
    Font(R.font.orbitron_variable, FontWeight.Bold),
)

val JetBrainsMonoFamily = FontFamily(
    Font(R.font.jetbrains_mono_variable, FontWeight.Normal),
)

val InterFamily = FontFamily(
    Font(R.font.inter_variable, FontWeight.Normal),
)

// ── Typography system ────────────────────────────────────────────────────
val CitadelTypography = Typography(

    // ─── Display — hero titles ──────────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily = CinzelFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        letterSpacing = 4.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = CinzelFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        letterSpacing = 3.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = CinzelFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 2.sp,
    ),

    // ─── Headline — section titles ──────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily = CinzelFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        letterSpacing = 2.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 2.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 2.sp,
    ),

    // ─── Title — panel headers, buttons ─────────────────────────────────
    titleLarge = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = 2.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 2.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 2.sp,
    ),

    // ─── Body — readable content ────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    ),

    // ─── Label — buttons, tags, small UI ────────────────────────────────
    labelLarge = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 3.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 1.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 1.sp,
    ),
)