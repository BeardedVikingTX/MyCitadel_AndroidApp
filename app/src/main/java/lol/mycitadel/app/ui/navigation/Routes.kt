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
}