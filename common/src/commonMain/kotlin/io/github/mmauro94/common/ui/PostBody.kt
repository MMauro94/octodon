package io.github.mmauro94.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.mmauro94.common.markdown.Markdown
import io.github.mmauro94.common.markdown.MarkdownElement
import io.github.mmauro94.common.markdown.ReadOnlyMarkdownElement
import io.github.mmauro94.common.ui.components.HeightLimitedColumn

@Composable
fun PostBody(
    modifier: Modifier,
    body: Markdown,
    enableClicks: Boolean,
    maxHeight: Dp? = null,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Box(Modifier.padding(horizontal = 4.dp)) {
            val elements = buildList(body.elements.size + 2) {
                add(MarkdownElement.Spacer(8.dp))
                addAll(body.elements)
                add(MarkdownElement.Spacer(8.dp))
            }
            HeightLimitedColumn(
                maxHeight,
                MaterialTheme.colorScheme.surfaceVariant,
                elements,
            ) { element ->
                ReadOnlyMarkdownElement(element, enableClicks)
            }
        }
    }
}
