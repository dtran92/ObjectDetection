package com.dtran.scanner.ui.screen.home

import android.Manifest
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dtran.scanner.R
import com.dtran.scanner.navigation.Screen
import com.dtran.scanner.ui.widget.TopBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    snackbarScope: CoroutineScope,
    navController: NavHostController,
) {
    val context = LocalContext.current
    val cameraPermission = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    ) {
        if (it) navController.navigate(Screen.ScanScreen)
        else snackbarScope.launch {
            snackbarHostState.showSnackbar(context.getString(R.string.permission_camera))
        }
    }

    BackHandler {
        (context as Activity).finishAndRemoveTask()
    }

    Scaffold(
        topBar = { TopBar(isHome = true, title = stringResource(id = R.string.title_home), onBackArrowPressed = {}) },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(text = buildAnnotatedString {
                withStyle(style = SpanStyle()) {
                    append(stringResource(id = R.string.text_welcome_1))
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    append(Firebase.auth.currentUser?.email?.split("@")?.firstOrNull() ?: "")
                }
                withStyle(style = SpanStyle()) {
                    append(stringResource(id = R.string.text_welcome_2))
                }
                withStyle(style = SpanStyle()) {
                    append(stringResource(id = R.string.text_welcome_3))
                }
            }, textAlign = TextAlign.Center)
            Spacer(modifier = modifier.height(30.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Card(
                        shape = RoundedCornerShape(10.dp), onClick = { cameraPermission.launchPermissionRequest() },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null,
                            modifier = modifier
                                .size(100.dp)
                                .padding(20.dp)
                        )
                    }
                    Spacer(modifier = modifier.height(5.dp))
                    Text(text = stringResource(id = R.string.find_item), color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = modifier.width(40.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        onClick = { navController.navigate(Screen.ListScreen) },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_box),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null,
                            modifier = modifier
                                .size(100.dp)
                                .padding(20.dp)
                        )

                    }
                    Spacer(modifier = modifier.height(5.dp))
                    Text(
                        text = stringResource(id = R.string.view_item),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = modifier.height(30.dp))
            LogoutText {
                Firebase.auth.signOut()
                navController.popBackStack(Screen.HomeScreen, true)
                navController.navigate(Screen.LoginScreen)
            }
        }
    }
}

@Composable
private fun LogoutText(logout: () -> Unit) {
    val text = buildAnnotatedString {
        withStyle(style = SpanStyle().copy(fontSize = 17.sp)) {
            append(stringResource(id = R.string.text_logout_1))
        }
        pushStringAnnotation(tag = "click", annotation = "click")
        withStyle(style = SpanStyle().copy(fontSize = 25.sp, color = MaterialTheme.colorScheme.primary)) {
            append(stringResource(id = R.string.text_logout_2))
        }
        pop()
    }
    ClickableText(text = text, style = MaterialTheme.typography.bodyMedium, onClick = { offset ->
        text.getStringAnnotations(tag = "click", start = offset, end = offset).firstOrNull()?.let {
            logout.invoke()
        }
    })
}