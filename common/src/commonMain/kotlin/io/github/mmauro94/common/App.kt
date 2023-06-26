package io.github.mmauro94.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.client.entities.ListingType
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.navigation.NavigationDestination
import io.github.mmauro94.common.navigation.StackData
import io.github.mmauro94.common.navigation.StackNavigation
import io.github.mmauro94.common.platform.createSqlDriver
import io.github.mmauro94.common.ui.FeedRequest
import io.github.mmauro94.common.ui.screens.AddServerLoginScreen
import io.github.mmauro94.common.ui.screens.FeedScreen
import io.github.mmauro94.common.ui.screens.PostScreen
import io.github.mmauro94.common.utils.LocalDataDb
import io.github.mmauro94.octodon.common.db.Data
import io.github.mmauro94.octodon.common.db.ServerLogin
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

sealed interface OctodonDestination : NavigationDestination {
    class Feed(val serverLogin: ServerLogin) : OctodonDestination {
        var feedRequest by mutableStateOf(FeedRequest.default())
    }

    class Post(val serverLogin: ServerLogin, val post: PostView) : OctodonDestination

    object AddServer : OctodonDestination
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContent() {
    val cs = rememberCoroutineScope()
    val db = LocalDataDb.current
    val usersState by remember(db) {
        db.serverLoginQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
    }.collectAsState(null)
    val users = usersState

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val openDrawerLambda: () -> Unit = { cs.launch { drawerState.open() } }

    if (users == null) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    } else {
        var userState by remember { mutableStateOf(users.firstOrNull()) }
        if (userState !in users) {
            userState = users.firstOrNull()
        }
        val user = userState
        var stackData by remember {
            mutableStateOf(StackData.of<OctodonDestination>(OctodonDestination.AddServer))
        }
        remember(user) {
            stackData = StackData.of(
                if (user != null) {
                    OctodonDestination.Feed(user)
                } else {
                    OctodonDestination.AddServer
                },
            )
        }
        val main = stackData.stack.firstOrNull { it is OctodonDestination.Feed } as OctodonDestination.Feed?

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    users.forEach { u ->
                        NavigationDrawerItem(
                            label = {
                                Column {
                                    Text(u.username ?: stringResource(MR.strings.anonymous))
                                    Text(u.serverUrl, style = MaterialTheme.typography.labelSmall)
                                }
                            },
                            selected = u == user,
                            onClick = {
                                cs.launch { drawerState.close() }
                                userState = u
                            },
                            icon = { Icon(Icons.Default.Person, null) },
                        )
                    }
                    NavigationDrawerItem(
                        label = { Text(stringResource(MR.strings.add_server_or_account)) },
                        selected = false,
                        onClick = {
                            cs.launch { drawerState.close() }
                            stackData = stackData.push(OctodonDestination.AddServer)
                        },
                        icon = { Icon(Icons.Default.Add, null) },
                    )
                    if (main != null) {
                        Divider()
                        ListingType.values().forEach { listingType ->
                            NavigationDrawerItem(
                                label = { Text(listingType.label.str()) },
                                selected = main.feedRequest.type == listingType,
                                onClick = {
                                    cs.launch { drawerState.close() }
                                    stackData = stackData.popUntil(main)
                                    main.feedRequest = main.feedRequest.copy(type = listingType, communityId = null)
                                },
                                icon = { Icon(listingType.icon, null) },
                            )
                        }
                    }
                }
            },
        ) {
            StackNavigation(
                stack = stackData,
                dismiss = {
                    if (stackData.canPop(it)) {
                        stackData = stackData.pop(it)
                        true
                    } else {
                        false
                    }
                },
            ) { location, state ->
                when (location) {
                    is OctodonDestination.AddServer -> AddServerLoginScreen(
                        state,
                        openDrawer = openDrawerLambda,
                    )

                    is OctodonDestination.Feed -> FeedScreen(
                        location,
                        state,
                        onPostClick = { post ->
                            stackData = stackData.push(OctodonDestination.Post(location.serverLogin, post))
                        },
                        openDrawer = openDrawerLambda,
                    )

                    is OctodonDestination.Post -> PostScreen(
                        location,
                        state,
                        openDrawer = openDrawerLambda,
                    )
                }
            }
        }
    }
}
