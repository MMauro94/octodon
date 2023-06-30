package io.github.mmauro94.common.client.entities

import io.github.mmauro94.common.client.ParsableInstant
import io.github.mmauro94.common.client.ParsableUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val id: Long,
    val name: String,
    @SerialName("display_name") val displayName: String?,
    val avatar: ParsableUrl?,
    val banned: Boolean,
    val published: ParsableInstant,
    val updated: ParsableInstant?,
    @SerialName("actor_id") val actorId: ParsableUrl,
    val bio: String?,
    val local: Boolean,
    val banner: ParsableUrl?,
    val deleted: Boolean,
    @SerialName("inbox_url") val inboxUrl: ParsableUrl?,
    @SerialName("shared_inbox_url") val sharedInboxUrl: ParsableUrl?,
    @SerialName("matrix_user_id") val matrixUserId: String?,
    val admin: Boolean,
    @SerialName("bot_account") val botAccount: Boolean,
    @SerialName("instance_id") val instanceId: Long,
)
