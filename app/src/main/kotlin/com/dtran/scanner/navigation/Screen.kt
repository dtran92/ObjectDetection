package com.dtran.scanner.navigation

sealed class Screen(val route: String) {
    data object LoginScreen : Screen("login_screen")
    data object HomeScreen : Screen("home_screen")
    data object ScanScreen : Screen("scan_screen")
    data object ListScreen : Screen("list_screen")
    data object ResultScreen : Screen("result_screen")
    data object WebScreen : Screen("web_screen")
    data object CountryListScreen : Screen("country_list_screen")
}

sealed class TopLevelRoute(val route: String) {
    data object HomeRoute : TopLevelRoute("home_route")
    data object FlagRoute : TopLevelRoute("flag_route")
}