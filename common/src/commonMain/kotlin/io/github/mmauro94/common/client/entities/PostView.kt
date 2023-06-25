@file:UseSerializers(InstantSerializer::class)

package io.github.mmauro94.common.client.entities

import io.github.mmauro94.common.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class PostView(
    val post: Post,
    val creator: Person,
    val community: Community,
    @SerialName("creator_banned_from_community") val creatorBannedFromCommunity: Boolean,
    val counts: Counts,
    val subscribed: SubscribedType,
    val saved: Boolean,
    val read: Boolean,
    @SerialName("creator_blocked") val creatorBlocked: Boolean,
    @SerialName("my_vote") val myVote: Int?,
    @SerialName("unread_comments") val unreadComments: Int,
) {

    @Serializable
    data class Counts(
        val id: Long,
        @SerialName("post_id") val postId: Long,
        val comments: Int,
        val score: Int,
        val upvotes: Int,
        val downvotes: Int,
        val published: Instant,
        @SerialName("newest_comment_time_necro") val newestCommentTimeNecro: Instant?,
        @SerialName("newest_comment_time") val newestCommentTime: Instant?,
        @SerialName("featured_community") val featuredCommunity: Boolean,
        @SerialName("featured_local") val featuredLocal: Boolean,
        @SerialName("hot_rank") val hotRank: Int,
        @SerialName("hot_rank_active") val hotRankActive: Int,
    )
}