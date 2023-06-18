@file:UseSerializers(InstantSerializer::class)

package io.github.mmauro94.common.client.entities

import io.github.mmauro94.common.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Community(
    val id: Long,
    val name: String,
    val description: String?,
    val removed: Boolean,
    val published: Instant,
    val updated: Instant?,
    val deleted: Boolean,
    val nsfw: Boolean,
    @SerialName("actor_id") val actorId: String,
    val local: Boolean,
    val icon: String?,
    val banner: String?,
    val hidden: Boolean,
    @SerialName("posting_restricted_to_mods") val postingRestrictedToMods: Boolean,
    @SerialName("instance_id") val instanceId: Long,
)
