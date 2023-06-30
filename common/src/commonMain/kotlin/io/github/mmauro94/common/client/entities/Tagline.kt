package io.github.mmauro94.common.client.entities

import io.github.mmauro94.common.client.ParsableInstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tagline(
    val content: String,
    val id: Long,
    @SerialName("local_site_id") val localSiteId: Long,
    val published: ParsableInstant,
    val updated: ParsableInstant?,
)
