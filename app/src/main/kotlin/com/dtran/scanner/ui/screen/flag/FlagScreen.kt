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
import coil.compose.SubcomposeAsyncImage
import com.dtran.scanner.R
import com.dtran.scanner.data.common.Resource
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
    goBack: () -> Unit
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
                    println("???")
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
        goBack.invoke()
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
                    )
                }
            }
            ProgressIndicator(showProgressBarState = progressIndicatorState.value, modifier = modifier.fillMaxSize())
        }
    }
}

@Composable
fun CountryItem(
    item: CountryUiModel,
    onClickCallback: (CountryUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(modifier = modifier
        .fillMaxSize()
        .clickable { onClickCallback.invoke(item) }) {
        SubcomposeAsyncImage(
            model = item.media?.flag, loading = {
                ProgressIndicator(showProgressBarState = true, modifier = modifier.fillMaxSize())
            }, contentDescription = null, modifier = modifier.size(150.dp)
        )
        Text(text = item.name ?: "", modifier = modifier.align(Alignment.CenterVertically))
    }
}
