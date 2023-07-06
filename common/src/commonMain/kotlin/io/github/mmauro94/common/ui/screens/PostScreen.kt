package io.github.mmauro94.common.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.entities.Community
import io.github.mmauro94.common.destination.PostDestination
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.SwipeToPopNestedScrollConnection
import io.github.mmauro94.common.navigation.swipeToPop
import io.github.mmauro94.common.ui.Post
import io.github.mmauro94.common.ui.components.PostComments

@Composable
fun PostScreen(
    destination: PostDestination,
    screenState: ItemAnimatableState,
    openDrawer: () -> Unit,
    openCommunity: (Community) -> Unit,
) {
    ScreenContainer(screenState) { width, height ->
        val nestedScrollConnection = remember(screenState) {
            SwipeToPopNestedScrollConnection(screenState)
        }

        Column(
            Modifier
                .swipeToPop(screenState, width, height, vertical = true)
                .nestedScroll(nestedScrollConnection),
        ) {
            SwipeableTopAppBar(
                screenState,
                openDrawer,
                width,
                height,
                title = { Text(stringResource(MR.strings.comments)) },
            )
            PostComments(
                postView = destination.post,
                header = {
                    Post(
                        destination.post,
                        onClick = null,
                        onUpdatePost = { destination.post = it },
                        openCommunity = openCommunity,
                        maxBodyHeight = null,
                        enableBodyClicks = true,
                    )
                },
            )
        }
    }
}
