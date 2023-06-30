package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyUserInfo(
    @SerialName("community_blocks") val communityBlocks: List<CommunityBlockView>,
    @SerialName("discussion_languages") val discussionLanguages: List<Long>,
    val follows: List<CommunityFollowerView>,
    @SerialName("local_user_view") val localUserView: LocalUserView,
    val moderates: List<CommunityModeratorView>,
    @SerialName("person_blocks") val personBlocks: List<PersonBlockView>,
)
