package io.github.mmauro94.common.navigation

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import io.github.mmauro94.common.utils.progress

fun Modifier.stackAnimation(
    state: ItemAnimatableState,
    width: Dp,
    height: Dp,
    preferVertical: Boolean = false,
): Modifier {
    return graphicsLayer {
        val appearanceProgress = state.transitionState.value
        var focusProgress = 1 - state.topItemsVisibility()

        if (state.zIndex.value > 0f) {
            val translateVertically = when {
                state.currentManualOffsetY != 0f -> true
                state.currentManualOffsetX != 0f -> false
                else -> preferVertical
            }
            val ty = if (translateVertically) (1 - appearanceProgress) * height.toPx() else 0f
            val tx = if (!translateVertically) (1 - appearanceProgress) * width.toPx() else 0f
            this.translationY = ty + state.currentManualOffsetY
            this.translationX = tx + state.currentManualOffsetX
        } else {
            focusProgress = minOf(focusProgress, appearanceProgress)
        }
        this.scaleX *= (0.95f..1f).progress(focusProgress)
        this.scaleY *= (0.95f..1f).progress(focusProgress)
        this.transformOrigin = TransformOrigin(0.5f, 1f)
        val alphaRange = if (state.itemsOnTopCanSeeBehind) 0.6f..1f else 0f..1f
        this.alpha = alphaRange.progress(focusProgress)
    }
}

fun Modifier.slideAnimation(state: ItemAnimatableState, width: Dp): Modifier {
    return graphicsLayer {
        val appearanceProgress = state.transitionState.value * (1 - state.topItemsVisibility())
        val direction = if (state.isOnTop) 1f else -1f
        this.translationX =
            direction * (1 - appearanceProgress) * width.toPx() + state.currentManualOffsetX
    }
}

fun Modifier.crossFade(state: ItemAnimatableState, overlap: Float = 1f): Modifier {
    return graphicsLayer {
        val visibility = state.visibility() * (1 - state.topItemsVisibility())
        this.alpha = if (visibility > 1 - overlap) {
            (visibility - 1 + overlap) / overlap
        } else {
            0f
        }
    }
}
