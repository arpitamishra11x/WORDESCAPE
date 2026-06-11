package com.wordscape.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Learning : Screen("learning/{worldId}/{wordIndex}") {
        fun createRoute(worldId: String, wordIndex: Int) = "learning/$worldId/$wordIndex"
    }
    object Journal : Screen("journal")
    object Forest : Screen("forest")
    object Settings : Screen("settings")
    object AR : Screen("ar")
}
