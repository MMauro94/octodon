package io.github.mmauro94.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mmauro94.common.client.ApiResult.Error
import io.github.mmauro94.common.client.ApiResult.Success
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.getPosts
import io.github.mmauro94.common.client.entities.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

sealed interface FeedState {

    val nextPage: Int?

    object Finished : FeedState {
        override val nextPage = null
    }

    data class Resting(override val nextPage: Int) : FeedState

    data class Loading(override val nextPage: Int) : FeedState

    data class Error(override val nextPage: Int, val message: String) : FeedState
}

data class FeedInfo(
    val posts: List<Post>,
    val state: FeedState,
) {

    companion object {
        val DEFAULT = FeedInfo(
            posts = emptyList(),
            state = FeedState.Resting(1),
        )
    }
}

@Composable
fun Feed(
    client: LemmyClient,
    modifier: Modifier = Modifier,
    communityId: Long? = null,
) {
    val cs = rememberCoroutineScope()
    var feed by remember(client, communityId) { mutableStateOf(FeedInfo.DEFAULT) }
    val channel = remember(client, communityId) { Channel<Int>(Channel.CONFLATED) }
    LaunchedEffect(client, communityId) {
        for (page in channel) {
            feed = feed.copy(state = FeedState.Loading(nextPage = page))
            val posts = async(Dispatchers.IO) {
                client.getPosts(communityId = communityId, page = page)
            }.await()
            feed = when (posts) {
                is Error -> feed.copy(
                    state = FeedState.Error(
                        nextPage = page,
                        posts.exception.message ?: "Unknown error",
                    ),
                )

                is Success -> feed.copy(
                    posts = feed.posts + posts.result,
                    state = when {
                        posts.result.isEmpty() -> FeedState.Finished
                        else -> FeedState.Resting(nextPage = page + 1)
                    },
                )
            }
        }
    }

    LazyColumn(modifier, contentPadding = PaddingValues(vertical = 8.dp)) {
        items(feed.posts) { Post(it) }
        when (val state = feed.state) {
            is FeedState.Error -> item {
                Column {
                    Text(state.message)
                    Button(
                        onClick = { cs.launch { channel.send(state.nextPage) } },
                    ) {
                        Text("Retry")
                    }
                }
            }

            is FeedState.Loading -> item {
                CircularProgressIndicator()
            }

            is FeedState.Resting -> item {
                LaunchedEffect(Unit) {
                    channel.send(state.nextPage)
                }
            }

            FeedState.Finished -> {}
        }
    }
}
