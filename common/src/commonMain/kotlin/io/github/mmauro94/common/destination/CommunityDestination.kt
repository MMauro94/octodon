package io.github.mmauro94.common.destination

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mmauro94.common.client.entities.Community
import io.github.mmauro94.common.client.entities.ListingType
import io.github.mmauro94.common.client.entities.SortType
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.StackData
import io.github.mmauro94.common.ui.components.CommunityHeader
import io.github.mmauro94.common.ui.screens.FeedScreen
import io.github.mmauro94.common.utils.DownloadableFeed
import io.github.mmauro94.common.utils.FeedRequest
import io.github.mmauro94.common.utils.LemmyContext

class CommunityDestination(
    lemmyContext: LemmyContext,
    community: Community,
) : LemmyDestination(lemmyContext) {
    val sort = mutableStateOf(SortType.HOT)
    val community by mutableStateOf(community)
    private val feedRequest get() = FeedRequest(sort.value, ListingType.ALL, community.id)
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
            feedHeader = {
                item {
                    CommunityHeader(community)
                }
                item { Spacer(Modifier.height(12.dp)) }
            },
            onPostClick = { post ->
                editStack {
                    push(PostDestination(lemmyContext, post))
                }
            },
            openDrawer = openDrawer,
            setSort = { sort.value = it },
        ) { community ->
            if (community != this.community) {
                editStack {
                    push(CommunityDestination(lemmyContext, community))
                }
            }
        }
    }
}
