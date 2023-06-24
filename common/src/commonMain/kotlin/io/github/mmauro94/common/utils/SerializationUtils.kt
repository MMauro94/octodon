package io.github.mmauro94.common.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
inline fun <reified E : Enum<E>> E.serialName(): String {
    return E::class.serializer().descriptor.getElementName(this.ordinal)
}
