package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomEmojiKeyword(
    @SerialName("custom_emoji_id") val customEmojiId: Long,
    val id: Long,
    val keyword: String,
)
