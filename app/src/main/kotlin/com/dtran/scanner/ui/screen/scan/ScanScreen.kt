package com.dtran.scanner.ui.screen.scan

import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.dtran.scanner.R
import com.dtran.scanner.data.Status
import com.dtran.scanner.navigation.Screen
import com.dtran.scanner.ui.widget.ProgressIndicator
import com.dtran.scanner.ui.widget.TopBar
import com.dtran.scanner.util.Constant
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.common.model.CustomRemoteModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.linkfirebase.FirebaseModelSource
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel = koinViewModel()
) {
    val cameraPermission = rememberPermissionState(permission = android.Manifest.permission.CAMERA) {
        if (!it) navController.popBackStack()
    }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }
    val isModelReady = remember { mutableStateOf(false) }
    val isItemReady = remember { mutableStateOf(false) }
    val remoteModel = CustomRemoteModel.Builder(FirebaseModelSource.Builder(Constant.REMOTE_MODEL).build()).build()
    val downloadConditions = DownloadConditions.Builder().requireWifi().build()
    val snackbarHostState = remember { SnackbarHostState() }
    val label = remember { mutableStateOf("") }
    val buttonState = remember { mutableStateOf(false) }
    val image = remember { mutableStateOf("") }
    val analyzer = remember {
        ImageAnalyzer(remoteModel, { base64Str, des ->
            label.value = des
            image.value = base64Str
            buttonState.value = true
        }, {
            label.value = ""
            image.value = ""
            buttonState.value = false
        })
    }

    LaunchedEffect(key1 = cameraPermission.status) {
        if (!cameraPermission.status.isGranted) cameraPermission.launchPermissionRequest()
        else {
            cameraController.bindToLifecycle(lifecycleOwner)
            RemoteModelManager.getInstance().download(remoteModel, downloadConditions).addOnSuccessListener {
                isModelReady.value = true
                cameraController.setImageAnalysisAnalyzer(
                    context.mainExecutor, analyzer
                )
            }
        }
    }

    LaunchedEffect(key1 = label.value) {
        if (label.value.isNotEmpty()) snackbarHostState.showSnackbar(
            label.value, duration = SnackbarDuration.Indefinite
        )
    }

    LaunchedEffect(Unit) {
        viewModel.getItem().collectLatest {
            when (it) {
                is Status.Loading -> isItemReady.value = false
                is Status.Error -> {
                    isItemReady.value = true
                    snackbarHostState.showSnackbar(it.error ?: "")
                    analyzer.updateItem(it.data ?: emptyList())
                }

                is Status.Success -> {
                    isItemReady.value = true
                    analyzer.updateItem(it.data ?: emptyList())
                }
            }
        }
    }

    Scaffold(topBar = {
        TopBar(isHome = false,
            title = stringResource(id = R.string.title_scan),
            onBackArrowPressed = { navController.popBackStack() })
    }) { padding ->
        Box {
            AndroidView(modifier = modifier
                .fillMaxSize()
                .padding(padding), factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(Color.BLACK)
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }.also { previewView ->
                    previewView.controller = cameraController
                }
            })

            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.align(Alignment.BottomCenter)
            ) {
                SnackbarHost(snackbarHostState)
                Button(
                    onClick = {
                        navController.navigate(
                            Screen.ResultScreen(
                                base64String = image.value,
                                label = label.value
                            )
                        ) {
                            popUpTo(Screen.HomeScreen)
                        }
                    },
                    shape = CircleShape,
                    modifier = modifier
                        .padding(bottom = 20.dp)
                        .size(50.dp),
                    contentPadding = PaddingValues(0.dp),
                    enabled = buttonState.value && label.value.isNotEmpty() && image.value.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.White,
                    )
                }
            }

        }
    }

    ProgressIndicator(
        showProgressBarState = !(isModelReady.value and isItemReady.value),
        modifier = modifier.fillMaxSize()
    )
}