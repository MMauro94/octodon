package io.github.mmauro94.common.platform

import androidx.compose.runtime.Composable
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mmauro94.common.APP_DIR_NAME
import io.github.mmauro94.octodon.common.db.Data
import net.harawata.appdirs.AppDirsFactory
import java.io.File

@Composable
actual fun createSqlDriver(): SqlDriver {
    val db = File(
        AppDirsFactory.getInstance().getUserDataDir(APP_DIR_NAME, null, null, true),
        "data.sqlite",
    )
    db.parentFile.mkdirs()
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$db")
    if (!db.exists()) {
        Data.Schema.create(driver)
    }
    return driver
}
