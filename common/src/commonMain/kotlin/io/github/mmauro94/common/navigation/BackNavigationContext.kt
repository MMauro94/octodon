package io.github.mmauro94.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalBackNavigationContext = staticCompositionLocalOf<BackNavigationContext?> {
    null
}
private val LocalBackNavigationHandler = staticCompositionLocalOf<BackNavigationHandler?> {
    null
}

class BackNavigationHandler(
    val parent: BackNavigationHandler?,
    val goBack: () -> Boolean,
) {
    val depth: Int = if (parent == null) 0 else 1 + parent.depth
}

@Composable
fun BackNavigationHandler(
    goBack: () -> Boolean,
    content: @Composable () -> Unit,
) {
    val currentHandler = LocalBackNavigationHandler.current
    val newHandler = remember(goBack, currentHandler) {
        BackNavigationHandler(currentHandler, goBack)
    }
    val backNavigationContext = LocalBackNavigationContext.current
    DisposableEffect(newHandler, backNavigationContext) {
        backNavigationContext?.register(newHandler)
        onDispose {
            backNavigationContext?.unregister(newHandler)
        }
    }
    CompositionLocalProvider(
        LocalBackNavigationHandler provides newHandler,
    ) {
        content()
    }
}

class BackNavigationContext {

    private val handlers = mutableListOf<BackNavigationHandler>()

    fun back(): Boolean {
        return handlers.any { it.goBack() }
    }

    fun register(handler: BackNavigationHandler) {
        handlers.add(handler)
        handlers.sortByDescending { it.depth }
    }

    fun unregister(handler: BackNavigationHandler) {
        require(handlers.remove(handler))
    }
}
