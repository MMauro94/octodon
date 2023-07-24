package io.github.mmauro94.common.ui.components.comments

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR

@Composable
fun LoadingComments(depth: Int) {
    CommentContainer(depth = depth) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(MR.strings.loading___),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
