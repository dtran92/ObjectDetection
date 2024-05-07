package com.dtran.scanner.ui.screen.list

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieCompositionResult
import com.dtran.scanner.R
import com.dtran.scanner.ui.model.ItemUiModel

const val ANIMATION_DURATION = 500
const val MIN_DRAG_AMOUNT = 6

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableCard(
    item: ItemUiModel,
    isRevealed: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier,
    onItemClicked: () -> Unit, composition: LottieCompositionResult
) {
    val transitionState = remember {
        MutableTransitionState(isRevealed).apply {
            targetState = !isRevealed
        }
    }
    val transition = updateTransition(transitionState, null)
//    val cardBgColor by transition.animateColor(
//        label = "cardBgColorTransition",
//        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
//        targetValueByState = {
//            if (isRevealed) cardExpandedBackgroundColor else cardCollapsedBackgroundColor
//        }
//    )
    val offsetTransition = transition.animateDp(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (isRevealed) dimensionResource(id = R.dimen.size_action_button).times(-1) else 0.dp },
    )
//    val cardElevation by transition.animateDp(
//        label = "cardElevation",
//        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
//        targetValueByState = { if (isRevealed) 40.dp else 2.dp }
//    )

    Card(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)
        .offset {
            IntOffset(
                offsetTransition.value
                    .toPx()
                    .toInt(), 0
            )
        }
        .pointerInput(Unit) {
            detectHorizontalDragGestures { _, dragAmount ->
                when {
                    dragAmount < -MIN_DRAG_AMOUNT -> onExpand()
                    dragAmount >= MIN_DRAG_AMOUNT -> onCollapse()
                }
            }
        },
//        backgroundColor = cardBgColor,
//        shape = remember { RoundedCornerShape(10.dp) },
//        elevation = CardElevation,
        content = { ChildItem(item = item, onItemClicked = onItemClicked, composition = composition) })
}