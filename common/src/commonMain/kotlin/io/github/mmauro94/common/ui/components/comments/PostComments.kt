package io.github.mmauro94.common.ui.components.comments

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.entities.CommentSortType
import io.github.mmauro94.common.client.entities.GetCommentsForm
import io.github.mmauro94.common.client.entities.GetCommentsUserPreferenceInfo
import io.github.mmauro94.common.client.entities.ListingType
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.utils.AsyncState
import io.github.mmauro94.common.utils.LocalLemmyContext
import io.github.mmauro94.common.utils.Result
import io.github.mmauro94.common.utils.comments.CommentList
import io.github.mmauro94.common.utils.comments.CommentListState
import io.github.mmauro94.common.utils.comments.CommentTree
import io.github.mmauro94.common.utils.comments.PostComments
import io.github.mmauro94.common.utils.comments.getPostComments
import io.github.mmauro94.common.utils.composeWorker
import io.github.mmauro94.common.utils.process
import kotlinx.coroutines.CoroutineScope

@Composable
fun PostComments(
    header: @Composable () -> Unit,
    postView: PostView,
) {
    val lemmyContext = LocalLemmyContext.current
    val (asyncState, workerChannel) = composeWorker<PostView, PostComments, StringResource>(
        process = { input ->
            when (
                val result = lemmyContext.client.getPostComments(
                    postView = input,
                    form = GetCommentsForm(
                        userPreferences = GetCommentsUserPreferenceInfo(
                            sort = CommentSortType.TOP,
                            type = ListingType.ALL,
                        ),
                    ),
                )
            ) {
                is Result.Success -> result
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
    val cs = rememberCoroutineScope()

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp), state = state) {
        item {
            header()
        }
        items(cs, postComments)
    }
}

private fun LazyListScope.items(cs: CoroutineScope, postComments: PostComments) {
    items(cs, postComments.commentList, 0)
}

private fun LazyListScope.items(cs: CoroutineScope, commentTree: CommentTree, depth: Int) {
    item {
        Comment(commentTree, depth)
    }
    if (!commentTree.collapsed) {
        items(cs, commentTree.children, depth + 1)
    }
}

private fun LazyListScope.items(cs: CoroutineScope, commentList: CommentList<*>, depth: Int) {
    for (comment in commentList.comments.values) {
        items(cs, comment, depth)
    }
    item {
        when (val state = commentList.state) {
            CommentListState.Finished -> {}
            CommentListState.Loading -> LoadingComments(depth)
            is CommentListState.Error -> Text("Error loading more comments") // TODO
            is CommentListState.MoreComments -> LoadMoreComments(cs, commentList, depth)
        }
    }
}
