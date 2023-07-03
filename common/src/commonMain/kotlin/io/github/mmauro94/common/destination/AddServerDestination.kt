package io.github.mmauro94.common.destination

import androidx.compose.runtime.Composable
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.StackData
import io.github.mmauro94.common.ui.screens.AddServerLoginScreen

object AddServerDestination : OctodonDestination {
    @Composable
    override fun content(
        state: ItemAnimatableState,
        openDrawer: () -> Unit,
        editStack: (editor: StackData<OctodonDestination>.() -> StackData<OctodonDestination>) -> Unit,
    ) {
        AddServerLoginScreen(
            state,
            openDrawer = openDrawer,
        )
    }
}
