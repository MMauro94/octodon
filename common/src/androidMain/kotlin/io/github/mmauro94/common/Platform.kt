package io.github.mmauro94.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual fun getPlatformName(): String {
    return "Android"
}

@Composable
actual fun PlatformStyle(content: @Composable () -> Unit) {
    content()
}

@Composable
actual fun PlatformVerticalScrollbar(
    listState: LazyListState,
    modifier: Modifier,
    reverseLayout: Boolean,
    interactionSource: MutableInteractionSource,
) = Unit
