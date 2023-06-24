package io.github.mmauro94.common.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.mmauro94.octodon.common.db.Data

@Composable
actual fun createSqlDriver(): SqlDriver {
    return AndroidSqliteDriver(Data.Schema, LocalContext.current, "data.db")
}
