package io.github.mmauro94.common.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.OctodonDestination
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.SwipeToPopNestedScrollConnection
import io.github.mmauro94.common.navigation.swipeToPop
import io.github.mmauro94.common.ui.Post

@Composable
fun PostScreen(
    destination: OctodonDestination.Post,
    screenState: ItemAnimatableState,
    openDrawer: () -> Unit,
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
            Box(Modifier.verticalScroll(rememberScrollState())) {
                Post(destination.post, onClick = null, maxContentLines = Int.MAX_VALUE)
            }
        }
    }
}
