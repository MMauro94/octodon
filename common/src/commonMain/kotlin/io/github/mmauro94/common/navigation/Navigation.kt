package io.github.mmauro94.common.navigation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.SwipeableDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@Composable
fun <T : NavigationDestination> rememberStackState(
    stack: StackData<T>,
    dismiss: (T) -> Boolean,
    canSeeBehind: (T) -> Boolean = { false },
    animationSpec: AnimationSpec<Float> = tween(450),
    manualAnimationSpec: AnimationSpec<Float> = SpringSpec(stiffness = Spring.StiffnessLow),
): List<Pair<T, ItemAnimatableState>> {
    val channel = remember { Channel<List<T>>(Channel.CONFLATED) }
    SideEffect { channel.trySend(stack.stack) }
    var itemStates: Map<T, ItemAnimatableState> by remember {
        mutableStateOf(
            stack.stack.withIndex().associate { (index, it) ->
                it to ItemAnimatableState(index, manualAnimationSpec) { state ->
                    if (!state) {
                        dismiss(it)
                    } else {
                        true
                    }
                }
            },
        )
    }

    LaunchedEffect(channel) {
        var prev = stack.stack
        for (target in channel) {
            val newTarget = channel.tryReceive().getOrNull() ?: target
            val prevTarget = prev
            prev = newTarget
            if (prevTarget != newTarget) {
                val newIndex: Map<T, Int> = newTarget.withIndex().associate { it.value to it.index }

                val thingsGoingOn = mutableListOf<Job>()

                prevTarget.forEach { pt ->
                    val state = itemStates.getValue(pt)
                    if (pt !in newIndex) {
                        thingsGoingOn += launch {
                            state.transitionState.animateTo(0f, animationSpec)
                            itemStates = itemStates.minus(pt)
                        }
                    }
                }
                newTarget.forEach { nt ->
                    val ni = newIndex.getValue(nt)
                    val state = itemStates[nt] ?: ItemAnimatableState(ni, manualAnimationSpec, false) { state ->
                        if (!state) {
                            dismiss(nt)
                        } else {
                            true
                        }
                    }
                    if (nt !in itemStates) {
                        itemStates = itemStates.plus(nt to state)
                    }
                    if (state.zIndex.targetValue != ni.toFloat()) {
                        thingsGoingOn += launch {
                            state.zIndex.animateTo(ni.toFloat(), animationSpec)
                        }
                    }
                    if (state.transitionState.targetValue != 1f) {
                        thingsGoingOn += launch {
                            state.transitionState.animateTo(1f, animationSpec)
                        }
                    }
                }

                thingsGoingOn.joinAll()
            }
        }
    }
    val sortedStates = itemStates
        .entries
        .sortedBy { it.value.zIndex.value }
        .map { it.key to it.value }

    val visible = sortedStates.visible(canSeeBehind)

    if (visible.isNotEmpty()) {
        visible.forEachIndexed { index, (destination, state) ->
            val onTop = visible.subList(index + 1, visible.size)
            state.itemsOnTopCanSeeBehind = onTop.isNotEmpty() && onTop.all { canSeeBehind(it.first) }
            state.canPop = stack.bottomOrNull() != destination
            state.isOpaque = !canSeeBehind(destination)
            state.isOnTop = onTop.isEmpty()
            state.topItemsVisibility = {
                onTop.fold(0f) { acc, (_, state) ->
                    1 - (1 - acc) * (1 - state.visibility())
                }
            }
        }
    }
    return visible
}

@Composable
fun <T : NavigationDestination> StackNavigationUI(
    items: List<Pair<T, ItemAnimatableState>>,
    modifier: Modifier = Modifier,
    composable: @Composable (BoxScope.(T, state: ItemAnimatableState) -> Unit),
) {
    Box(modifier) {
        items.forEach { (destination, state) ->
            key(destination) {
                composable(destination, state)
            }
        }
    }
}

@Composable
fun <T : NavigationDestination> StackNavigation(
    stack: StackData<T>,
    dismiss: (T) -> Boolean,
    canSeeBehind: (T) -> Boolean = { false },
    animationSpec: AnimationSpec<Float> = tween(450),
    manualAnimationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    modifier: Modifier = Modifier,
    composable: @Composable (BoxScope.(T, state: ItemAnimatableState) -> Unit),
) {
    val items = rememberStackState(
        stack,
        dismiss,
        canSeeBehind,
        animationSpec,
        manualAnimationSpec,
    )
    StackNavigationUI(items, modifier, composable)
}
