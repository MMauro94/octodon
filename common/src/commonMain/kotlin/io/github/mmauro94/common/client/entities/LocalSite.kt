@file:UseSerializers(InstantSerializer::class)

package io.github.mmauro94.common.client.entities

import io.github.mmauro94.common.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class LocalSite(
    val id: Long,
    @SerialName("actor_name_max_length") val actorNameMaxLength: Int,
    @SerialName("application_email_admins") val applicationEmailAdmins: Boolean,
    @SerialName("application_question") val applicationQuestion: String?,
    @SerialName("captcha_difficulty") val captchaDifficulty: String,
    @SerialName("captcha_enabled") val captchaEnabled: Boolean,
    @SerialName("community_creation_admin_only") val communityCreationAdminOnly: Boolean,
    @SerialName("default_post_listing_type") val defaultPostListingType: ListingType,
    @SerialName("default_theme") val defaultTheme: String,
    @SerialName("enable_downvotes") val enableDownvotes: Boolean,
    @SerialName("enable_nsfw") val enableNsfw: Boolean,
    @SerialName("federation_enabled") val federationEnabled: Boolean,
    @SerialName("federation_worker_count") val federationWorkerCount: Int?,
    @SerialName("hide_modlog_mod_names") val hideModlogModNames: Boolean,
    @SerialName("legal_information") val legalInformation: String?,
    @SerialName("private_instance") val privateInstance: Boolean,
    val published: Instant,
    @SerialName("registration_mode") val registrationMode: RegistrationMode,
    @SerialName("reports_email_admins") val reportsEmailAdmins: Boolean,
    @SerialName("require_email_verification") val requireEmailVerification: Boolean,
    @SerialName("site_id") val siteId: Long,
    @SerialName("site_setup") val siteSetup: Boolean,
    @SerialName("slur_filter_regex") val slurFilterRegex: String?,
    @SerialName("updated") val updated: Instant?,
)
