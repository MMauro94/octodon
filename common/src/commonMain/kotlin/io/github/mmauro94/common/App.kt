package io.github.mmauro94.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.mmauro94.common.Screens.HOME
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.ui.Feed

private enum class Screens(val icon: ImageVector) {
    HOME(Icons.Default.Home),
    INBOX(Icons.Default.Mail),
    SAVED(Icons.Default.Star),
    PROFILE(Icons.Default.Person),
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(HOME) }
    val client = remember { LemmyClient("https://lemmy.ml/") }
    MaterialTheme(
        colorScheme = darkColorScheme(),
    ) {
        PlatformStyle {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column {
                    when (currentScreen) {
                        HOME -> Feed(client = client, modifier = Modifier.weight(1f).fillMaxWidth())
                        else -> Spacer(Modifier.weight(1f))
                    }

                    NavigationBar(tonalElevation = 8.dp) {
                        Screens.values().forEach { screen ->
                            NavigationBarItem(
                                selected = screen == currentScreen,
                                icon = { Icon(screen.icon, null) },
                                label = { Text(screen.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    currentScreen = screen
                                },
                                alwaysShowLabel = false,
                            )
                        }
                    }
                }
            }
        }
    }
}
