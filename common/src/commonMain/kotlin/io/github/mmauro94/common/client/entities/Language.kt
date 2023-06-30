package io.github.mmauro94.common.client.entities

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val code: String,
    val id: Long,
    val name: String,
)
