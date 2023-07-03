package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import io.github.mmauro94.common.client.ParsableInstant
import io.github.mmauro94.common.client.ParsableUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class CustomEmoji(
    @SerialName("alt_text") val altText: String,
    val category: String,
    val id: Long,
    @SerialName("image_url") val imageUrl: ParsableUrl,
    @SerialName("local_site_id") val localSiteId: Long,
    val published: ParsableInstant,
    val shortcode: String,
    val updated: ParsableInstant?,
)
