package com.dtran.scanner.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.dtran.scanner.ui.screen.flag.FlagScreen
import com.dtran.scanner.ui.screen.home.HomeScreen
import com.dtran.scanner.ui.screen.list.ListScreen
import com.dtran.scanner.ui.screen.login.LoginScreen
import com.dtran.scanner.ui.screen.result.ResultScreen
import com.dtran.scanner.ui.screen.scan.ScanScreen
import com.dtran.scanner.ui.screen.web.WebScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope

@Composable
fun Navigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    snackbarScope: CoroutineScope,
    lottieComposition: LottieCompositionResult,
    modifier: Modifier
) {
    val currentUser = remember { Firebase.auth.currentUser }

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) TopLevelRoute.HomeRoute.route else Screen.LoginScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        modifier = modifier
    ) {
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                navController = navController,
                snackbarHostState = snackbarHostState,
                lottieCompositionResult = lottieComposition
            )

        }
        navigation(route = TopLevelRoute.HomeRoute.route, startDestination = Screen.HomeScreen.route) {
            composable(Screen.HomeScreen.route) {
                HomeScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    snackbarScope = snackbarScope,
                )
            }
            composable(Screen.ListScreen.route) {
                ListScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    lottieComposition = lottieComposition
                )

            }
            composable(Screen.ScanScreen.route) {
                ScanScreen(navController = navController, lottieCompositionResult = lottieComposition)

            }
            composable(Screen.ResultScreen.route + "/{base64String}" + "/{label}") {
                val base64String = it.arguments?.getString("base64String") ?: ""
                val label = it.arguments?.getString("label") ?: ""
                ResultScreen(
                    label = label,
                    base64String = base64String,
                    snackbarHostState = snackbarHostState,
                    navController = navController,
                    lottieCompositionResult = lottieComposition
                )

            }
            composable(Screen.WebScreen.route + "/{url}") {
                val url = it.arguments?.getString("url") ?: ""
                WebScreen(url = url, navController = navController, lottieCompositionResult = lottieComposition)

            }
        }
        navigation(route = TopLevelRoute.FlagRoute.route, startDestination = Screen.CountryListScreen.route) {
            composable(Screen.CountryListScreen.route) {
                FlagScreen(
                    navController = navController,
                    lottieCompositionResult = lottieComposition,
                )
            }
        }
    }
}