package io.github.mmauro94.common.client.entities

import kotlinx.serialization.Serializable

@Serializable
data class PersonView(
    val counts: PersonAggregates,
    val person: Person,
)
