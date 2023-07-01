package io.github.mmauro94.common

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import net.harawata.appdirs.AppDirsFactory
import okio.Path.Companion.toOkioPath
import java.io.File

@Composable
actual fun PlatformStyle(content: @Composable () -> Unit) = CompositionLocalProvider(
    LocalScrollbarStyle provides defaultScrollbarStyle().copy(
        unhoverColor = Color.White.copy(alpha = 0.12f),
        hoverColor = Color.White.copy(alpha = 0.40f),
        thickness = 12.dp,
        shape = RectangleShape,
    ),
    content = content,
)

@Composable
actual fun PlatformVerticalScrollbar(
    listState: LazyListState,
    modifier: Modifier,
    reverseLayout: Boolean,
    interactionSource: MutableInteractionSource,
) {
    VerticalScrollbar(
        modifier = modifier,
        adapter = rememberScrollbarAdapter(listState),
        reverseLayout = reverseLayout,
        interactionSource = interactionSource,
    )
}

@Composable
actual fun generateImageLoader(): ImageLoader {
    return ImageLoader {
        components {
            setupDefaultComponents()
        }
        interceptor {
            memoryCacheConfig {
                maxSizePercent(percent = 0.25)
            }
            diskCacheConfig {
                val imagesCache = File(
                    AppDirsFactory.getInstance().getUserCacheDir(APP_DIR_NAME, null, null),
                    "images",
                )
                imagesCache.parentFile.mkdirs()
                directory(imagesCache.toOkioPath())
                maxSizeBytes(512L * 1024 * 1024)
            }
        }
    }
}

@Composable
actual fun appColorScheme(): ColorScheme {
    return darkColorScheme()
}
