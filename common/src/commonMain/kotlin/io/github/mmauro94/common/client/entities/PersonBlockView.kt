package io.github.mmauro94.common.client.entities

import kotlinx.serialization.Serializable

@Serializable
data class PersonBlockView(
    val person: Person,
    val target: Person,
)
