package com.dtran.scanner.ui.screen.login

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieCompositionResult
import com.dtran.scanner.R
import com.dtran.scanner.data.Status
import com.dtran.scanner.navigation.TopLevelRoute
import com.dtran.scanner.ui.widget.ProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState,
    lottieCompositionResult: LottieCompositionResult,
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val showProgressBarState = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current as Activity
    val isVisiblePassword = remember { mutableStateOf(false) }

    BackHandler {
        context.finishAndRemoveTask()
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding(),
                    start = 20.dp,
                    end = 20.dp
                ), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = modifier.width(200.dp),
                colorFilter = ColorFilter.tint(color = Color.Black)
            )
            Spacer(modifier = modifier.height(40.dp))
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                modifier = modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(id = R.string.enter_email)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
            Spacer(modifier = modifier.height(10.dp))
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                modifier = modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(id = R.string.enter_password)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (isVisiblePassword.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isVisiblePassword.value = !isVisiblePassword.value }) {
                        if (!isVisiblePassword.value) Icon(
                            painterResource(id = R.drawable.ic_visibility), null
                        ) else Icon(
                            painterResource(id = R.drawable.ic_visibility_off), null
                        )
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
            Spacer(modifier = modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onLoginButtonClicked(
                            coroutineScope,
                            viewModel,
                            email.value,
                            password.value,
                            showProgressBarState,
                            snackbarHostState
                        ) { navController.navigate(TopLevelRoute.HomeRoute) }
                    }, modifier = modifier
                        .sizeIn(minWidth = 120.dp, minHeight = TextFieldDefaults.MinHeight)
                ) {
                    Text(
                        text = stringResource(id = R.string.login),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = modifier.width(20.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onRegisterButtonClicked(
                            coroutineScope,
                            viewModel,
                            email.value,
                            password.value,
                            showProgressBarState,
                            snackbarHostState
                        ) { navController.navigate(TopLevelRoute.HomeRoute) }
                    },
                    modifier = modifier
                        .sizeIn(minWidth = 120.dp, minHeight = TextFieldDefaults.MinHeight)

                )
                {
                    Text(
                        text = stringResource(id = R.string.register),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

        }
    }

    ProgressIndicator(showProgressBarState.value, lottieCompositionResult, modifier)
}

fun onRegisterButtonClicked(
    coroutineScope: CoroutineScope,
    viewModel: LoginViewModel,
    value: String,
    value1: String,
    showProgressBarState: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    function: () -> Unit
) {
    coroutineScope.launch {
        viewModel.register(value, value1).collectLatest {
            when (it) {
                is Status.Error -> {
                    showProgressBarState.value = false
                    it.error?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                    }
                }

                is Status.Loading -> showProgressBarState.value = true
                is Status.Success -> function.invoke()
            }
        }
    }
}

private fun onLoginButtonClicked(
    coroutineScope: CoroutineScope,
    viewModel: LoginViewModel,
    email: String,
    password: String,
    showProgressBarState: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    navigateToHome: () -> Unit
) {
    coroutineScope.launch {
        viewModel.login(email, password).collectLatest {
            when (it) {
                is Status.Error -> {
                    showProgressBarState.value = false
                    it.error?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                    }
                }

                is Status.Loading -> showProgressBarState.value = true
                is Status.Success -> navigateToHome.invoke()
            }
        }
    }
}

