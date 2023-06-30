package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.entities.CommentSortType
import io.github.mmauro94.common.client.entities.CommentView
import io.github.mmauro94.common.client.entities.ListingType
import io.github.mmauro94.common.utils.serialName
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

@Serializable
private data class GetCommentsBody(
    val comments: List<CommentView>,
)

suspend fun LemmyClient.getComments(
    communityId: Long? = null,
    communityName: String? = null,
    page: Int? = null,
    limit: Int? = null,
    maxDepth: Int? = null,
    parentId: Long? = null,
    postId: Long? = null,
    savedOnly: Boolean? = null,
    sort: CommentSortType? = null,
    type: ListingType? = null,
): ApiResult<List<CommentView>> {
    return ApiResult {
        ktorClient.get("comment/list") {
            parameter("community_id", communityId)
            parameter("community_name", communityName)
            parameter("page", page)
            parameter("limit", limit)
            parameter("max_depth", maxDepth)
            parameter("parent_id", parentId)
            parameter("post_id", postId)
            parameter("saved_only", savedOnly)
            parameter("sort", sort?.serialName())
            parameter("type_", type?.serialName())
        }.body<GetCommentsBody>().comments
    }
}
