package com.dtran.scanner.ui.widget


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(isHome: Boolean, title: String, onBackArrowPressed: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge.copy(color = Color.White)) },
        navigationIcon = {
            if (!isHome) IconButton(onClick = onBackArrowPressed) {
                Icon(
                    painter = painterResource(id = com.dtran.scanner.R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Preview
@Composable
fun Preview() {
    TopBar(isHome = false, title = "Home", onBackArrowPressed = {})
}