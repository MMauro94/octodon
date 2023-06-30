package io.github.mmauro94.common.client.entities

import kotlinx.serialization.Serializable

@Serializable
data class CommunityBlockView(
    val community: Community,
    val person: Person,
)
