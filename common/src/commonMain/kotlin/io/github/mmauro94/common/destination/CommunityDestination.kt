package io.github.mmauro94.common.destination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.mmauro94.common.client.entities.ListingType
import io.github.mmauro94.common.client.entities.SortType
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.StackData
import io.github.mmauro94.common.ui.FeedRequest
import io.github.mmauro94.common.ui.screens.FeedScreen
import io.github.mmauro94.common.utils.LemmyContext

class CommunityDestination(
    lemmyContext: LemmyContext,
    val communityId: Long,
) : LemmyDestination(lemmyContext) {
    val sort = mutableStateOf(SortType.HOT)
    val feedRequest get() = FeedRequest(sort.value, ListingType.ALL, communityId)

    @Composable
    override fun contentWithLemmyContext(
        state: ItemAnimatableState,
        openDrawer: () -> Unit,
        editStack: (editor: StackData<OctodonDestination>.() -> StackData<OctodonDestination>) -> Unit,
    ) {
        FeedScreen(
            feedRequest,
            state,
            onPostClick = { post ->
                editStack {
                    push(PostDestination(lemmyContext, post))
                }
            },
            openDrawer = openDrawer,
            setSort = { sort.value = it },
            openCommunity = { community ->
                if (community.id != communityId) {
                    editStack {
                        push(CommunityDestination(lemmyContext, community.id))
                    }
                }
            },
        )
    }
}
