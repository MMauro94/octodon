package io.github.mmauro94.common.serializers

import io.github.mmauro94.common.markdown.Markdown
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class MarkdownSerializer : KSerializer<Markdown> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Markdown", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Markdown {
        return Markdown.of(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Markdown) {
        throw UnsupportedOperationException()
    }
}
