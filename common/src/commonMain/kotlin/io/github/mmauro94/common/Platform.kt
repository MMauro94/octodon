package io.github.mmauro94.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.seiko.imageloader.ImageLoader

@Composable
expect fun PlatformStyle(content: @Composable () -> Unit)

@Composable
expect fun PlatformVerticalScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
)

@Composable
expect fun generateImageLoader(): ImageLoader

@Composable
expect fun appColorScheme(): ColorScheme
