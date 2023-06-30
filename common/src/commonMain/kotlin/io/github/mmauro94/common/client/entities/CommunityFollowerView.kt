package io.github.mmauro94.common.client.entities

import kotlinx.serialization.Serializable

@Serializable
data class CommunityFollowerView(
    val community: Community,
    val follower: Person,
)
