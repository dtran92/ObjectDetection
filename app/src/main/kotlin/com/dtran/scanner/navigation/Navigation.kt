package com.dtran.scanner.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
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
    modifier: Modifier
) {
    val currentUser = remember { Firebase.auth.currentUser }

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) TopLevelRoute.HomeRoute else Screen.LoginScreen,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        modifier = modifier
    ) {
        composable<Screen.LoginScreen> {
            LoginScreen(
                goToHome = { navController.navigate(TopLevelRoute.HomeRoute) },
                snackbarHostState = snackbarHostState,
            )

        }
        navigation<TopLevelRoute.HomeRoute>(startDestination = Screen.HomeScreen) {
            composable<Screen.HomeScreen> {
                HomeScreen(
                    snackbarHostState = snackbarHostState,
                    snackbarScope = snackbarScope,
                    goToScan = { navController.navigate(Screen.ScanScreen) },
                    goToList = { navController.navigate(Screen.ListScreen) },
                    goToLogin = {
                        navController.popBackStack(Screen.HomeScreen, true)
                        navController.navigate(Screen.LoginScreen)
                    }
                )
            }
            composable<Screen.ListScreen> {
                ListScreen(
                    snackbarHostState = snackbarHostState,
                    goBack = { navController.popBackStack() },
                    goToWeb = { url ->
                        navController.navigate(
                            Screen.WebScreen(url = url)
                        )
                    }
                )
            }
            composable<Screen.ScanScreen> {
                ScanScreen(goBack = { navController.popBackStack() },
                    goToResult = { base64String, label ->
                        navController.navigate(
                            Screen.ResultScreen(
                                base64String = base64String,
                                label = label
                            )
                        )
                        {
                            popUpTo(Screen.HomeScreen)
                        }
                    })

            }
            composable<Screen.ResultScreen> {
                val base64String = it.toRoute<Screen.ResultScreen>().base64String
                val label = it.toRoute<Screen.ResultScreen>().label
                ResultScreen(
                    label = label,
                    base64String = base64String,
                    snackbarHostState = snackbarHostState,
                    goBack = { navController.popBackStack() },
                    goToList = {
                        navController.navigate(Screen.ListScreen) {
                            popUpTo(Screen.HomeScreen)
                        }
                    })

            }
            composable<Screen.WebScreen> {
                val url = it.toRoute<Screen.WebScreen>().url
                WebScreen(
                    url = url, goBack = { navController.popBackStack() },
                )

            }
        }
        navigation<TopLevelRoute.FlagRoute>(startDestination = Screen.CountryListScreen) {
            composable<Screen.CountryListScreen> {
                FlagScreen(
                    goBack = {
                        navController.navigate(TopLevelRoute.HomeRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}