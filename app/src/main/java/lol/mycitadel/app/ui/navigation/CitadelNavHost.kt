package lol.mycitadel.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import lol.mycitadel.app.ui.components.CitadelBottomNavBar
import lol.mycitadel.app.ui.components.CitadelTopBar
import lol.mycitadel.app.ui.screens.AboutScreen
import lol.mycitadel.app.ui.screens.HomeScreen
import lol.mycitadel.app.ui.screens.PlaceholderScreen
import lol.mycitadel.app.ui.screens.SecurityScreen
import lol.mycitadel.app.ui.screens.ContactScreen
import lol.mycitadel.app.ui.screens.TermsScreen

@Composable
fun CitadelNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CitadelTopBar(
                onLoginClick = {
                    // TODO: navigate to Login when auth ships
                },
                onRegisterClick = {
                    // TODO: navigate to Register when auth ships
                },
            )
        },
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                CitadelBottomNavBar(
                    items = GuestNavItems,
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
            composable(Routes.HOME) {
                HomeScreen()
            }
            composable(Routes.ABOUT) {
                AboutScreen()
            }
            composable(Routes.CONTACT) {
                ContactScreen()
            }
            composable(Routes.SECURITY) {
                SecurityScreen()
            }
            composable(Routes.TERMS) {
                TermsScreen()
            }
        }
    }
}