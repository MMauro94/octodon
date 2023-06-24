package io.github.mmauro94.common.client.entities

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Public
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.utils.ComposableString
import io.github.mmauro94.common.utils.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ListingType(val label: ComposableString, val icon: ImageVector) {
    @SerialName("Subscribed")
    SUBSCRIBED(MR.strings.listing_type_subscribed.composable(), Icons.Filled.Home),

    @SerialName("Local")
    LOCAL(MR.strings.listing_type_local.composable(), Icons.Default.LocationCity),

    @SerialName("All")
    ALL(MR.strings.listing_type_all.composable(), Icons.Default.Public),
}
