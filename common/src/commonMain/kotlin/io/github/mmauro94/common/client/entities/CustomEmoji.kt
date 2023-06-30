@file:UseSerializers(InstantSerializer::class)

package io.github.mmauro94.common.client.entities

import io.github.mmauro94.common.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class CustomEmoji(
    @SerialName("alt_text") val altText: String,
    val category: String,
    val id: Long,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("local_site_id") val localSiteId: Long,
    val published: Instant,
    val shortcode: String,
    val updated: Instant?,
)