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
import io.github.mmauro94.common.client.entities.SortType
import io.github.mmauro94.common.destination.AddServerDestination
import io.github.mmauro94.common.destination.FeedDestination
import io.github.mmauro94.common.destination.OctodonDestination
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.StackData
import io.github.mmauro94.common.navigation.StackNavigation
import io.github.mmauro94.common.platform.createSqlDriver
import io.github.mmauro94.common.utils.LemmyContext
import io.github.mmauro94.common.utils.LocalDataDb
import io.github.mmauro94.octodon.common.db.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
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
    val mainFeedSort = remember { mutableStateOf(SortType.HOT) }
    val usersState by remember(db) {
        db.serverLoginQueries.selectAll().asFlow()
            .mapToList(Dispatchers.IO)
            .map { logins ->
                logins.map { login -> LemmyContext(login) }
            }
    }.collectAsState(null)
    val users = usersState

    val drawerState = rememberDrawerState(DrawerValue.Closed)

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
            mutableStateOf(StackData.of<OctodonDestination>(AddServerDestination))
        }
        remember(user) {
            stackData = StackData.of(
                if (user != null) {
                    FeedDestination(user, ListingType.LOCAL, mainFeedSort)
                } else {
                    AddServerDestination
                },
            )
        }
        val mainFeed = stackData.stack.firstOrNull { it is FeedDestination } as FeedDestination?

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    users.forEach { u ->
                        NavigationDrawerItem(
                            label = {
                                Column {
                                    Text(u.serverLogin.username ?: stringResource(MR.strings.anonymous))
                                    Text(u.serverLogin.serverUrl, style = MaterialTheme.typography.labelSmall)
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
                            stackData = stackData.push(AddServerDestination)
                        },
                        icon = { Icon(Icons.Default.Add, null) },
                    )
                    if (user != null) {
                        Divider()
                        ListingType.values().forEach { listingType ->
                            NavigationDrawerItem(
                                label = { Text(listingType.label.str()) },
                                selected = mainFeed?.feedRequest?.type == listingType,
                                onClick = {
                                    cs.launch { drawerState.close() }
                                    val feedDestination = FeedDestination(user, listingType, mainFeedSort)
                                    stackData = if (mainFeed != null) {
                                        stackData
                                            .popUntil(mainFeed)
                                            .replace(mainFeed, feedDestination)
                                    } else {
                                        stackData.push(feedDestination)
                                    }
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
            ) { location, state: ItemAnimatableState ->
                location.content(
                    state = state,
                    openDrawer = { cs.launch { drawerState.open() } },
                    editStack = { editor -> stackData = editor(stackData) },
                )
            }
        }
    }
}
