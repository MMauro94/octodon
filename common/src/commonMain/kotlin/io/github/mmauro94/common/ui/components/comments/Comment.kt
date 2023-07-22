package io.github.mmauro94.common.ui.components.comments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.client.entities.Comment
import io.github.mmauro94.common.client.entities.CommentView
import io.github.mmauro94.common.markdown.ReadOnlyMarkdown
import io.github.mmauro94.common.ui.components.FooterIcon
import io.github.mmauro94.common.ui.components.FooterLabeledIcon
import io.github.mmauro94.common.ui.components.FooterVoteIcons
import io.github.mmauro94.common.ui.onSurfaceLowlighted
import io.github.mmauro94.common.utils.LikeStatus
import io.github.mmauro94.common.utils.comments.CommentTree
import io.github.mmauro94.common.utils.relativeTimeString

@Composable
fun Comment(commentTree: CommentTree, depth: Int) {
    CommentContainer(
        modifier = Modifier.clickable { commentTree.collapsed = !commentTree.collapsed },
        depth = depth,
    ) {
        Column(Modifier.padding(top = 8.dp)) {
            CommentHeader(commentTree.commentView)
            CommentBody(commentTree.commentView.comment)
            CommentFooter(commentTree.commentView)
        }
    }
}

@Composable
private fun CommentHeader(comment: CommentView) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(1f)) {
            Text(
                text = comment.creator.name,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { /* TODO */ },
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = relativeTimeString(comment.comment.published),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceLowlighted,
        )
    }
}

@Composable
private fun CommentBody(comment: Comment) {
    Box(Modifier.padding(8.dp)) {
        ReadOnlyMarkdown(
            markdown = comment.content,
            enableClicks = true,
        )
    }
}

@Composable
private fun CommentFooter(comment: CommentView) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FooterLabeledIcon(
            Icons.Outlined.ModeComment,
            stringResource(MR.plurals.n_replies, comment.counts.childCount.toInt(), comment.counts.childCount),
        )
        Spacer(Modifier.weight(1f))
        FooterPostVoteIcons(comment)
        FooterIcon({}, icon = Icons.Default.BookmarkBorder, stringResource(MR.strings.save_action))
        FooterIcon({}, icon = Icons.Default.MoreVert, stringResource(MR.strings.more_options))
    }
}

@Composable
private fun FooterPostVoteIcons(
    comment: CommentView,
) {
    val likeStatus = remember(comment.myVote) { LikeStatus.fromScore(comment.myVote) }
    FooterVoteIcons(
        likeStatus = likeStatus,
        upvotes = comment.counts.upvotes - comment.counts.downvotes,
        enableVoting = false,
        onUpvote = { /* TODO */ },
        onDownvote = { /* TODO */ },
    )
}
