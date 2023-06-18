package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName

// TODO verify
enum class SubcriptionStatus {

    @SerialName("NotSubscribed")
    NOT_SUBSCRIBED,

    @SerialName("Subscribed")
    SUBSCRIBED,
}
