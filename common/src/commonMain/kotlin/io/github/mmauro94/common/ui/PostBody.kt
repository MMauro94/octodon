package io.github.mmauro94.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.mmauro94.common.markdown.ReadOnlyMarkdownElements
import io.github.mmauro94.common.markdown.parse
import io.github.mmauro94.common.ui.components.HeightLimitedColumn

@Composable
fun PostBody(
    modifier: Modifier,
    body: String,
    enableClicks: Boolean,
    maxHeight: Dp? = null,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Box(Modifier.padding(horizontal = 4.dp)) {
            HeightLimitedColumn(maxHeight, MaterialTheme.colorScheme.surfaceVariant) {
                Spacer(Modifier.height(8.dp))
                val ast = parse(body)
                ReadOnlyMarkdownElements(ast, enableClicks)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
