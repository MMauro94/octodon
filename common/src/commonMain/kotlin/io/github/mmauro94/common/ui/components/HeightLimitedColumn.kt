package io.github.mmauro94.common.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.lazy.layout.LazyLayoutIntervalContent
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.foundation.lazy.layout.MutableIntervalList
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
private class HeightLimitedColumnIntervalContent(
    val item: @Composable (index: Int) -> Unit,
) : LazyLayoutIntervalContent {
    override val key: ((index: Int) -> Any)? = null
    override val type: ((index: Int) -> Any?) = { null }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> HeightLimitedColumn(
    maxHeight: Dp?,
    background: Color,
    items: List<T>,
    itemComposable: @Composable (T) -> Unit,
) {
    if (maxHeight != null) {
        LazyLayout(
            LazyLayoutItemProvider(
                intervals = MutableIntervalList<HeightLimitedColumnIntervalContent>().apply {
                    this.addInterval(
                        1,
                        HeightLimitedColumnIntervalContent {
                            Scrim(background)
                        },
                    )
                    this.addInterval(
                        items.size,
                        HeightLimitedColumnIntervalContent { index ->
                            itemComposable(items[index])
                        },
                    )
                },
                nearestItemsRange = 0..100,
                itemContent = { interval, index ->
                    interval.value.item(index - interval.startIndex)
                },
            ),
        ) { constraints ->
            val placeableScrim = this.measure(0, constraints).single()
            val maxHeightPx = maxHeight.roundToPx()
            val placeableContent = mutableListOf<Placeable>()
            var contentH = 0
            var maxW = placeableScrim.width
            for (i in items.indices) {
                val placeables = this.measure(i + 1, constraints)
                for (p in placeables) {
                    contentH += p.height
                    maxW = maxOf(maxW, p.width)
                    placeableContent.add(p)
                    if (contentH > maxHeightPx) {
                        break
                    }
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
            for (item in items) {
                itemComposable(item)
            }
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
