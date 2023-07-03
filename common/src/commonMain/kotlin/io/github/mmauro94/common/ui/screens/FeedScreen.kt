package io.github.mmauro94.common.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.mmauro94.common.client.entities.Community
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.client.entities.SortType
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.swipeToPop
import io.github.mmauro94.common.ui.Feed
import io.github.mmauro94.common.ui.FeedRequest
import io.github.mmauro94.common.ui.components.SortMenuButton

@Composable
fun FeedScreen(
    feedRequest: FeedRequest,
    screenState: ItemAnimatableState,
    onPostClick: (PostView) -> Unit,
    openDrawer: () -> Unit,
    setSort: (SortType) -> Unit,
    openCommunity: (Community) -> Unit,
) {
    ScreenContainer(screenState) { width, height ->
        Column(Modifier.fillMaxSize().swipeToPop(screenState, width, height)) {
            SwipeableTopAppBar(
                screenState,
                openDrawer,
                width,
                height,
                title = { Text("Octodon") },
                actions = {
                    SortMenuButton(onSortSelected = setSort)
                },
            )
            Feed(
                feedRequest = feedRequest,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                onPostClick = onPostClick,
                openCommunity = openCommunity,
            )
        }
    }
}
