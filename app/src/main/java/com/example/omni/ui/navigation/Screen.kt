package com.example.omni.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object IndexPage : Screen(route = "index_page")
    object Dashboard : Screen("dashboard")
    object Todo : Screen("todo")
    object Challenges : Screen("challenges")
    object Clock : Screen("clock")
    object Weather : Screen("weather")
    object Gps : Screen("gps")
    object Sensors : Screen("sensors")
    object Calendar : Screen("calendar")
    object Settings : Screen("settings")
}
