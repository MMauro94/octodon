package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import io.github.mmauro94.common.client.ParsableInstant
import io.github.mmauro94.common.client.ParsableMarkdown
import io.github.mmauro94.common.client.ParsableUrl
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
@Immutable
data class Comment(
    val id: Long,
    @SerialName("ap_id") val apId: ParsableUrl,
    val content: ParsableMarkdown,
    @SerialName("creator_id") val creatorId: Long,
    val deleted: Boolean,
    val distinguished: Boolean,
    @SerialName("language_id") val languageId: Long,
    val local: Boolean,
    val path: CommentPath,
    @SerialName("post_id") val postId: Long,
    val published: ParsableInstant,
    val removed: Boolean,
    val updated: ParsableInstant?,
) {

    val parentId = path.ids.getOrNull(path.ids.size - 2)?.takeIf { it > 0 }

    init {
        require(path.ids.last() == id)
    }
}

@Serializable(CommentPath.Serializer::class)
@Immutable
data class CommentPath(
    val originalValue: String,
    val ids: List<Long>,
) {

    val depth = ids.size - 1

    init {
        require(ids.isNotEmpty())
        require(ids[0] == 0L)
        require(originalValue == ids.joinToString("."))
    }

    companion object {
        fun parse(value: String): CommentPath {
            return CommentPath(
                originalValue = value,
                ids = value.split('.').map { it.toLong() },
            )
        }
    }

    object Serializer : KSerializer<CommentPath> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("CommentPath", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): CommentPath {
            return parse(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: CommentPath) {
            encoder.encodeString(value.originalValue)
        }
    }
}
