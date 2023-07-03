package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class CustomEmojiView(
    @SerialName("custom_emoji") val customEmoji: CustomEmoji,
    val keywords: List<CustomEmojiKeyword>,
)
