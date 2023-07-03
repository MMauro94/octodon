package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class LocalUserView(
    val counts: PersonAggregates,
    @SerialName("local_user") val localUser: LocalUser,
    val person: Person,
)
