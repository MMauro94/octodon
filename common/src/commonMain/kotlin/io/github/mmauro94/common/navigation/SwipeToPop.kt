package io.github.mmauro94.common.navigation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Modifier.swipeToPop(
    state: ItemAnimatableState,
    screenWidth: Dp,
    screenHeight: Dp,
    vertical: Boolean = false,
    horizontal: Boolean = state.canPop,
): Modifier {
    var ret = this
    if (horizontal) {
        ret = ret.createSwipeable(state.canPop, screenWidth, state.horizontalSwipe, Orientation.Horizontal)
    }
    if (vertical) {
        ret = ret.createSwipeable(state.canPop, screenHeight, state.verticalSwipe, Orientation.Vertical, true)
    }
    return ret
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Modifier.createSwipeable(
    canPop: Boolean,
    size: Dp,
    swipeableState: SwipeableState<Boolean>,
    orientation: Orientation,
    blockWrongDirectionSwiper: Boolean = false,
): Modifier {
    val happyPathAnchors = mapOf(0f to true, with(LocalDensity.current) { size.toPx() } to false)
    val anchors = if (canPop) happyPathAnchors else mapOf(0f to true)
    val resistance = if (canPop) {
        SwipeableDefaults.StandardResistanceFactor
    } else {
        SwipeableDefaults.StiffResistanceFactor
    }

    return swipeable(
        swipeableState,
        anchors = anchors,
        thresholds = { _, _ -> FractionalThreshold(0.5f) },
        orientation = orientation,
        resistance = SwipeableDefaults.resistanceConfig(
            happyPathAnchors.keys,
            if (blockWrongDirectionSwiper) 0f else resistance,
            resistance,
        ),
    )
}
