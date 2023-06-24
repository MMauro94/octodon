package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.entities.SiteView
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

suspend fun LemmyClient.getSite(): ApiResult<GetSiteResponse> {
    return ApiResult {
        ktorClient.get("site").body<GetSiteResponse>()
    }
}

@Serializable
data class GetSiteResponse(
    // TODO val admins: List<PersonView>
    // TODO @SerialName("all_languages") val allLanguages: List<Language>,
    // TODO @SerialName("custom_emojis") val customEmojis: List<CustomEmojiView>,
    @SerialName("discussion_languages") val discussionLanguages: List<Long>,
    // TODO @SerialName("my_user") val myUser: MyUserInfo?,
    @SerialName("site_view") val siteView: SiteView,
    // TODO val taglines: List<Tagline>,
    val version: String,
)
