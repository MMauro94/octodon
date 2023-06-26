package io.github.mmauro94.common.ui.login

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.GetSiteResponse
import io.github.mmauro94.common.client.api.LoginResponse
import io.github.mmauro94.common.client.api.login
import io.github.mmauro94.common.utils.WorkerMessage
import io.github.mmauro94.common.utils.WorkerState
import io.github.mmauro94.common.utils.composeWorker
import kotlinx.coroutines.launch

private data class LogInInfo(
    val usernameOrEmail: String,
    val password: String,
    val totp2faToken: String?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogIn(
    modifier: Modifier,
    client: LemmyClient,
    serverInfo: GetSiteResponse,
    login: (usernameOrEmail: String, token: String) -> Unit,
) {
    val credentialsNotAvailableYetStr = stringResource(MR.strings.credentials_not_available_yet)
    val cs = rememberCoroutineScope()
    var workerStateState: WorkerState<LogInInfo, ErrorMessage> by remember { mutableStateOf(WorkerState.Resting) }
    val workerChannel = composeWorker<LogInInfo, ApiResult<LoginResponse>>(
        process = { info ->
            client.login(info.usernameOrEmail, info.password, info.totp2faToken)
        },
        onStateChange = { state ->
            when (state) {
                WorkerState.Loading -> workerStateState = WorkerState.Loading
                WorkerState.Resting -> workerStateState = WorkerState.Resting
                is WorkerState.Done -> when (state.result) {
                    is ApiResult.Error -> workerStateState = WorkerState.Done(state.input, state.result.exception.message.orEmpty())
                    is ApiResult.Success -> if (state.result.result.jwt != null) {
                        workerStateState = WorkerState.Resting
                        login(state.input.usernameOrEmail, state.result.result.jwt)
                    } else {
                        workerStateState = WorkerState.Done(state.input, credentialsNotAvailableYetStr)
                    }
                }
            }
        },
    )
    val workerState = workerStateState
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    LoginStepContainer(
        modifier,
        Icons.Default.AccountCircle,
        stepTitle = stringResource(MR.strings.log_in_on_x, serverInfo.siteView.site.name),
        stepSubtitle = client.url,
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = usernameOrEmail,
            onValueChange = {
                usernameOrEmail = it
                cs.launch { workerChannel.send(WorkerMessage.Stop) }
            },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            label = { Text(stringResource(MR.strings.username_or_email)) },
            singleLine = true,
            isError = workerState is WorkerState.Done,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = {
                password = it
                cs.launch { workerChannel.send(WorkerMessage.Stop) }
            },
            leadingIcon = { Icon(Icons.Default.Password, null) },
            label = { Text(stringResource(MR.strings.password)) },
            placeholder = { Text("") },
            visualTransformation = when (showPassword) {
                true -> VisualTransformation.None
                false -> PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton({ showPassword = !showPassword }) {
                    if (showPassword) {
                        Icon(Icons.Default.VisibilityOff, stringResource(MR.strings.hide_password))
                    } else {
                        Icon(Icons.Default.Visibility, stringResource(MR.strings.show_password))
                    }
                }
            },
            isError = workerState is WorkerState.Done,
            supportingText = {
                Box(Modifier.animateContentSize()) {
                    if (workerState is WorkerState.Done) {
                        Text(workerState.result)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(
                onAny = {
                    cs.launch { workerChannel.send(WorkerMessage.Process(usernameOrEmail)) }
                },
            ),
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
        )

        Spacer(Modifier.height(32.dp))

        ActionButton(
            stringResource(MR.strings.log_in),
            { LogInInfo(usernameOrEmail, password, null) },
            workerChannel,
            workerState,
        )
    }
}
