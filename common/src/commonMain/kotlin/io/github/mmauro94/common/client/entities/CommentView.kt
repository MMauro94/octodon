package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentView(
    val comment: Comment,
    val community: Community,
    val counts: CommentAggregates,
    val creator: Person,
    @SerialName("creator_banned_from_community") val creatorBannedFromCommunity: Boolean,
    @SerialName("creator_blocked") val creatorBlocked: Boolean,
    @SerialName("my_vote") val myVote: Int?,
    val post: Post,
    val saved: Boolean,
    val subscribed: SubscribedType,
)
