@file:UseSerializers(InstantSerializer::class)

package io.github.mmauro94.common.client.entities

import io.github.mmauro94.common.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class LocalUser(
    @SerialName("accepted_application") val acceptedApplication: Boolean,
    @SerialName("default_listing_type") val defaultListingType: ListingType,
    @SerialName("default_sort_type") val defaultSortType: SortType,
    val email: String?,
    @SerialName("email_verified") val emailVerified: Boolean,
    val id: Long,
    @SerialName("interface_language") val interfaceLanguage: String,
    @SerialName("person_id") val personId: Long,
    @SerialName("send_notifications_to_email") val sendNotificationsToEmail: Boolean,
    @SerialName("show_avatars") val showAvatars: Boolean,
    @SerialName("show_bot_accounts") val showBotAccounts: Boolean,
    @SerialName("show_new_post_notifs") val showNewPostNotifs: Boolean,
    @SerialName("show_nsfw") val showNsfw: Boolean,
    @SerialName("show_read_posts") val showReadPosts: Boolean,
    @SerialName("show_scores") val showScores: Boolean,
    val theme: String,
    @SerialName("totp_2fa_url") val totp2FaUrl: String?,
    @SerialName("validator_time") val validatorTime: Instant,
)