package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyAuthRequestBody
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.entities.PostResponse
import io.github.mmauro94.common.utils.LikeStatus
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class LikePostRequest(
    override val auth: String,
    @SerialName("post_id") val postId: Long,
    val score: Int,
) : LemmyAuthRequestBody

suspend fun LemmyClient.likePost(
    postId: Long,
    status: LikeStatus,
): ApiResult<PostResponse> {
    return ApiResult {
        ktorClient.post("post/like") {
            setBody(
                LikePostRequest(
                    postId = postId,
                    score = status.score,
                    auth = checkToken(),
                ),
            )
        }.body<PostResponse>()
    }
}
