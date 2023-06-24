package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ListingType {
    @SerialName("All")
    ALL,

    @SerialName("Local")
    LOCAL,

    @SerialName("Subscribed")
    SUBSCRIBED,
}
