package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.entities.ListingType
import io.github.mmauro94.common.client.entities.Post
import io.github.mmauro94.common.client.entities.SortType
import io.github.mmauro94.common.utils.serialName
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
private data class GetPostsBody(
    val posts: List<Post>,
)

suspend fun LemmyClient.getPosts(
    communityId: Long? = null,
    page: Int? = null,
    limit: Int? = null,
    sort: SortType? = null,
    type: ListingType? = null,
    savedOnly: Boolean? = null,
): ApiResult<List<Post>> {
    return ApiResult {
        ktorClient.get("post/list") {
            parameter("community_id", communityId)
            parameter("page", page)
            parameter("limit", limit)
            parameter("sort", sort?.serialName())
            parameter("type_", type?.serialName())
            parameter("saved_only", savedOnly)
        }.body<GetPostsBody>().posts
    }
}
