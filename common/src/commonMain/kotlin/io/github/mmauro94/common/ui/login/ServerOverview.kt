package io.github.mmauro94.common.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MailLock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.api.GetSiteResponse

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServerOverview(
    modifier: Modifier,
    serverUrl: String,
    serverInfo: GetSiteResponse,
    logIn: () -> Unit,
    signIn: () -> Unit,
    enterAnonimously: () -> Unit,
) {
    LoginStepContainer(
        modifier,
        Icons.Default.Dns,
        stepTitle = serverInfo.siteView.site.name,
        stepSubtitle = serverUrl,
    ) {
        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Tag(
                serverInfo.siteView.localSite.enableDownvotes,
                Icons.Default.ArrowDownward,
                stringResource(MR.strings.downvotes_enabled),
                stringResource(MR.strings.downvotes_disabled),
            )
            Tag(
                serverInfo.siteView.localSite.federationEnabled,
                Icons.Default.Share,
                stringResource(MR.strings.federation_enabled),
                stringResource(MR.strings.federation_disabled),
            )
            Tag(
                serverInfo.siteView.localSite.privateInstance,
                Icons.Default.Shield,
                stringResource(MR.strings.private_instance),
                stringResource(MR.strings.public_instance),
            )
            Tag(
                serverInfo.siteView.localSite.requireEmailVerification,
                Icons.Default.MailLock,
                stringResource(MR.strings.email_verification_required),
                Icons.Default.Mail,
                stringResource(MR.strings.email_verification_not_required),
            )
            Tag(
                serverInfo.siteView.localSite.enableNsfw,
                Icons.Default.Warning,
                stringResource(MR.strings.nsfw_enabled),
                stringResource(MR.strings.nsfw_disabled),
            )
        }
        Spacer(Modifier.height(16.dp))
        Info(stringResource(MR.strings.banner), serverInfo.siteView.site.banner)
        Info(stringResource(MR.strings.description), serverInfo.siteView.site.description)
        Info(stringResource(MR.strings.sidebar), serverInfo.siteView.site.sidebar)
        Info(stringResource(MR.strings.legal_information), serverInfo.siteView.localSite.legalInformation)

        Button(logIn) {
            Text(stringResource(MR.strings.log_in))
        }
        Button(signIn) {
            Text(stringResource(MR.strings.sign_up))
        }
        Button(enterAnonimously) {
            Text(stringResource(MR.strings.browse_anonymously))
        }
    }
}

@Composable
private fun Tag(
    value: Boolean,
    ifTrueIcon: ImageVector,
    ifTrueLabel: String,
    ifFalseIcon: ImageVector,
    ifFalseLabel: String,
) {
    Box(Modifier.padding(4.dp)) {
        Card(shape = MaterialTheme.shapes.extraSmall) {
            Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(if (value) ifTrueIcon else ifFalseIcon, null, Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (value) ifTrueLabel else ifFalseLabel, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun Tag(
    value: Boolean,
    icon: ImageVector,
    ifTrueLabel: String,
    ifFalseLabel: String,
) {
    Tag(value, icon, ifTrueLabel, icon, ifFalseLabel)
}

@Composable
private fun ColumnScope.Info(title: String, content: String?) {
    if (content != null) {
        val modifier = Modifier.align(Alignment.Start)
        Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = modifier)
        Text(content, style = MaterialTheme.typography.bodyMedium, modifier = modifier)
        Spacer(Modifier.height(16.dp))
    }
}
