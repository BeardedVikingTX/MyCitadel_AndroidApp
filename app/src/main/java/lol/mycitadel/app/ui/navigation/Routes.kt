package lol.mycitadel.app.ui.navigation

/**
 * Every route in the app, in one place. Add new routes here — never use
 * magic strings at call sites.
 */
object Routes {
    const val HOME      = "home"
    const val ABOUT     = "about"
    const val CONTACT   = "contact"
    const val SECURITY  = "security"
    const val TERMS     = "terms"

    // Future routes — reserved now so we don't rename them later
    const val LOGIN     = "login"
    const val REGISTER  = "register"
    const val DASHBOARD = "dashboard"
    const val FEED      = "feed"
    const val USERS     = "users"
    const val PROFILE   = "profile"

    const val USER_VIEW = "user/{userId}"
    fun userView(userId: Int): String = "user/$userId"

    const val MESSAGES = "messages"
    const val CHAT = "chat/{conversationId}"
    fun chat(conversationId: Long): String = "chat/$conversationId"

    const val NOTIFICATIONS = "notifications"

    const val POST_VIEW = "post/{postId}"
    fun postView(postId: Int): String = "post/$postId"

    const val FORGOT_PASSWORD = "forgot-password"
    const val PREMIUM         = "premium"
}