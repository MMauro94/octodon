package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
@Immutable
enum class CommentSortType {

    @SerialName("Hot")
    @JsonNames("hot")
    HOT,

    @SerialName("Top")
    @JsonNames("top")
    TOP,

    @SerialName("New")
    @JsonNames("new")
    NEW,

    @SerialName("Old")
    @JsonNames("old")
    OLD,
}
