@file:UseSerializers(InstantSerializer::class)

package io.github.mmauro94.common.client.entities

import io.github.mmauro94.common.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Tagline(
    val content: String,
    val id: Long,
    @SerialName("local_site_id") val localSiteId: Long,
    val published: Instant,
    val updated: Instant?,
)
