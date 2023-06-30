package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomEmojiView(
    @SerialName("custom_emoji") val customEmoji: CustomEmoji,
    val keywords: List<CustomEmojiKeyword>,
)
