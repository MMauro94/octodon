package io.github.mmauro94.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInNew
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
                PostFooter()
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
            val duration = (now - post.post.published).inWholeMinutes.minutes
            Text(duration.toString(), style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.height(4.dp))
        Text(post.post.name, style = MaterialTheme.typography.titleMedium, maxLines = 2)
        Spacer(Modifier.height(4.dp))
        Row {
            Text(
                text = (post.counts.upvotes - post.counts.downvotes).toString(),
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = "${post.counts.comments} comments",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
fun CommunityInfo(
    community: Community,
) {
    Text(community.name, style = MaterialTheme.typography.labelMedium, maxLines = 1)
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
fun PostFooter() {
    Row {
        IconButton({}, Modifier.weight(1f)) {
            Icon(Icons.Default.ArrowUpward, "upvote")
        }
        IconButton({}, Modifier.weight(1f)) {
            Icon(Icons.Default.ArrowDownward, "downvote")
        }
        IconButton({}, Modifier.weight(1f)) {
            Icon(Icons.Default.BookmarkBorder, "save")
        }
        IconButton({}, Modifier.weight(1f)) {
            Icon(Icons.Default.Comment, "open comments")
        }
        IconButton({}, Modifier.weight(1f)) {
            Icon(Icons.Default.OpenInNew, "open link")
        }
        IconButton({}, Modifier.weight(1f)) {
            Icon(Icons.Default.MoreVert, "options")
        }
    }
}
