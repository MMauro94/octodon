package io.github.mmauro94.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HeightLimitedColumn(
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
            val placeableContent = mutableListOf<Placeable>()

            var contentH = 0
            var maxW = placeableScrim.width
            for (i in 1 until measurables.size) {
                val p = measurables[i].measure(constraints)
                contentH += p.height
                maxW = maxOf(maxW, p.width)
                placeableContent.add(p)
                if (contentH > maxHeightPx) {
                    break
                }
            }

            layout(maxW, minOf(contentH, maxHeightPx)) {
                var h = 0
                for (p in placeableContent) {
                    p.place(0, h)
                    h += p.height
                }
                if (contentH > maxHeightPx) {
                    placeableScrim.place(0, maxHeightPx - placeableScrim.height)
                }
            }
        }
    } else {
        Column {
            content()
        }
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
