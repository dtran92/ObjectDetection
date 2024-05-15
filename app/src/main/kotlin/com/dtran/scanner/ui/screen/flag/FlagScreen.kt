package com.dtran.scanner.ui.screen.flag

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieConstants
import com.dtran.scanner.R
import com.dtran.scanner.data.common.Resource
import com.dtran.scanner.navigation.TopLevelRoute
import com.dtran.scanner.ui.model.CountryUiModel
import com.dtran.scanner.ui.widget.ProgressIndicator
import com.dtran.scanner.ui.widget.TopBar
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@SuppressLint("RestrictedApi")
@Composable
fun FlagScreen(
    modifier: Modifier = Modifier,
    viewModel: FlagViewModel = koinViewModel(),
    navController: NavHostController,
    lottieCompositionResult: LottieCompositionResult,
) {
    val countryList = viewModel.countryList.collectAsStateWithLifecycle()
    val progressIndicatorState = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.countryFlow.collectLatest {
            when (it) {
                is Resource.Loading -> progressIndicatorState.value = true

                is Resource.Error -> {
                    progressIndicatorState.value = false
                    snackbarHostState.showSnackbar(it.error?.localizedMessage.toString())
                }

                is Resource.Success -> {
                    progressIndicatorState.value = false
                    viewModel.updateCountryList(it.data ?: emptyList())
                }
            }
        }
    }

    // Either go to home page or the page above home
    BackHandler {
        // navController.popBackStack()
        navController.navigate(TopLevelRoute.HomeRoute) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
        TopBar(isHome = true, title = stringResource(id = R.string.title_countries), onBackArrowPressed = {})
    }) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LazyColumn {
                items(items = countryList.value, key = { item -> item.id }) { country ->
                    CountryItem(
                        item = country,
                        onClickCallback = { },
                        modifier = modifier,
                        composition = lottieCompositionResult
                    )
                }
            }
            ProgressIndicator(progressIndicatorState.value, lottieCompositionResult, modifier)
        }
    }
}

@Composable
fun CountryItem(
    item: CountryUiModel,
    onClickCallback: (CountryUiModel) -> Unit,
    modifier: Modifier,
    composition: LottieCompositionResult
) {

    Row(modifier = modifier
        .fillMaxSize()
        .clickable { onClickCallback.invoke(item) }) {
        SubcomposeAsyncImage(
            model = item.media?.flag, loading = {
                LottieAnimation(
                    composition.value,
                    iterations = LottieConstants.IterateForever,
                    modifier = modifier.wrapContentSize()
                )
            }, contentDescription = null, modifier = modifier.size(150.dp)
        )
        Text(text = item.name ?: "", modifier = modifier.align(Alignment.CenterVertically))
    }
}
