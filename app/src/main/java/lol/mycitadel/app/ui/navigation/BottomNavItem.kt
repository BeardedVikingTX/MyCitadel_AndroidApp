package lol.mycitadel.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A single item in the bottom navigation bar.
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

/**
 * The bottom bar items shown to unauthenticated visitors — the public
 * marketing pages. Matches the web nav.php guest state.
 */
val GuestNavItems: List<BottomNavItem> = listOf(
    BottomNavItem(Routes.HOME,     "Home",     Icons.Filled.Home),
    BottomNavItem(Routes.ABOUT,    "About",    Icons.Filled.Info),
    BottomNavItem(Routes.CONTACT,  "Contact",  Icons.Filled.Email),
    BottomNavItem(Routes.SECURITY, "Security", Icons.Filled.Lock),
)

/**
 * The bottom bar items shown to authenticated users.
 * Not wired up yet — reserved for when auth ships.
 */
val AuthedNavItems: List<BottomNavItem> = listOf(
    BottomNavItem(Routes.FEED,      "Feed",      Icons.Filled.Home),
    BottomNavItem(Routes.DASHBOARD, "Dashboard", Icons.Filled.Info),
    BottomNavItem(Routes.USERS,     "Citizens",  Icons.Filled.Email),
    BottomNavItem(Routes.PROFILE,   "Profile",   Icons.Filled.Lock),
)

/**
 * Whether the bottom bar should be visible on this route.
 * Login/Register are modal-style flows — no bar.
 * Everything else shows the bar.
 */
fun shouldShowBottomBar(route: String?): Boolean {
    return when (route) {
        Routes.LOGIN, Routes.REGISTER -> false
        else                          -> true
    }
}