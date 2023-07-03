@file:OptIn(ExperimentalSerializationApi::class)

package io.github.mmauro94.common.client.entities

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Moving
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.utils.ComposableString
import io.github.mmauro94.common.utils.composable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@Immutable
enum class SortType(val label: ComposableString, val group: Group) {
    @SerialName("Active")
    @JsonNames("active")
    ACTIVE(MR.strings.sort_active.composable(), Group.ACTIVE),

    @SerialName("Hot")
    @JsonNames("hot")
    HOT(MR.strings.sort_hot.composable(), Group.HOT),

    @SerialName("New")
    @JsonNames("new")
    NEW(MR.strings.sort_new.composable(), Group.NEW),

    @SerialName("Old")
    @JsonNames("old")
    OLD(MR.strings.sort_old.composable(), Group.OLD),

    @SerialName("TopHour")
    @JsonNames("tophour")
    TOP_HOUR(MR.plurals.sort_top_n_hours.composable(1, 1), Group.TOP),

    @SerialName("TopSixHour")
    @JsonNames("topsixhour")
    TOP_SIXHOUR(MR.plurals.sort_top_n_hours.composable(6, 6), Group.TOP),

    @SerialName("TopTwelveHour")
    @JsonNames("toptwelvehour")
    TOP_TWELVEHOUR(MR.plurals.sort_top_n_hours.composable(12, 12), Group.TOP),

    @SerialName("TopDay")
    @JsonNames("topday")
    TOP_DAY(MR.strings.sort_top_day.composable(), Group.TOP),

    @SerialName("TopWeek")
    @JsonNames("topweek")
    TOP_WEEK(MR.strings.sort_top_week.composable(), Group.TOP),

    @SerialName("TopMonth")
    @JsonNames("topmonth")
    TOP_MONTH(MR.strings.sort_top_month.composable(), Group.TOP),

    @SerialName("TopYear")
    @JsonNames("topyear")
    TOP_YEAR(MR.strings.sort_top_year.composable(), Group.TOP),

    @SerialName("TopAll")
    @JsonNames("topall")
    TOP_ALL(MR.strings.sort_top_all.composable(), Group.TOP),

    @SerialName("MostComments")
    @JsonNames("mostcomments")
    MOST_COMMENTS(MR.strings.sort_most_comments.composable(), Group.MOST_COMMENTS),

    @SerialName("NewComments")
    @JsonNames("newcomments")
    NEW_COMMENTS(MR.strings.sort_new_comments.composable(), Group.NEW_COMMENTS),
    ;

    enum class Group(val label: ComposableString, val icon: ImageVector) {
        ACTIVE(MR.strings.sort_active.composable(), Icons.Default.Moving),
        HOT(MR.strings.sort_hot.composable(), Icons.Default.LocalFireDepartment),
        NEW(MR.strings.sort_new.composable(), Icons.Default.NewReleases),
        MOST_COMMENTS(MR.strings.sort_most_comments.composable(), Icons.Default.Forum),
        NEW_COMMENTS(MR.strings.sort_new_comments.composable(), Icons.Default.Reviews),
        TOP(MR.strings.sort_top.composable(), Icons.Default.BarChart),
        OLD(MR.strings.sort_old.composable(), Icons.Default.Elderly),
    }

    companion object {
        val GROUPS = values().groupBy { it.group }
    }
}
