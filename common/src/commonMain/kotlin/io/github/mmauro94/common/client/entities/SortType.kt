package io.github.mmauro94.common.client.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SortType {
    @SerialName("Active")
    ACTIVE,

    @SerialName("Hot")
    HOT,

    @SerialName("New")
    NEW,

    @SerialName("Old")
    OLD,

    @SerialName("TopDay")
    TOP_DAY,

    @SerialName("TopWeek")
    TOP_WEEK,

    @SerialName("TopMonth")
    TOP_MONTH,

    @SerialName("TopYear")
    TOP_YEAR,

    @SerialName("TopAll")
    TOP_ALL,

    @SerialName("MostComments")
    MOST_COMMENTS,

    @SerialName("NewComments")
    NEW_COMMENTS,

    @SerialName("TopHour")
    TOP_HOUR,

    @SerialName("TopSixHour")
    TOP_SIXHOUR,

    @SerialName("TopTwelveHour")
    TOP_TWELVEHOUR,
}
