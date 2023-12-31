package io.github.mmauro94.common.destination

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import io.github.mmauro94.common.client.entities.ListingType
import io.github.mmauro94.common.client.entities.SortType
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.StackData
import io.github.mmauro94.common.ui.screens.FeedScreen
import io.github.mmauro94.common.utils.DownloadableFeed
import io.github.mmauro94.common.utils.FeedRequest
import io.github.mmauro94.common.utils.LemmyContext

class MainFeedDestination(
    lemmyContext: LemmyContext,
    val type: ListingType,
    val sort: MutableState<SortType>,
    val communityId: Long? = null,
) : LemmyDestination(lemmyContext) {
    val feedRequest get() = FeedRequest(sort.value, type, communityId)
    private var downloadableFeed = DownloadableFeed(lemmyContext, feedRequest)
    private val feedListState = LazyListState()

    @Composable
    override fun contentWithLemmyContext(
        state: ItemAnimatableState,
        openDrawer: () -> Unit,
        editStack: (editor: StackData<OctodonDestination>.() -> StackData<OctodonDestination>) -> Unit,
    ) {
        if (feedRequest != downloadableFeed.feedRequest) {
            downloadableFeed = DownloadableFeed(lemmyContext, feedRequest)
        }
        LaunchedEffect(downloadableFeed) {
            downloadableFeed.start()
        }
        FeedScreen(
            state,
            downloadableFeed = downloadableFeed,
            feedListState = feedListState,
            onPostClick = { post ->
                editStack {
                    push(PostDestination(lemmyContext, post))
                }
            },
            openDrawer = openDrawer,
            setSort = { sort.value = it },
        ) { community ->
            editStack {
                push(CommunityDestination(lemmyContext, community))
            }
        }
    }
}
