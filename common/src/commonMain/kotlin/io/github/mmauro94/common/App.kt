package io.github.mmauro94.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.LocalImageLoader
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.ui.AppTheme
import io.github.mmauro94.common.ui.Feed
import io.github.mmauro94.common.ui.FeedRequest
import io.github.mmauro94.common.ui.components.SortMenuButton
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme(
        colorScheme = appColorScheme(),
        shapes = AppTheme.shapes,
    ) {
        PlatformStyle {
            CompositionLocalProvider(LocalImageLoader provides generateImageLoader()) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContent() {
    val cs = rememberCoroutineScope()
    val client = remember { LemmyClient("https://lemmy.ml/") }
    var feedRequest by remember(client) { mutableStateOf(FeedRequest.default(client)) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                Divider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /*TODO*/ },
                )
            }
        },
    ) {
        Column {
            TopAppBar(
                title = { Text("Octodon") },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton({ cs.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, "open menu")
                    }
                },
                actions = {
                    SortMenuButton(onSortSelected = { feedRequest = feedRequest.copy(sort = it) })
                },
            )
            Feed(feedRequest = feedRequest, modifier = Modifier.weight(1f).fillMaxWidth())
        }
    }
}
