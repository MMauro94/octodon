package io.github.mmauro94.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.seiko.imageloader.LocalImageLoader
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.entities.ListingType
import io.github.mmauro94.common.platform.createSqlDriver
import io.github.mmauro94.common.ui.Feed
import io.github.mmauro94.common.ui.FeedRequest
import io.github.mmauro94.common.ui.components.SortMenuButton
import io.github.mmauro94.common.ui.screen.AddServerLogin
import io.github.mmauro94.common.utils.LocalDataDb
import io.github.mmauro94.octodon.common.db.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme(
        colorScheme = appColorScheme(),
//        shapes = AppTheme.shapes,
    ) {
        PlatformStyle {
            CompositionLocalProvider(
                LocalImageLoader provides generateImageLoader(),
                LocalDataDb provides Data(createSqlDriver()),
            ) {
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
    val db = LocalDataDb.current
    val client = remember { LemmyClient("https://lemmy.ml/") }
    var feedRequest by remember(client) { mutableStateOf(FeedRequest.default(client)) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var addAccount by remember { mutableStateOf(false) }

    val usersState by remember(db) {
        db.serverLoginQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
    }.collectAsState(null)

    val users = usersState
    when {
        users == null -> {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        users.isEmpty() || addAccount -> {
            AddServerLogin(Modifier.fillMaxSize(), onAdded = { addAccount = false })
        }

        else -> {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        NavigationDrawerItem(
                            label = { Text("Add server login") }, // TODO
                            selected = false,
                            onClick = { addAccount = true },
                            icon = { Icon(Icons.Default.Add, null) },
                        )
                        Divider()
                        ListingType.values().forEach { listingType ->
                            NavigationDrawerItem(
                                label = { Text(listingType.label.str()) },
                                selected = feedRequest.type == listingType,
                                onClick = { feedRequest = feedRequest.copy(type = listingType, communityId = null) },
                                icon = { Icon(listingType.icon, null) },
                            )
                        }
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
    }
}
