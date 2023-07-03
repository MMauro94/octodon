package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class SiteAggregates(
    val id: Long,
    @SerialName("site_id") val siteId: Long,
    val comments: Int,
    val communities: Int,
    val posts: Int,
    val users: Int,
    @SerialName("users_active_day") val usersActiveDay: Int,
    @SerialName("users_active_half_year") val usersActiveHalfYear: Int,
    @SerialName("users_active_month") val usersActiveMonth: Int,
    @SerialName("users_active_week") val usersActiveWeek: Int,
)
