package io.github.mmauro94.common.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.GetSiteResponse
import io.github.mmauro94.common.navigation.ItemAnimatableState
import io.github.mmauro94.common.navigation.NavigationDestination
import io.github.mmauro94.common.navigation.StackData
import io.github.mmauro94.common.navigation.StackNavigation
import io.github.mmauro94.common.navigation.swipeToPop
import io.github.mmauro94.common.ui.login.ChooseServer
import io.github.mmauro94.common.ui.login.LogIn
import io.github.mmauro94.common.ui.login.ServerOverview
import io.github.mmauro94.common.ui.login.SignUp
import io.github.mmauro94.common.utils.LocalDataDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AddServerLoginScreen(
    screenState: ItemAnimatableState,
    openDrawer: () -> Unit,
) {
    val cs = rememberCoroutineScope()
    val db = LocalDataDb.current
    var stackData by remember {
        mutableStateOf(StackData.of<WorkflowState>(WorkflowState.Initial))
    }

    ScreenContainer(screenState) { width, height ->
        Column {
            SwipeableTopAppBar(
                screenState,
                openDrawer,
                width,
                height,
                title = { Text("Add server connection") },
            )
            StackNavigation(
                stackData,
                dismiss = {
                    if (stackData.canPop(it)) {
                        stackData = stackData.pop(it)
                        true
                    } else {
                        screenState.tryPop()
                    }
                },
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) { location, innerScreenState ->
                ScreenContainer(innerScreenState, animation = Animation.SLIDE) { innerWidth, innerHeight ->
                    val swipeableState = if (innerScreenState.canPop) innerScreenState else screenState
                    val innerScreenModifier = Modifier.fillMaxSize().swipeToPop(swipeableState, innerWidth, innerHeight)
                    when (location) {
                        WorkflowState.Initial -> ChooseServer(innerScreenModifier) { serverUrl, serverInfo ->
                            stackData = stackData.push(WorkflowState.ServerChosen(serverUrl, serverInfo))
                        }

                        is WorkflowState.ServerChosen -> ServerOverview(
                            innerScreenModifier,
                            location.client.url,
                            location.serverInfo,
                            logIn = { stackData = stackData.push(WorkflowState.LogIn(location.client, location.serverInfo)) },
                            signIn = { stackData = stackData.push(WorkflowState.SignUp(location.client, location.serverInfo)) },
                            enterAnonimously = {
                                cs.launch(Dispatchers.IO) {
                                    db.serverLoginQueries.insertServer(location.client.url)
                                    cs.launch(Dispatchers.Main) { screenState.tryPop() }
                                }
                            },
                        )

                        is WorkflowState.LogIn -> LogIn(
                            innerScreenModifier,
                            location.client,
                            location.serverInfo,
                            login = { usernameOrEmail, token ->
                                cs.launch(Dispatchers.IO) {
                                    db.serverLoginQueries.insertLogin(location.client.url, usernameOrEmail, token)
                                    cs.launch(Dispatchers.Main) { screenState.tryPop() }
                                }
                            },
                        )

                        is WorkflowState.SignUp -> SignUp(
                            innerScreenModifier,
                            location.client,
                            location.serverInfo,
                        )
                    }
                }
            }
        }
    }
}

private sealed interface WorkflowState : NavigationDestination {
    object Initial : WorkflowState
    data class ServerChosen(
        val client: LemmyClient,
        val serverInfo: GetSiteResponse,
    ) : WorkflowState

    data class LogIn(
        val client: LemmyClient,
        val serverInfo: GetSiteResponse,
    ) : WorkflowState

    data class SignUp(
        val client: LemmyClient,
        val serverInfo: GetSiteResponse,
    ) : WorkflowState
}
