package io.github.mmauro94.common.platform

import androidx.compose.runtime.Composable
import app.cash.sqldelight.db.SqlDriver

@Composable
expect fun createSqlDriver(): SqlDriver
