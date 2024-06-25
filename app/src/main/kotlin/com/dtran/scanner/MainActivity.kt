package com.dtran.scanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dtran.scanner.navigation.Navigation
import com.dtran.scanner.navigation.Screen
import com.dtran.scanner.ui.theme.ScannerTheme
import com.dtran.scanner.ui.widget.BottomBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setUpSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            ScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val keyboardController = LocalSoftwareKeyboardController.current
                    val interactionSource = remember { MutableInteractionSource() }
                    val focusManager = LocalFocusManager.current
                    val navController = rememberNavController()
                    val snackbarHostState = remember { SnackbarHostState() }
                    val snackbarScope = rememberCoroutineScope()

                    KoinAndroidContext {
                        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, bottomBar = {
                            if (navController.currentBackStackEntryAsState().value?.destination?.route != Screen.LoginScreen.javaClass.canonicalName &&
                                navController.currentBackStackEntryAsState().value?.destination?.route != Screen.ScanScreen.javaClass.canonicalName
                            )
                                BottomBar(
                                    rootNavController = navController
                                )
                        }) {
                            Navigation(
                                navController = navController,
                                snackbarHostState = snackbarHostState,
                                snackbarScope = snackbarScope,
                                modifier = Modifier
                                    .padding(it)
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null    // this gets rid of the ripple effect
                                    ) {
                                        keyboardController?.hide()
                                        focusManager.clearFocus(true)
                                    })
                        }
                    }
                }
            }
        }
    }

    private fun setUpSplashScreen() {
        var shouldShowSplashScreen = true
        lifecycleScope.launch {
            delay(500L)
            shouldShowSplashScreen = false
        }
        installSplashScreen().setKeepOnScreenCondition {
            shouldShowSplashScreen
        }
    }
}