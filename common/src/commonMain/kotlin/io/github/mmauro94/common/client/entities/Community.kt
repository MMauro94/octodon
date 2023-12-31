package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import io.github.mmauro94.common.client.ParsableInstant
import io.github.mmauro94.common.client.ParsableMarkdown
import io.github.mmauro94.common.client.ParsableUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Community(
    val id: Long,
    val name: String,
    val title: String,
    val description: ParsableMarkdown?,
    val removed: Boolean,
    val published: ParsableInstant,
    val updated: ParsableInstant?,
    val deleted: Boolean,
    val nsfw: Boolean,
    @SerialName("actor_id") val actorId: ParsableUrl,
    val local: Boolean,
    val icon: ParsableUrl?,
    val banner: ParsableUrl?,
    val hidden: Boolean,
    @SerialName("posting_restricted_to_mods") val postingRestrictedToMods: Boolean,
    @SerialName("instance_id") val instanceId: Long,
)
