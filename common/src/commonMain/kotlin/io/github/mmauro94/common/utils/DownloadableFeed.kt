package io.github.mmauro94.common.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.getPosts
import io.github.mmauro94.common.client.entities.ListingType
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.client.entities.SortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope

class DownloadableFeed(
    val lemmyContext: LemmyContext,
    val feedRequest: FeedRequest,
) {
    private var nextPage = 1
    var feed by mutableStateOf(FeedInfo.DEFAULT)
        private set
    private val channel = Channel<Unit>(Channel.CONFLATED)

    suspend fun downloadNextPage() {
        channel.send(Unit)
    }

    suspend fun start() = coroutineScope {
        for (message in channel) {
            feed = feed.copy(state = AsyncState.Loading)
            val posts = async(Dispatchers.IO) {
                feedRequest.getPosts(lemmyContext.client, nextPage)
            }.await()
            feed = when (posts) {
                is Result.Error -> feed.copy(
                    state = AsyncState.Error(
                        // TODO: translate
                        posts.error.message ?: "Unknown error",
                    ),
                )

                is Result.Success -> {
                    nextPage += 1
                    feed.copy(
                        postViewsMap = feed.postViewsMap + posts.result.associateBy { it.post.id },
                        state = when {
                            posts.result.isEmpty() -> AsyncState.Success(Unit)
                            else -> AsyncState.Resting
                        },
                    )
                }
            }
        }
    }

    fun updatePost(post: PostView) {
        val id = post.post.id
        if (id in feed.postViewsMap) {
            feed = feed.copy(
                postViewsMap = feed.postViewsMap.toMutableMap().apply {
                    this[id] = post
                },
            )
        } else {
            error("Post with id $id not in feed")
        }
    }
}

data class FeedInfo(
    val postViewsMap: Map<Long, PostView>,
    val state: AsyncState<Unit, String>,
) {

    val postViews = postViewsMap.values.toList()

    companion object {
        val DEFAULT = FeedInfo(
            postViewsMap = emptyMap(),
            state = AsyncState.Resting,
        )
    }
}

data class FeedRequest(
    val sort: SortType,
    val type: ListingType,
    val communityId: Long? = null,
) {

    suspend fun getPosts(client: LemmyClient, page: Int): ApiResult<List<PostView>> {
        return client.getPosts(
            page = page,
            communityId = communityId,
            sort = sort,
            type = type,
        )
    }

    companion object {
        fun default(): FeedRequest {
            return FeedRequest(
                sort = SortType.HOT,
                type = ListingType.ALL,
            )
        }
    }
}
