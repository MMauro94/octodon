package io.github.mmauro94.common.client.entities

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tagline(
    val content: String,
    val id: Long,
    @SerialName("local_site_id") val localSiteId: Long,
    val published: String,
    val updated: Instant?,
)
