@file:UseSerializers(InstantSerializer::class)

package io.github.mmauro94.common.client.entities

import io.github.mmauro94.common.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Person(
    val id: Long,
    val name: String,
    @SerialName("display_name") val displayName: String?,
    val avatar: String?,
    val banned: Boolean,
    val published: Instant,
    val updated: Instant?,
    @SerialName("actor_id") val actorId: String,
    val bio: String?,
    val local: Boolean,
    val banner: String?,
    val deleted: Boolean,
    @SerialName("inbox_url") val inboxUrl: String?,
    @SerialName("shared_inbox_url") val sharedInboxUrl: String?,
    @SerialName("matrix_user_id") val matrixUserId: String?,
    val admin: Boolean,
    @SerialName("bot_account") val botAccount: Boolean,
    @SerialName("instance_id") val instanceId: Long,
)
