package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class CommunityFollowerView(
    val community: Community,
    val follower: Person,
)
