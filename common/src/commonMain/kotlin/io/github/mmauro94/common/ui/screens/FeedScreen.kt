package io.github.mmauro94.common.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.destination.FeedDestination
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.ui.Feed
import io.github.mmauro94.common.ui.components.SortMenuButton

@Composable
fun FeedScreen(
    destination: FeedDestination,
    screenState: ItemAnimatableState,
    onPostClick: (PostView) -> Unit,
    openDrawer: () -> Unit,
) {
    ScreenContainer(screenState) { width, height ->
        Column(Modifier.fillMaxSize()) {
            SwipeableTopAppBar(
                screenState,
                openDrawer,
                width,
                height,
                title = { Text("Octodon") },
                actions = {
                    SortMenuButton(onSortSelected = { destination.sort.value = it })
                },
            )
            Feed(
                feedRequest = destination.feedRequest,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                onPostClick = onPostClick,
            )
        }
    }
}
