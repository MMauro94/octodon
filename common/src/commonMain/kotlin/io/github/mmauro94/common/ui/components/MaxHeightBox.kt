package io.github.mmauro94.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MaxHeightBox(
    maxHeight: Dp?,
    background: Color,
    content: @Composable () -> Unit,
) {
    if (maxHeight != null) {
        Layout(
            modifier = Modifier,
            content = {
                Scrim(background)
                content()
            },
        ) { measurables, constraints ->
            val maxHeightPx = maxHeight.roundToPx()
            val placeableScrim = measurables[0].measure(constraints)
            val placeableContent = measurables[1].measure(constraints)
            layout(placeableContent.width, minOf(placeableContent.height, maxHeightPx)) {
                placeableContent.place(0, 0)
                if (placeableContent.height > maxHeightPx) {
                    placeableScrim.place(0, maxHeightPx - placeableScrim.height)
                }
            }
        }
    } else {
        content()
    }
}

@Composable
private fun Scrim(background: Color) {
    Box(
        Modifier.fillMaxWidth().height(32.dp).background(
            Brush.verticalGradient(
                colors = listOf(background.copy(alpha = 0.1f), background),
            ),
        ),
    )
}
