package io.github.mmauro94.common.ui.login

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.GetSiteResponse

@Composable
fun SignUp(
    modifier: Modifier,
    client: LemmyClient,
    serverInfo: GetSiteResponse,
) {
    LoginStepContainer(
        modifier,
        Icons.Default.AccountCircle,
        stepTitle = stringResource(MR.strings.sign_up_on_x, serverInfo.siteView.site.name),
        stepSubtitle = client.url,
    ) {
        Text(stringResource(MR.strings.not_available_in_the_app_yet), style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))

        val uriHandler = LocalUriHandler.current
        Button({ uriHandler.openUri(client.url) }) {
            Text(stringResource(MR.strings.sign_up_online))
        }
    }
}
