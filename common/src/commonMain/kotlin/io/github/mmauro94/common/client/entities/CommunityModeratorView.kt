package io.github.mmauro94.common.client.entities

import kotlinx.serialization.Serializable

@Serializable
data class CommunityModeratorView(
    val community: Community,
    val moderator: Person,
)
