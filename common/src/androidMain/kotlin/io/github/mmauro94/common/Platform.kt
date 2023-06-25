package io.github.mmauro94.common

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import okio.Path.Companion.toOkioPath

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

@Composable
actual fun generateImageLoader(): ImageLoader {
    val context = LocalContext.current
    return ImageLoader {
        components {
            setupDefaultComponents(context)
        }
        interceptor {
            memoryCacheConfig {
                maxSizePercent(context, 0.25)
            }
            diskCacheConfig {
                directory(context.cacheDir.resolve("image_cache").toOkioPath())
                maxSizeBytes(512L * 1024 * 1024)
            }
        }
    }
}

@Composable
actual fun appColorScheme(): ColorScheme {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    return if (dynamicColor) {
        dynamicDarkColorScheme(LocalContext.current)
    } else {
        darkColorScheme()
    }
}

@Composable
actual fun UrlOpener(url: String, content: @Composable (openUrl: () -> Unit) -> Unit) {
    val ctx = LocalContext.current
    content {
        val i = Intent(Intent.ACTION_VIEW)
        i.setData(Uri.parse(url))
        ctx.startActivity(i)
    }
}
