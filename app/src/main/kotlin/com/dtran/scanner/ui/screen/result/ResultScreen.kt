package com.dtran.scanner.ui.screen.result

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dtran.scanner.R
import com.dtran.scanner.data.Status
import com.dtran.scanner.navigation.Screen
import com.dtran.scanner.ui.widget.ProgressIndicator
import com.dtran.scanner.ui.widget.TopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ResultScreen(
    label: String,
    base64String: String,
    modifier: Modifier = Modifier,
    viewModel: ResultViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
) {
    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    val showProgressBarState = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopBar(isHome = false,
            title = stringResource(id = R.string.title_result),
            onBackArrowPressed = { navController.popBackStack() })
    },
        snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding),
        ) {
            Image(
                bitmap = decodedImage.asImageBitmap(),
                contentDescription = null,
                modifier = modifier
                    .fillMaxWidth()
                    .widthIn(0.dp, 300.dp)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.FillWidth
            )
            Text(text = label, textAlign = TextAlign.Center)
            Button(onClick = {
                uploadPhoto(coroutineScope, viewModel, label, imageBytes, showProgressBarState, snackbarHostState) {
                    navController.navigate(Screen.ListScreen) {
                        popUpTo(Screen.HomeScreen)
                    }
                }
            }) {
                Text(text = stringResource(id = R.string.upload_photo), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    ProgressIndicator(
        showProgressBarState = showProgressBarState.value,
        modifier = modifier.fillMaxSize()
    )
}

private fun uploadPhoto(
    coroutineScope: CoroutineScope,
    viewModel: ResultViewModel,
    label: String,
    byteArray: ByteArray,
    showProgressBarState: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    navigateCallback: () -> Unit
) {
    coroutineScope.launch {
        viewModel.uploadPhoto(label, byteArray).collectLatest {
            when (it) {
                is Status.Loading -> showProgressBarState.value = true
                is Status.Error -> {
                    showProgressBarState.value = false
                    snackbarHostState.showSnackbar(it.error ?: "")
                }

                is Status.Success -> {
                    showProgressBarState.value = false
                    navigateCallback.invoke()
                }
            }
        }
    }
}