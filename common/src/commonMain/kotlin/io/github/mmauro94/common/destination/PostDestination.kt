package io.github.mmauro94.common.destination

import androidx.compose.runtime.Composable
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.StackData
import io.github.mmauro94.common.ui.screens.PostScreen
import io.github.mmauro94.common.utils.LemmyContext

class PostDestination(lemmyContext: LemmyContext, val post: PostView) : LemmyDestination(lemmyContext) {

    @Composable
    override fun contentWithLemmyContext(
        state: ItemAnimatableState,
        openDrawer: () -> Unit,
        editStack: (editor: StackData<OctodonDestination>.() -> StackData<OctodonDestination>) -> Unit,
    ) {
        PostScreen(
            this,
            state,
            openDrawer = openDrawer,
        )
    }
}
