package io.github.mmauro94.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

private enum class Screens(val icon: ImageVector) {
    HOME(Icons.Default.Home),
    INBOX(Icons.Default.Mail),
    SAVED(Icons.Default.Star),
    PROFILE(Icons.Default.Person),
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screens.HOME) }
    Column {
        Text(currentScreen.name, Modifier.weight(1f))
        NavigationBar {
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
