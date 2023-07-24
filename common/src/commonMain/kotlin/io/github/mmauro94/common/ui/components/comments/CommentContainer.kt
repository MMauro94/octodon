package io.github.mmauro94.common.ui.components.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val DEPTH_WIDTH = 4.dp

@Composable
fun CommentContainer(
    depth: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .then(modifier),
    ) {
        Spacer(Modifier.width(DEPTH_WIDTH * (depth - 1)))
        if (depth > 0) {
            Box(Modifier.width(DEPTH_WIDTH).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
        }
        content()
    }
    Divider()
}
