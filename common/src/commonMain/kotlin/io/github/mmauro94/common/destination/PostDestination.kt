package io.github.mmauro94.common.destination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.StackData
import io.github.mmauro94.common.ui.screens.PostScreen
import io.github.mmauro94.common.utils.LemmyContext

class PostDestination(lemmyContext: LemmyContext, post: PostView) : LemmyDestination(lemmyContext) {

    var post by mutableStateOf(post)

    @Composable
    override fun contentWithLemmyContext(
        state: ItemAnimatableState,
        openDrawer: () -> Unit,
        editStack: (editor: StackData<OctodonDestination>.() -> StackData<OctodonDestination>) -> Unit,
    ) {
        PostScreen(
            destination = this,
            screenState = state,
            openDrawer = openDrawer,
            openCommunity = { community ->
                editStack {
                    push(CommunityDestination(lemmyContext, community))
                }
            },
        )
    }
}
