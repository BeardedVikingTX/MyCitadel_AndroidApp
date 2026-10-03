package lol.mycitadel.app.ui.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import lol.mycitadel.app.MyCitadelApp
import lol.mycitadel.app.service.PushNotificationManager
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import lol.mycitadel.app.ui.screens.RegisterScreen
import lol.mycitadel.app.ui.screens.SecurityScreen
import lol.mycitadel.app.ui.screens.TermsScreen
import lol.mycitadel.app.ui.screens.DashboardScreen
import lol.mycitadel.app.ui.screens.LoginScreen
import lol.mycitadel.app.ui.screens.ProfileEditScreen
import lol.mycitadel.app.ui.screens.FeedScreen
import lol.mycitadel.app.ui.screens.UsersScreen
import lol.mycitadel.app.ui.screens.UserViewScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import android.content.Intent
import lol.mycitadel.app.ui.screens.MessagesScreen
import lol.mycitadel.app.ui.screens.ChatScreen
import lol.mycitadel.app.ui.screens.NotificationsScreen
import lol.mycitadel.app.ui.screens.ForgotPasswordScreen
import lol.mycitadel.app.ui.screens.PremiumScreen

@Composable
fun CitadelNavHost(
    modifier: Modifier = Modifier,
    pendingIntent: Intent? = null,
    onIntentHandled: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Session state: null = guest, non-null = authenticated
    var currentUser by remember { mutableStateOf<UserDto?>(null) }

    val context = LocalContext.current
    val app = context.applicationContext as MyCitadelApp
    val notificationsRepo = app.notificationsRepository
    val authRepo = app.authRepository

    // Always register FCM device token & restore session automatically on app startup
    LaunchedEffect(Unit) {
        PushNotificationManager.registerFcmToken(notificationsRepo)
        val user = authRepo.fetchMe()
        if (user != null) {
            currentUser = user
        }
    }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            PushNotificationManager.registerFcmToken(notificationsRepo)
            PushNotificationManager.startNotificationSync(context, notificationsRepo)
        } else {
            PushNotificationManager.stopNotificationSync()
        }
    }

    // Handle deep links from notification clicks
    LaunchedEffect(pendingIntent) {
        val intent = pendingIntent ?: return@LaunchedEffect

        val convId = intent.getLongExtra("conversation_id", 0L)
            .takeIf { it > 0L }
            ?: intent.getStringExtra("conversation_id")?.toLongOrNull()

        val postId = intent.getIntExtra("post_id", 0)
            .takeIf { it > 0 }
            ?: intent.getStringExtra("post_id")?.toIntOrNull()

        val actorId = intent.getIntExtra("actor_id", 0)
            .takeIf { it > 0 }
            ?: intent.getStringExtra("actor_id")?.toIntOrNull()

        val type = intent.getStringExtra("type") ?: intent.getStringExtra("notification_type")

        when {
            convId != null && convId > 0L -> {
                navController.navigate(Routes.chat(convId))
            }
            postId != null && postId > 0 -> {
                navController.navigate(Routes.postView(postId))
            }
            type != null && type.contains("comment") -> {
                navController.navigate(Routes.FEED)
            }
            type != null && type.contains("message") -> {
                navController.navigate(Routes.MESSAGES)
            }
            actorId != null && actorId > 0 -> {
                navController.navigate(Routes.userView(actorId))
            }
            type != null -> {
                navController.navigate(Routes.NOTIFICATIONS)
            }
        }

        onIntentHandled()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CitadelTopBar(
                currentUser = currentUser,
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onDashboardClick = { navController.navigate(Routes.DASHBOARD) },
                onPremiumClick = { navController.navigate(Routes.PREMIUM) },
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
                        if (!navController.popBackStack(route, inclusive = false)) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                )
            }
        },
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            // ── Public marketing pages ─────────────────────────────
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                    onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                    onNavigateToPremium = { navController.navigate(Routes.PREMIUM) },
                )
            }
            composable(Routes.ABOUT) {
                AboutScreen(
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                    onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                )
            }
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
                    onNavigateToForgotPassword = {
                        navController.navigate(Routes.FORGOT_PASSWORD)
                    },
                )
            }

            composable(Routes.PROFILE) {
                ProfileEditScreen(
                    onBack = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.PROFILE) { inclusive = true }
                        }
                    },
                )
            }

            composable(Routes.FEED) {
                FeedScreen(currentUser = currentUser)
            }

            composable(Routes.USERS) {
                UsersScreen(
                    currentUser = currentUser,
                    onOpenProfile = { id ->
                        navController.navigate(Routes.userView(id))
                    },
                    onOpenChat = { convId ->
                        navController.navigate(Routes.chat(convId))
                    },
                )
            }

            composable(
                route = Routes.USER_VIEW,
                arguments = listOf(navArgument("userId") { type = NavType.IntType }),
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                UserViewScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() },
                    onEditOwnProfile = { navController.navigate(Routes.PROFILE) },
                    onOpenDashboard = { navController.navigate(Routes.DASHBOARD) },
                )
            }

            composable(Routes.MESSAGES) {
                MessagesScreen(
                    onOpenChat = { convId -> navController.navigate(Routes.chat(convId)) },
                    onNewConversation = {
                        // Simplest v1: route to Citizens, user picks there
                        navController.navigate(Routes.USERS)
                    },
                )
            }

            composable(
                route = Routes.CHAT,
                arguments = listOf(navArgument("conversationId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val convId = backStackEntry.arguments?.getLong("conversationId") ?: 0L
                ChatScreen(
                    conversationId = convId,
                    currentUser = currentUser,
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Routes.NOTIFICATIONS) {
                NotificationsScreen(
                    onOpenUser = { id -> navController.navigate(Routes.userView(id)) },
                    onOpenPost = { id -> navController.navigate(Routes.postView(id)) },
                    onOpenChat = { convId -> navController.navigate(Routes.chat(convId)) },
                    onOpenMessages = { navController.navigate(Routes.MESSAGES) },
                )
            }

            composable(
                route = Routes.POST_VIEW,
                arguments = listOf(navArgument("postId") { type = NavType.IntType }),
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getInt("postId") ?: 0
                FeedScreen(
                    currentUser = currentUser,
                    focusedPostId = if (postId > 0) postId else null,
                )
            }

            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    onBackToLogin = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                        }
                    },
                )
            }

            composable(Routes.PREMIUM) {
                PremiumScreen(
                    currentUser = currentUser,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
