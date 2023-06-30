package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalUserView(
    val counts: PersonAggregates,
    @SerialName("local_user") val localUser: LocalUser,
    val person: Person,
)
