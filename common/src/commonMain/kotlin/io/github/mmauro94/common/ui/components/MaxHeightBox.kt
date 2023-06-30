package io.github.mmauro94.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
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
                content()
                Scrim(background)
            },
        ) { measurables, constraints ->
            val maxHeightPx = maxHeight.roundToPx()
            val placeableContent = measurables[0].measure(constraints)
            val placeableScrim = measurables[1].measure(Constraints.fixed(placeableContent.width, 32.dp.roundToPx()))
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
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                colors = listOf(background.copy(alpha = 0.1f), background),
            ),
        ),
    )
}
