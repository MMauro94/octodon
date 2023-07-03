@file:OptIn(ExperimentalSerializationApi::class)

package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@Immutable
enum class RegistrationMode {

    @SerialName("Closed")
    @JsonNames("closed")
    CLOSED,

    @SerialName("RequireApplication")
    @JsonNames("requireapplication")
    REQUIRE_APPLICATION,

    @SerialName("Open")
    @JsonNames("open")
    OPEN,
}
