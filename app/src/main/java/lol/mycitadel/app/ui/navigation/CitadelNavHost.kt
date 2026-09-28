package lol.mycitadel.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import lol.mycitadel.app.data.network.UserDto
import lol.mycitadel.app.ui.components.CitadelBottomNavBar
import lol.mycitadel.app.ui.components.CitadelTopBar
import lol.mycitadel.app.ui.screens.AboutScreen
import lol.mycitadel.app.ui.screens.ContactScreen
import lol.mycitadel.app.ui.screens.HomeScreen
import lol.mycitadel.app.ui.screens.PlaceholderScreen
import lol.mycitadel.app.ui.screens.RegisterScreen
import lol.mycitadel.app.ui.screens.SecurityScreen
import lol.mycitadel.app.ui.screens.TermsScreen
import lol.mycitadel.app.ui.screens.DashboardScreen
import lol.mycitadel.app.ui.screens.LoginScreen

@Composable
fun CitadelNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Session state: null = guest, non-null = authenticated
    var currentUser by remember { mutableStateOf<UserDto?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CitadelTopBar(
                currentUser = currentUser,
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onDashboardClick = { navController.navigate(Routes.DASHBOARD) },
                onLogoutComplete = {
                    currentUser = null
                    navController.navigate(Routes.HOME) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
            )
        },
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                CitadelBottomNavBar(
                    items = if (currentUser != null) AuthedNavItems else GuestNavItems,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            // ── Public marketing pages ─────────────────────────────
            composable(Routes.HOME)     { HomeScreen() }
            composable(Routes.ABOUT)    { AboutScreen() }
            composable(Routes.CONTACT)  { ContactScreen() }
            composable(Routes.SECURITY) { SecurityScreen() }
            composable(Routes.TERMS)    { TermsScreen() }

            // ── Auth flows ─────────────────────────────────────────
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegistered = { user ->
                        currentUser = user
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    },
                )
            }

            composable(Routes.LOGIN) {
                PlaceholderScreen(
                    title = "Log In",
                    subtitle = "Coming next — the login screen mirrors the register flow.",
                )
            }

            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onLogout = {
                        currentUser = null
                        navController.navigate(Routes.HOME) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                )
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoggedIn = { user ->
                        currentUser = user
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}