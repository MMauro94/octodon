package io.github.mmauro94.common.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.ui.onSurfaceLowlighted
import io.github.mmauro94.common.utils.LikeStatus

private val footerIconColor
    @Composable
    get() = MaterialTheme.colorScheme.onSurfaceLowlighted

@Composable
fun FooterIcon(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean = true,
    highlighted: Boolean = false,
) {
    IconButton(onClick = onClick, modifier = Modifier.size(44.dp), enabled = enabled) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (highlighted) MaterialTheme.colorScheme.primary else footerIconColor,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
fun FooterLabeledIcon(
    icon: ImageVector,
    label: String,
) {
    Row(Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = footerIconColor, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = footerIconColor,
        )
    }
}

@Composable
fun FooterVoteIcons(
    likeStatus: LikeStatus,
    upvotes: Long,
    enableVoting: Boolean,
    onUpvote: () -> Unit,
    onDownvote: () -> Unit,
) {
    FooterIcon(
        onClick = onUpvote,
        icon = Icons.Default.ArrowUpward,
        contentDescription = stringResource(MR.strings.upvote_action),
        enabled = enableVoting,
        highlighted = likeStatus == LikeStatus.UPVOTED,
    )
    Text(
        text = upvotes.toString(),
        style = MaterialTheme.typography.labelLarge,
        color = footerIconColor,
    )
    FooterIcon(
        onClick = onDownvote,
        icon = Icons.Default.ArrowDownward,
        contentDescription = stringResource(MR.strings.downvote_action),
        enabled = enableVoting,
        highlighted = likeStatus == LikeStatus.DOWNVOTED,
    )
}
