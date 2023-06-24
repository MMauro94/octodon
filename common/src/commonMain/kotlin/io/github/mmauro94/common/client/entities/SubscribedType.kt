package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SubscribedType {

    @SerialName("NotSubscribed")
    NOT_SUBSCRIBED,

    @SerialName("Subscribed")
    SUBSCRIBED,

    @SerialName("Pending")
    PENDING,
}
