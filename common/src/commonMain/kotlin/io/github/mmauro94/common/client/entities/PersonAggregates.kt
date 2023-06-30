package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonAggregates(
    val id: Long,
    @SerialName("comment_count") val commentCount: Long,
    @SerialName("comment_score") val commentScore: Long,
    @SerialName("person_id") val personId: Long,
    @SerialName("post_count") val postCount: Long,
    @SerialName("post_score") val postScore: Long,
)
