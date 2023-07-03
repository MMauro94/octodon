package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import io.github.mmauro94.common.client.ParsableInstant
import io.github.mmauro94.common.client.ParsableUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Site(
    val id: Long,
    @SerialName("actor_id") val actorId: ParsableUrl,
    val banner: ParsableUrl?,
    val description: String?,
    val icon: ParsableUrl?,
    @SerialName("inbox_url") val inboxUrl: ParsableUrl,
    @SerialName("instance_id") val instanceId: Long,
    @SerialName("last_refreshed_at") val lastRefreshedAt: ParsableInstant,
    val name: String,
    @SerialName("private_key") val privateKey: String?,
    @SerialName("public_key") val publicKey: String,
    val published: ParsableInstant,
    val sidebar: String?,
    val updated: ParsableInstant?,
)
