package com.dtran.scanner.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object LoginScreen : Screen()

    @Serializable
    data object HomeScreen : Screen()

    @Serializable
    data object ScanScreen : Screen()

    @Serializable
    data object ListScreen : Screen()

    @Serializable
    data class ResultScreen(
        val base64String: String,
        val label: String
    ) : Screen()

    @Serializable
    data class WebScreen(
        val url: String
    ) : Screen()

    @Serializable
    data object CountryListScreen : Screen()
}

@Serializable
sealed class TopLevelRoute {
    @Serializable
    data object HomeRoute : TopLevelRoute()

    @Serializable
    data object FlagRoute : TopLevelRoute()
}