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
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.GetSiteResponse
import io.github.mmauro94.common.client.api.LoginResponse
import io.github.mmauro94.common.client.api.login
import io.github.mmauro94.common.utils.AsyncState
import io.github.mmauro94.common.utils.Result
import io.github.mmauro94.common.utils.WorkerMessage
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
    onLoggedIn: (usernameOrEmail: String, token: String) -> Unit,
) {
    val cs = rememberCoroutineScope()

    val (asyncState, workerChannel) = composeWorker<LogInInfo, String, StringResource>(
        process = { info ->
            when (val apiResult = client.login(info.usernameOrEmail, info.password, info.totp2faToken)) {
                is Result.Error -> Result.Error(MR.strings.connection_error)
                is Result.Success -> when (val loginResponse = apiResult.result) {
                    is LoginResponse.Successful -> when {
                        loginResponse.jwt != null -> {
                            Result.Success(loginResponse.jwt)
                        }

                        else -> Result.Error(MR.strings.credentials_not_available_yet)
                    }

                    LoginResponse.CouldntFindUsernameOrEmail -> Result.Error(MR.strings.couldnt_find_username_or_email)
                    LoginResponse.PasswordIncorrect -> Result.Error(MR.strings.password_incorrect)
                }
            }
        },
        onSuccess = { loginInfo, jwt ->
            onLoggedIn(loginInfo.usernameOrEmail, jwt)
        },
    )
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
            isError = asyncState is AsyncState.Error,
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
            isError = asyncState is AsyncState.Error,
            supportingText = {
                Box(Modifier.animateContentSize()) {
                    if (asyncState is AsyncState.Error) {
                        Text(stringResource(asyncState.error))
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(
                onAny = {
                    cs.launch { workerChannel.send(WorkerMessage.Process(LogInInfo(usernameOrEmail, password, null))) }
                },
            ),
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
        )

        Spacer(Modifier.height(32.dp))

        ActionButton(
            stringResource(MR.strings.log_in),
            { LogInInfo(usernameOrEmail, password, null) },
            workerChannel,
            asyncState,
        )
    }
}
