package io.github.mmauro94.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.entities.Community
import io.github.mmauro94.common.client.entities.PostMediaInfo
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.ui.components.LoadableImage
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

private val LATERAL_PADDING = 8.dp

@Composable
fun Post(
    postView: PostView,
    onClick: (() -> Unit)?,
    openCommunity: (Community) -> Unit,
    enableBodyClicks: Boolean = false,
    maxBodyHeight: Dp? = 128.dp,
) {
    Box(Modifier.padding(bottom = 12.dp)) {
        Surface(shadowElevation = 4.dp, tonalElevation = 4.dp) {
            var modifier: Modifier = Modifier
            if (onClick != null) modifier = modifier.clickable(onClick = onClick)
            Column(modifier.padding(top = 8.dp)) {
                PostHeader(postView, openCommunity)
                PostContent(postView, maxBodyHeight = maxBodyHeight, enableBodyClicks = enableBodyClicks)
                PostFooter(postView)
            }
        }
    }
}

@Composable
fun PostHeader(
    postView: PostView,
    openCommunity: (Community) -> Unit,
) {
    Column(Modifier.padding(horizontal = LATERAL_PADDING)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CommunityInfo(postView.community, openCommunity)
            var now by remember { mutableStateOf(Clock.System.now()) }
            LaunchedEffect(Unit) {
                while (true) {
                    now = Clock.System.now()
                    delay(1.seconds)
                }
            }
            Text(
                text = "•",
                modifier = Modifier.padding(horizontal = 2.dp),
                color = MaterialTheme.colorScheme.onSurfaceLowlighted,
            )
            val duration = (now - postView.post.published).inWholeMinutes.minutes
            Text(
                text = duration.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceLowlighted,
            )
            if (postView.post.url != null) {
                Text(
                    text = "•",
                    modifier = Modifier.padding(horizontal = 2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceLowlighted,
                )
                Text(
                    text = postView.post.url.host.removePrefix("www."),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceLowlighted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Row {
            Text(
                text = postView.post.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (postView.post.url != null && postView.post.mediaInfo == null) {
                Spacer(Modifier.width(8.dp))
                val uriHandler = LocalUriHandler.current
                Surface(
                    Modifier.size(64.dp).clickable { uriHandler.openUri(postView.post.url.toString()) },
                    shape = MaterialTheme.shapes.extraSmall,
                ) {
                    if (postView.post.thumbnailUrl != null) {
                        LoadableImage(
                            Modifier.fillMaxSize(),
                            Modifier.fillMaxSize(),
                            postView.post.thumbnailUrl,
                        )
                    } else {
                        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant)) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.Center).size(32.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariantLowlighted,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityInfo(
    community: Community,
    openCommunity: (Community) -> Unit,
) {
    Text(
        text = community.name,
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.clickable { openCommunity(community) },
    )
}

@Composable
fun ColumnScope.PostContent(postView: PostView, enableBodyClicks: Boolean, maxBodyHeight: Dp? = null) {
    when (val mediaInfo = postView.post.mediaInfo) {
        null -> {}
        is PostMediaInfo.Image -> {
            Spacer(Modifier.height(16.dp))
            LoadableImage(
                Modifier.fillMaxWidth(),
                Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                image = mediaInfo.thumbnailUrl,
                contentScale = ContentScale.FillWidth,
            )
        }
    }
    if (postView.post.body != null && !postView.post.body.isBlank()) {
        Spacer(Modifier.height(12.dp))
        PostBody(
            modifier = Modifier.padding(horizontal = LATERAL_PADDING).fillMaxWidth(),
            body = postView.post.body,
            enableClicks = enableBodyClicks,
            maxHeight = maxBodyHeight,
        )
    }
}

@Composable
fun PostFooter(postView: PostView) {
    val color = MaterialTheme.colorScheme.onSurfaceLowlighted
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.ModeComment, stringResource(MR.strings.comments), tint = color, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${postView.counts.comments} comments", // TODO
                style = MaterialTheme.typography.labelLarge,
                color = color,
            )
        }

        Spacer(Modifier.weight(1f))
        FooterIcon({}, icon = Icons.Default.ArrowUpward, stringResource(MR.strings.upvote_action))
        Text(
            text = (postView.counts.upvotes - postView.counts.downvotes).toString(),
            style = MaterialTheme.typography.labelLarge,
            color = color,
        )
        FooterIcon({}, icon = Icons.Default.ArrowDownward, stringResource(MR.strings.downvote_action))
        FooterIcon({}, icon = Icons.Default.BookmarkBorder, stringResource(MR.strings.save_action))
        if (postView.post.url != null) {
            val uriHandler = LocalUriHandler.current
            FooterIcon(
                onClick = { uriHandler.openUri(postView.post.url.toString()) },
                icon = Icons.Default.OpenInBrowser,
                contentDescription = stringResource(MR.strings.open_in_browser),
            )
        }
        FooterIcon({}, icon = Icons.Default.MoreVert, stringResource(MR.strings.more_options))
    }
}

@Composable
private fun FooterIcon(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
) {
    IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceLowlighted,
            modifier = Modifier.size(22.dp),
        )
    }
}
