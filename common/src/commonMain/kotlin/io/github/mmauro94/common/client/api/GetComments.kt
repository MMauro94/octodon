package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.entities.CommentView
import io.github.mmauro94.common.client.entities.GetCommentsForm
import io.github.mmauro94.common.client.entities.GetCommentsFormContextualInfo
import io.github.mmauro94.common.utils.serialName
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

@Serializable
private data class GetCommentsBody(
    val comments: List<CommentView>,
)

const val DEFAULT_COMMENTS_LIMIT = 50

suspend fun LemmyClient.getComments(form: GetCommentsForm): ApiResult<List<CommentView>> {
    return ApiResult {
        ktorClient.get("comment/list") {
            fill(form)
        }.body<GetCommentsBody>().comments
    }
}

private fun HttpRequestBuilder.fill(form: GetCommentsForm) {
    when (form.context) {
        is GetCommentsFormContextualInfo.CommunityId -> parameter("community_id", form.context.communityId)
        is GetCommentsFormContextualInfo.CommunityName -> parameter("community_name", form.context.communityName)
        is GetCommentsFormContextualInfo.ParentId -> parameter("parent_id", form.context.commentId)
        is GetCommentsFormContextualInfo.PostId -> parameter("post_id", form.context.postId)
        null -> {}
    }
    parameter("max_depth", form.maxDepth)

    if (form.page != null) {
        parameter("page", form.page.page)
        parameter("limit", form.page.limit)
    }

    parameter("saved_only", form.userPreferences.savedOnly)
    parameter("sort", form.userPreferences.sort?.serialName())
    parameter("type_", form.userPreferences.type?.serialName())
}
