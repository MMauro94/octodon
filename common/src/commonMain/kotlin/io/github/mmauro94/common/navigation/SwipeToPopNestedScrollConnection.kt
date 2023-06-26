package io.github.mmauro94.common.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

class SwipeToPopNestedScrollConnection(
    private val state: ItemAnimatableState,
) : NestedScrollConnection {

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource,
    ): Offset = when {
        // If the user is swiping up, handle it
        available.y < 0 -> onScroll(available)
        else -> Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        if (source != NestedScrollSource.Drag) return Offset.Zero
        return onScroll(available)
    }

    @OptIn(ExperimentalMaterialApi::class)
    private fun onScroll(available: Offset): Offset {
        val consumed = state.verticalSwipe.performDrag(available.y)
        return Offset(x = 0f, y = consumed)
    }

    @OptIn(ExperimentalMaterialApi::class)
    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        state.verticalSwipe.performFling(available.y)
        return Velocity(0f, available.y)
    }
}
