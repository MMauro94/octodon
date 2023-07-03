package io.github.mmauro94.common.utils

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.octodon.common.db.ServerLogin

class LemmyContext(
    val serverLogin: ServerLogin,
) {
    val client: LemmyClient = LemmyClient(serverLogin.serverUrl)
}

val LocalLemmyContext = staticCompositionLocalOf<LemmyContext> {
    error("LocalLemmyContext must be explicitly initialized")
}
