package io.github.mmauro94.common.ui.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.slideAnimation
import io.github.mmauro94.common.navigation.stackAnimation
import io.github.mmauro94.common.navigation.swipeToPop

@Composable
fun ScreenContainer(
    screenState: ItemAnimatableState,
    animation: Animation = Animation.STACK,
    content: @Composable (w: Dp, h: Dp) -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight
        val modifier = when (animation) {
            Animation.STACK -> Modifier.stackAnimation(screenState, w, h)
            Animation.SLIDE -> Modifier.slideAnimation(screenState, w)
        }
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            content(w, h)
        }
    }
}

enum class Animation {
    STACK, SLIDE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTopAppBar(
    screenState: ItemAnimatableState,
    openDrawer: () -> Unit,
    width: Dp,
    height: Dp,
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth().swipeToPop(
            screenState,
            width,
            height,
            horizontal = screenState.canPop,
            vertical = false,
        ),
        navigationIcon = {
            NavigationButton(screenState, openDrawer)
        },
        title = title,
        actions = actions,
    )
}

@Composable
fun NavigationButton(
    screenState: ItemAnimatableState,
    openDrawer: () -> Unit,
) {
    if (screenState.canPop) {
        IconButton({ screenState.tryPop() }) {
            Icon(Icons.Default.ArrowBack, stringResource(MR.strings.back_action))
        }
    } else {
        IconButton(openDrawer) {
            Icon(Icons.Default.Menu, stringResource(MR.strings.open_menu))
        }
    }
}
