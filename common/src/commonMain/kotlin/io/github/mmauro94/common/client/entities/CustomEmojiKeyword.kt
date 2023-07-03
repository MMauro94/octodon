package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class CustomEmojiKeyword(
    @SerialName("custom_emoji_id") val customEmojiId: Long,
    val id: Long,
    val keyword: String,
)
