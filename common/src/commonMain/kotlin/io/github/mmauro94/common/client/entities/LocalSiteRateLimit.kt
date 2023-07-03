package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import io.github.mmauro94.common.client.ParsableInstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class LocalSiteRateLimit(
    val id: Long,
    @SerialName("local_site_id") val localSiteId: Long,
    val comment: Int,
    @SerialName("comment_per_second") val commentPerSecond: Int,
    val image: Int,
    @SerialName("image_per_second") val imagePerSecond: Int,
    val message: Int,
    @SerialName("message_per_second") val messagePerSecond: Long,
    val post: Int,
    @SerialName("post_per_second") val postPerSecond: Long,
    val register: Int,
    @SerialName("register_per_second") val registerPerSecond: Long,
    val search: Int,
    @SerialName("search_per_second") val searchPerSecond: Long,
    val published: ParsableInstant,
    val updated: ParsableInstant?,
)
