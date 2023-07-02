package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    @SerialName("post_view") val postView: PostView,
)
