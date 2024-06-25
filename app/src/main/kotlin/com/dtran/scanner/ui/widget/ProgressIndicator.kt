package com.dtran.scanner.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.AsyncUpdates
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dtran.scanner.R

@Composable
fun ProgressIndicator(
    showProgressBarState: Boolean, modifier: Modifier = Modifier
) {

    when (showProgressBarState) {
        true -> {
            val composition = rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.anim_loading))
            Box(
                modifier = modifier, contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition.value, iterations = LottieConstants.IterateForever, asyncUpdates = AsyncUpdates.ENABLED,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        false -> Unit
    }
}