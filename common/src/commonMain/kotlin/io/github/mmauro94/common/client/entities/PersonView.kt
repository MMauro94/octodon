package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class PersonView(
    val counts: PersonAggregates,
    val person: Person,
)
