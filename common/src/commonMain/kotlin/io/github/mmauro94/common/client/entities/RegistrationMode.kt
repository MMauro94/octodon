package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RegistrationMode {

    @SerialName("Closed")
    CLOSED,

    @SerialName("RequireApplication")
    REQUIRE_APPLICATION,

    @SerialName("Open")
    OPEN,
}
