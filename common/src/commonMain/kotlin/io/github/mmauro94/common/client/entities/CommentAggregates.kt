package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import io.github.mmauro94.common.client.ParsableInstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class CommentAggregates(
    @SerialName("child_count") val childCount: Long,
    @SerialName("comment_id") val commentId: Long,
    val downvotes: Long,
    @SerialName("hot_rank") val hotRank: Long,
    val id: Long,
    val published: ParsableInstant,
    val score: Long,
    val upvotes: Long,
)
