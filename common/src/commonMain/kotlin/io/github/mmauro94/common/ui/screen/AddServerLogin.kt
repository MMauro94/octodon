package io.github.mmauro94.common.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.getSite
import io.github.mmauro94.common.utils.LocalDataDb
import io.github.mmauro94.octodon.common.db.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServerLogin(
    modifier: Modifier = Modifier,
    onAdded: () -> Unit,
) {
    val cs = rememberCoroutineScope()
    val db = LocalDataDb.current
    var serverUrl by remember { mutableStateOf("") }
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isAnonymous by remember { mutableStateOf(false) }

    Box(modifier.padding(32.dp), contentAlignment = Alignment.Center) {
        Column(Modifier.widthIn(max = 400.dp)) {
            Spacer(Modifier.weight(1f))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = serverUrl,
                onValueChange = { serverUrl = it },
                leadingIcon = { Icon(Icons.Default.Dns, null) },
                label = { Text(stringResource(MR.strings.server_url)) },
                placeholder = { Text("https://lemmy.example.com") },
                trailingIcon = { IconButton({}) { Icon(Icons.Default.Help, stringResource(MR.strings.help)) } },
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isAnonymous,
                    onCheckedChange = { isAnonymous = it },
                )
                Text(stringResource(MR.strings.anonymous_login))
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAnonymous,
                value = usernameOrEmail,
                onValueChange = { usernameOrEmail = it },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                label = { Text(stringResource(MR.strings.username_or_email)) },
                placeholder = { Text("") },
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAnonymous,
                value = password,
                onValueChange = { password = it },
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
            )
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (isAnonymous) {
                        cs.addServer(db, serverUrl, onAdded)
                    }
                },
            ) {
                if (isAnonymous) {
                    Text(stringResource(MR.strings.add_server))
                } else {
                    Text(stringResource(MR.strings.log_in_action))
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

private fun CoroutineScope.addServer(db: Data, serverUrl: String, onAdded: () -> Unit) {
    launch(Dispatchers.IO) {
        val site = LemmyClient(url = serverUrl).getSite()
        if (site is ApiResult.Success) {
            db.serverLoginQueries.insertServer(serverUrl)
            launch(Dispatchers.Main) {
                onAdded()
            }
        } else {
            // TODO handle errors
            println(site)
        }
    }
}
