package io.github.mmauro94.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.mmauro94.common.markdown.ReadOnlyMarkdown
import io.github.mmauro94.common.ui.components.MaxHeightBox

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
        MaxHeightBox(maxHeight, MaterialTheme.colorScheme.surfaceVariant) {
            Box(Modifier.padding(horizontal = 4.dp, vertical = 8.dp)) {
                ReadOnlyMarkdown(body, enableClicks)
            }
        }
    }
}
