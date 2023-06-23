package io.github.mmauro94.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BookmarkBorder
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mmauro94.common.client.entities.Community
import io.github.mmauro94.common.client.entities.Post
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

@Composable
fun Post(
    post: Post,
) {
    Box(Modifier.padding(vertical = 4.dp)) {
        Surface(shadowElevation = 4.dp, tonalElevation = 4.dp) {
            Column(Modifier.padding(top = 8.dp)) {
                PostHeader(post)
                PostContent(post)
                PostFooter(post)
            }
        }
    }
}

@Composable
fun PostHeader(
    post: Post,
) {
    Column(Modifier.padding(horizontal = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CommunityInfo(post.community)
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
            val duration = (now - post.post.published).inWholeMinutes.minutes
            Text(
                text = duration.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceLowlighted,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = post.post.name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
        )
    }
}

@Composable
fun CommunityInfo(
    community: Community,
) {
    Text(
        text = community.name,
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
fun PostContent(post: Post) {
    if (post.post.body != null) {
        Surface(
            modifier = Modifier.padding(horizontal = 8.dp).padding(top = 4.dp).fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium,
        ) {
            Box(Modifier.padding(4.dp)) {
                PostBody(
                    body = post.post.body,
                    maxLines = 4,
                )
            }
        }
    }
}

@Composable
fun PostFooter(post: Post) {
    val color = MaterialTheme.colorScheme.onSurfaceLowlighted
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.ModeComment, contentDescription = "comments", tint = color, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${post.counts.comments} comments",
                style = MaterialTheme.typography.labelLarge,
                color = color,
            )
        }

        Spacer(Modifier.weight(1f))
        FooterIcon({}, icon = Icons.Default.ArrowUpward, contentDescription = "upvote")
        Text(
            text = (post.counts.upvotes - post.counts.downvotes).toString(),
            style = MaterialTheme.typography.labelLarge,
            color = color,
        )
        FooterIcon({}, icon = Icons.Default.ArrowDownward, contentDescription = "downvote")
        FooterIcon({}, icon = Icons.Default.BookmarkBorder, contentDescription = "save")
        FooterIcon({}, icon = Icons.Default.OpenInBrowser, contentDescription = "open link")
        FooterIcon({}, icon = Icons.Default.MoreVert, contentDescription = "options")
    }
}

@Composable
private fun FooterIcon(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
) {
    IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceLowlighted,
            modifier = Modifier.size(20.dp),
        )
    }
}
