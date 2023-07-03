package io.github.mmauro94.common.destination

import androidx.compose.runtime.Composable
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.NavigationDestination
import io.github.mmauro94.common.navigation.StackData

sealed interface OctodonDestination : NavigationDestination {

    @Composable
    fun content(
        state: ItemAnimatableState,
        openDrawer: () -> Unit,
        editStack: (editor: StackData<OctodonDestination>.() -> StackData<OctodonDestination>) -> Unit,
    )
}
