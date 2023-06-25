package io.github.mmauro94.common.ui.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.GetSiteResponse
import io.github.mmauro94.common.utils.LocalDataDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddServerLoginWorkflow(
    modifier: Modifier,
    onAdded: () -> Unit,
) {
    val cs = rememberCoroutineScope()
    val db = LocalDataDb.current
    var state by remember { mutableStateOf<WorkflowState>(WorkflowState.Initial) }
    AnimatedContent(
        state,
        modifier,
        transitionSpec = {
            slideInHorizontally { it } with slideOutHorizontally { -it }
        },
    ) { s ->
        when (s) {
            WorkflowState.Initial -> ChooseServer(Modifier.fillMaxSize()) { serverUrl, serverInfo ->
                state = WorkflowState.ServerChosen(serverUrl, serverInfo)
            }

            is WorkflowState.ServerChosen -> ServerOverview(
                Modifier.fillMaxSize(),
                s.client.url,
                s.serverInfo,
                logIn = { state = WorkflowState.LogIn(s.client, s.serverInfo) },
                signIn = { state = WorkflowState.SignUp(s.client, s.serverInfo) },
                enterAnonimously = {
                    cs.launch(Dispatchers.IO) {
                        db.serverLoginQueries.insertServer(s.client.url)
                        cs.launch(Dispatchers.Main) { onAdded() }
                    }
                },
            )

            is WorkflowState.LogIn -> LogIn(
                Modifier.fillMaxSize(),
                s.client,
                s.serverInfo,
                login = { usernameOrEmail, token ->
                    cs.launch(Dispatchers.IO) {
                        db.serverLoginQueries.insertLogin(s.client.url, usernameOrEmail, token)
                        cs.launch(Dispatchers.Main) { onAdded() }
                    }
                },
            )

            is WorkflowState.SignUp -> SignUp(
                Modifier.fillMaxSize(),
                s.client,
                s.serverInfo,
            )
        }
    }
}

private sealed interface WorkflowState {
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
