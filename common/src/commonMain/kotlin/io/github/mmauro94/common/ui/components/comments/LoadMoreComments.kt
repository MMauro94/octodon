package io.github.mmauro94.common.ui.components.comments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.utils.LocalLemmyContext
import io.github.mmauro94.common.utils.comments.CommentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoadMoreComments(
    coroutineScope: CoroutineScope,
    commentList: CommentList<*>,
    depth: Int,
) {
    val client = LocalLemmyContext.current.client
    CommentContainer(
        modifier = Modifier.clickable {
            coroutineScope.launch { commentList.loadNext(client) }
        },
        depth = depth,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            text = stringResource(MR.strings.load_more_comments),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
