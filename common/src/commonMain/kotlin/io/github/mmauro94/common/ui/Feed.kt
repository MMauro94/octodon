package io.github.mmauro94.common.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.mmauro94.common.client.ApiResult.Error
import io.github.mmauro94.common.client.ApiResult.Success
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.GetPostsItem
import io.github.mmauro94.common.client.api.getPosts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

sealed interface FeedState {

    object Finished : FeedState

    object Resting : FeedState

    object Loading : FeedState

    data class Error(val message: String) : FeedState
}

data class FeedInfo(
    val posts: List<GetPostsItem>,
    val state: FeedState,
) {

    companion object {
        val DEFAULT = FeedInfo(
            posts = emptyList(),
            state = FeedState.Loading,
        )
    }
}

@Composable
fun Feed(
    client: LemmyClient,
    modifier: Modifier = Modifier,
    communityId: Long? = null,
) {
    var feed by remember { mutableStateOf(FeedInfo.DEFAULT) }
    LaunchedEffect(client, communityId) {
        val posts = async(Dispatchers.IO) {
            client.getPosts(communityId = communityId)
        }.await()
        feed = when (posts) {
            is Error -> feed.copy(state = FeedState.Error(posts.exception.message ?: "Unknown error"))
            is Success -> feed.copy(
                posts = posts.result,
                state = FeedState.Resting,
            )
        }
    }

    LazyColumn(modifier) {
        items(feed.posts, key = { it.post.id }) { Post(it) }
        when (val state = feed.state) {
            is FeedState.Error -> item {
                Text(state.message)
            }

            FeedState.Loading -> item {
                CircularProgressIndicator()
            }

            FeedState.Resting, FeedState.Finished -> {}
        }
    }
}
