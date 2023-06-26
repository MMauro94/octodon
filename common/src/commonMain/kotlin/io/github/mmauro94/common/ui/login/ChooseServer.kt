package io.github.mmauro94.common.ui.login

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.GetSiteResponse
import io.github.mmauro94.common.client.api.getSite
import io.github.mmauro94.common.utils.WorkerMessage
import io.github.mmauro94.common.utils.WorkerState
import io.github.mmauro94.common.utils.composeWorker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseServer(
    modifier: Modifier,
    setServer: (client: LemmyClient, GetSiteResponse) -> Unit,
) {
    val cs = rememberCoroutineScope()
    var workerStateState: WorkerState<LemmyClient, ErrorMessage> by remember { mutableStateOf(WorkerState.Resting) }
    val workerChannel = composeWorker<LemmyClient, ApiResult<GetSiteResponse>>(
        process = { client ->
            client.getSite()
        },
        onStateChange = { state ->
            when (state) {
                WorkerState.Loading -> workerStateState = WorkerState.Loading
                WorkerState.Resting -> workerStateState = WorkerState.Resting
                is WorkerState.Done -> when (state.result) {
                    is ApiResult.Success -> {
                        workerStateState = WorkerState.Resting
                        setServer(state.input, state.result.result)
                    }
                    is ApiResult.Error -> workerStateState = WorkerState.Done(state.input, state.result.exception.message.orEmpty())
                }
            }
        },
    )
    val workerState = workerStateState
    var serverUrl by remember { mutableStateOf("") }
    val client = remember(serverUrl) {
        if (serverUrl.contains("://")) {
            LemmyClient(serverUrl)
        } else {
            LemmyClient("https://$serverUrl")
        }
    }

    LoginStepContainer(
        modifier,
        Icons.Default.AddHome,
        stepTitle = stringResource(MR.strings.choose_lemmy_server),
        stepDescription = stringResource(MR.strings.octodon_description_brief),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = serverUrl,
            onValueChange = {
                serverUrl = it
                cs.launch { workerChannel.send(WorkerMessage.Stop) }
            },
            leadingIcon = { Icon(Icons.Default.Dns, null) },
            label = { Text(stringResource(MR.strings.server_url)) },
            placeholder = { Text("https://lemmy.example.com") },
            singleLine = true,
            isError = workerState is WorkerState.Done,
            supportingText = {
                Box(Modifier.animateContentSize()) {
                    if (workerState is WorkerState.Done) {
                        Text(workerState.result)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Uri),
            keyboardActions = KeyboardActions(
                onAny = {
                    cs.launch { workerChannel.send(WorkerMessage.Process(client)) }
                },
            ),
        )

        Spacer(Modifier.height(32.dp))

        ActionButton(
            stringResource(MR.strings.next),
            { client },
            workerChannel,
            workerState,
        )
    }
}
