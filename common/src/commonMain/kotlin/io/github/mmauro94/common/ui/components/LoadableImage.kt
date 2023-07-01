package io.github.mmauro94.common.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import com.seiko.imageloader.rememberImagePainter
import io.ktor.http.Url

private val errorPainter = ColorPainter(Color.Red)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoadableImage(
    loadedModifier: Modifier,
    notLoadedModifier: Modifier,
    image: Url,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val loadingColor = MaterialTheme.colorScheme.surfaceVariant
    val loading = remember(loadingColor) {
        ColorPainter(loadingColor)
    }
    val painter = rememberImagePainter(
        image.toString(),
        placeholderPainter = { loading },
        errorPainter = { errorPainter },
    )
    // TODO seems to not work with webp
    AnimatedContent(
        targetState = painter,
        transitionSpec = { fadeIn() with fadeOut() using SizeTransform() },
    ) { p ->
        val modifier = if (p == loading || p == errorPainter) {
            notLoadedModifier
        } else {
            loadedModifier
        }
        Image(
            painter = p,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
        )
    }
}
