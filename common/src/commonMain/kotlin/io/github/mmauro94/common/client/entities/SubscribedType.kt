@file:OptIn(ExperimentalSerializationApi::class)

package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@Immutable
enum class SubscribedType {

    @SerialName("NotSubscribed")
    @JsonNames("notsubscribed")
    NOT_SUBSCRIBED,

    @SerialName("Subscribed")
    @JsonNames("subscribed")
    SUBSCRIBED,

    @SerialName("Pending")
    @JsonNames("pending")
    PENDING,
}
