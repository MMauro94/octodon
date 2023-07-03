package io.github.mmauro94.common.client.entities

import androidx.compose.runtime.Immutable
import io.github.mmauro94.common.client.ParsableInstant
import io.github.mmauro94.common.client.ParsableMarkdown
import io.github.mmauro94.common.client.ParsableUrl
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class Post(
    val id: Long,
    val name: String,
    val url: ParsableUrl?,
    val body: ParsableMarkdown?,
    @SerialName("creator_id") val creatorId: Long,
    @SerialName("community_id") val communityId: Long,
    val removed: Boolean,
    val locked: Boolean,
    val published: ParsableInstant,
    val updated: ParsableInstant?,
    val deleted: Boolean,
    val nsfw: Boolean,
    @SerialName("embed_title") val embedTitle: String?,
    @SerialName("embed_description") val embedDescription: String?,
    @SerialName("embed_video_url") val embedVideoUrl: ParsableUrl?,
    @SerialName("thumbnail_url") val thumbnailUrl: ParsableUrl?,
    @SerialName("ap_id") val apId: ParsableUrl?,
    val local: Boolean,
    @SerialName("language_id") val languageId: Int,
    @SerialName("featured_community") val featuredCommunity: Boolean,
    @SerialName("featured_local") val featuredLocal: Boolean,
) {

    @Transient
    val mediaInfo: PostMediaInfo? = run {
        if (thumbnailUrl != null && url != null && IMAGE_EXTENSIONS.any { ext -> url.encodedPath.endsWith(".$ext") }) {
            PostMediaInfo.Image(
                imageUrl = url,
                thumbnailUrl = thumbnailUrl,
            )
        } else {
            null
        }
    }

    companion object {
        private val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp", "tiff", "tif", "bmp")
    }
}

sealed interface PostMediaInfo {

    data class Image(
        val imageUrl: Url,
        val thumbnailUrl: Url,
    ) : PostMediaInfo
}
