package io.github.mmauro94.common.destination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.StackData
import io.github.mmauro94.common.utils.LemmyContext
import io.github.mmauro94.common.utils.LocalLemmyContext

sealed class LemmyDestination(
    val lemmyContext: LemmyContext,
) : OctodonDestination {
    @Composable
    final override fun content(
        state: ItemAnimatableState,
        openDrawer: () -> Unit,
        editStack: (editor: StackData<OctodonDestination>.() -> StackData<OctodonDestination>) -> Unit,
    ) {
        CompositionLocalProvider(
            LocalLemmyContext provides lemmyContext,
        ) {
            contentWithLemmyContext(state, openDrawer, editStack)
        }
    }

    @Composable
    abstract fun contentWithLemmyContext(
        state: ItemAnimatableState,
        openDrawer: () -> Unit,
        editStack: (editor: StackData<OctodonDestination>.() -> StackData<OctodonDestination>) -> Unit,
    )
}
