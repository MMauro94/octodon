@file:UseSerializers(InstantSerializer::class)

package io.github.mmauro94.common.client.entities

import io.github.mmauro94.common.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Site(
    val id: Long,
    @SerialName("actor_id") val actorId: String,
    val banner: String?,
    val description: String?,
    val icon: String?,
    @SerialName("inbox_url") val inboxUrl: String,
    @SerialName("instance_id") val instanceId: Long,
    @SerialName("last_refreshed_at") val lastRefreshedAt: Instant,
    val name: String,
    @SerialName("private_key") val privateKey: String?,
    @SerialName("public_key") val publicKey: String,
    val published: Instant,
    val sidebar: String?,
    val updated: Instant?,
)
