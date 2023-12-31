package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class SiteView(
    val counts: SiteAggregates,
    @SerialName("local_site") val localSite: LocalSite,
    @SerialName("local_site_rate_limit") val localSiteRateLimit: LocalSiteRateLimit,
    val site: Site,
)
