package io.github.mmauro94.common.utils

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.mmauro94.octodon.common.db.Data

val LocalDataDb = staticCompositionLocalOf<Data> {
    error("LocalDataDb must be explicitly initialized")
}
