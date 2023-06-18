@file:UseSerializers(InstantSerializer::class)

package io.github.mmauro94.common.client.entities

import io.github.mmauro94.common.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class PostData(
    val id: Long,
    val name: String,
    val url: String?,
    val body: String?,
    @SerialName("creator_id") val creatorId: Long,
    @SerialName("community_id") val communityId: Long,
    val removed: Boolean,
    val locked: Boolean,
    val published: Instant,
    val updated: Instant?,
    val deleted: Boolean,
    val nsfw: Boolean,
    @SerialName("embed_title") val embedTitle: String?,
    @SerialName("embed_description") val embedDescription: String?,
    @SerialName("embed_video_url") val embedVideoUrl: String?,
    @SerialName("thumbnail_url") val thumbnailUrl: String?,
    @SerialName("ap_id") val apId: String?,
    val local: Boolean,
    @SerialName("language_id") val languageId: Int,
    @SerialName("featured_community") val featuredCommunity: Boolean,
    @SerialName("featured_local") val featuredLocal: Boolean,
)
