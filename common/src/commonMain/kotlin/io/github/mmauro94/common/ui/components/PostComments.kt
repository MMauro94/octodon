package io.github.mmauro94.common.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.api.getComments
import io.github.mmauro94.common.client.entities.CommentSortType
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.utils.AsyncState
import io.github.mmauro94.common.utils.LocalLemmyContext
import io.github.mmauro94.common.utils.Result
import io.github.mmauro94.common.utils.comments.CommentList
import io.github.mmauro94.common.utils.comments.CommentListState
import io.github.mmauro94.common.utils.comments.CommentTree
import io.github.mmauro94.common.utils.comments.PostComments
import io.github.mmauro94.common.utils.comments.toPostComments
import io.github.mmauro94.common.utils.composeWorker
import io.github.mmauro94.common.utils.map
import io.github.mmauro94.common.utils.process

@Composable
fun PostComments(
    header: @Composable () -> Unit,
    postView: PostView,
) {
    val lemmyContext = LocalLemmyContext.current
    val (asyncState, workerChannel) = composeWorker<PostView, PostComments, StringResource>(
        process = { input ->
            when (
                val result = lemmyContext.client.getComments(
                    postId = input.post.id,
                    sort = CommentSortType.TOP,
                    limit = 100,
                )
            ) {
                is Result.Success -> result.map { it.toPostComments(input) }
                is Result.Error -> Result.Error(MR.strings.connection_error)
            }
        },
    )
    LaunchedEffect(postView) {
        workerChannel.process(postView)
    }

    when (asyncState) {
        AsyncState.Loading, AsyncState.Resting -> CircularProgressIndicator()
        is AsyncState.Error -> Text(stringResource(asyncState.error))
        is AsyncState.Success -> PostComments(header, asyncState.result)
    }
}

@Composable
private fun PostComments(
    header: @Composable () -> Unit,
    postComments: PostComments,
) {
    val state = rememberLazyListState()

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp), state = state) {
        item {
            header()
        }
        items(postComments)
    }
}

private fun LazyListScope.items(postComments: PostComments) {
    items(postComments.commentList, 0)
}

private fun LazyListScope.items(commentTree: CommentTree, depth: Int) {
    val comment = commentTree.commentView.comment
    item {
        Box(
            Modifier
                .clickable { commentTree.collapsed = !commentTree.collapsed }
                .padding(start = (16 * depth).dp),
        ) {
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
    if (!commentTree.collapsed) {
        items(commentTree.children.value, depth + 1)
    }
}

private fun LazyListScope.items(commentList: CommentList<*>, depth: Int) {
    for (comment in commentList.comments.values) {
        items(comment, depth)
    }
    item {
        when (commentList.state) {
            is Result.Error -> Text("Error loading more comments")

            is Result.Success -> when (commentList.state.result) {
                CommentListState.MORE_COMMENTS -> Text("Load more comments")

                CommentListState.FINISHED -> {}
                CommentListState.LOADING -> Text("Loading more comments...")
            }
        }
    }
}
