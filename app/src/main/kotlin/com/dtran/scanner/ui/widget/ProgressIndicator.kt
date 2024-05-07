package com.dtran.scanner.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.AsyncUpdates
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieConstants

@Composable
fun ProgressIndicator(
    showProgressBarState: Boolean, composition: LottieCompositionResult, modifier: Modifier
) {

    when (showProgressBarState) {
        true -> Box(
            modifier = modifier
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
//            CircularProgressIndicator(strokeCap = StrokeCap.Round)
            LottieAnimation(
                composition.value, iterations = LottieConstants.IterateForever, asyncUpdates = AsyncUpdates.ENABLED
            )
        }

        false -> Unit
    }
}