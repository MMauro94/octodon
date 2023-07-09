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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.api.likePost
import io.github.mmauro94.common.client.entities.Community
import io.github.mmauro94.common.client.entities.PostMediaInfo
import io.github.mmauro94.common.client.entities.PostResponse
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.ui.components.FooterIcon
import io.github.mmauro94.common.ui.components.FooterLabeledIcon
import io.github.mmauro94.common.ui.components.FooterVoteIcons
import io.github.mmauro94.common.ui.components.LoadableImage
import io.github.mmauro94.common.utils.AsyncState
import io.github.mmauro94.common.utils.LikeStatus
import io.github.mmauro94.common.utils.LocalLemmyContext
import io.github.mmauro94.common.utils.Result
import io.github.mmauro94.common.utils.composeWorker
import io.github.mmauro94.common.utils.process
import io.github.mmauro94.common.utils.relativeTimeString
import kotlinx.coroutines.launch

private val LATERAL_PADDING = 8.dp

@Composable
fun Post(
    postView: PostView,
    onClick: (() -> Unit)?,
    onUpdatePost: (post: PostView) -> Unit,
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
                PostFooter(postView, onUpdatePost)
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
            Text(
                text = "•",
                modifier = Modifier.padding(horizontal = 2.dp),
                color = MaterialTheme.colorScheme.onSurfaceLowlighted,
            )
            Text(
                text = relativeTimeString(postView.post.published),
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
fun PostFooter(
    postView: PostView,
    onUpdatePost: (post: PostView) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FooterLabeledIcon(
            Icons.Outlined.ModeComment,
            stringResource(MR.plurals.n_comments, postView.counts.comments.toInt(), postView.counts.comments),
        )
        Spacer(Modifier.weight(1f))
        FooterPostVoteIcons(postView, onUpdatePost)
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
private fun FooterPostVoteIcons(
    postView: PostView,
    onUpdatePost: (post: PostView) -> Unit,
) {
    val cs = rememberCoroutineScope()
    val lemmyContext = LocalLemmyContext.current
    val (asyncState, workerChannel) = composeWorker<LikeStatus, PostResponse, StringResource>(
        process = { status ->
            when (val result = lemmyContext.client.likePost(postView.post.id, status)) {
                is Result.Success -> result
                is Result.Error -> Result.Error(MR.strings.connection_error)
            }
        },
        onSuccess = { _, postResponse ->
            onUpdatePost(postResponse.postView)
        },
        onError = { _, _ ->
            // TODO give feedback with snackbar
        },
    )
    val likeStatus = remember(postView.myVote) { LikeStatus.fromScore(postView.myVote) }
    FooterVoteIcons(
        likeStatus = likeStatus,
        upvotes = postView.counts.upvotes - postView.counts.downvotes,
        enableVoting = asyncState !is AsyncState.Loading,
        onUpvote = {
            cs.launch { workerChannel.process(likeStatus.computeNewStatus(LikeStatus.UPVOTED)) }
        },
        onDownvote = {
            cs.launch { workerChannel.process(likeStatus.computeNewStatus(LikeStatus.DOWNVOTED)) }
        },
    )
}
