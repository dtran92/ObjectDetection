package com.dtran.scanner.ui.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.dtran.scanner.R
import com.dtran.scanner.navigation.Screen
import com.dtran.scanner.navigation.TopLevelRoute

@Composable
fun BottomBar(
    rootNavController: NavController, modifier: Modifier = Modifier
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
        val currentTopLevelDestination by rootNavController.currentBottomBarItemAsState()
        BottomBarItem.entries.forEach { screen ->
            val isTabSelected = screen == currentTopLevelDestination
            NavigationBarItem(
                isTabSelected, onClick = {
                    if (!isTabSelected) rootNavController.navigate(screen.route) {
                        popUpTo(rootNavController.graph.findStartDestination().id) {
                            saveState = !isTabSelected
                        }
                        launchSingleTop = true
                        restoreState = !isTabSelected
                    }
                }, icon = {
                    Icon(
                        ImageVector.vectorResource(id = screen.iconId),
                        contentDescription = null,
                        modifier = modifier.size(36.dp)
                    )
                }, colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFF95C9),
                    selectedTextColor = Color(0xFFFF95C9),
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

enum class BottomBarItem(val route: TopLevelRoute, val iconId: Int, @StringRes val label: Int) {
    Home(TopLevelRoute.HomeRoute, R.drawable.ic_home, R.string.label_home),
    Flag(TopLevelRoute.FlagRoute, R.drawable.ic_flag, R.string.label_flag)
}

/**
 * Adds an [NavController.OnDestinationChangedListener] to this [NavController] and updates the
 * returned [State] which is updated as the destination changes.
 */
@Composable
private fun NavController.currentBottomBarItemAsState(): State<BottomBarItem> {
    val selectedItem = remember { mutableStateOf(BottomBarItem.Home) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == Screen.CountryListScreen.javaClass.canonicalName } -> {
                    selectedItem.value = BottomBarItem.Flag
                }

                // TopLevelDestination.HOME is the start destination and, therefore, part of any stack
                else -> {
                    selectedItem.value = BottomBarItem.Home
                }
            }
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}