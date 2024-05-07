package com.dtran.scanner.ui.screen.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.AsyncUpdates
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieConstants
import com.dtran.scanner.ui.model.ItemUiModel

@Composable
fun ChildItem(
    item: ItemUiModel,
    onItemClicked: () -> Unit,
    composition: LottieCompositionResult,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onItemClicked.invoke() }
    )
    {
        SubcomposeAsyncImage(
            model = item.url,
            loading = {
//                CircularProgressIndicator(
//                    strokeCap = StrokeCap.Round,
//                    modifier = modifier.wrapContentSize(),
//                    color = MaterialTheme.colorScheme.secondary
//                )
                LottieAnimation(
                    composition.value,
                    iterations = LottieConstants.IterateForever,
                    asyncUpdates = AsyncUpdates.ENABLED,
                    modifier = modifier.wrapContentSize()
                )
            },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(125.dp)
                .padding(10.dp)
                .clip(CircleShape)
        )
        Text(
            text = item.text,
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}