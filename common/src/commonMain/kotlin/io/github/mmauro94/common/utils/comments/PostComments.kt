package io.github.mmauro94.common.utils.comments

import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.DEFAULT_COMMENTS_LIMIT
import io.github.mmauro94.common.client.api.getComments
import io.github.mmauro94.common.client.entities.CommentView
import io.github.mmauro94.common.client.entities.GetCommentsForm
import io.github.mmauro94.common.client.entities.GetCommentsFormContextualInfo
import io.github.mmauro94.common.client.entities.GetCommentsFormPageInfo
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.utils.getOr
import io.github.mmauro94.common.utils.mapSuccess

const val MAX_API_CALLS_FOR_FULL_COMMENTS = 2

data class PostComments(
    val postView: PostView,
    val commentList: CommentList<CommentTree.Root>,
)

/**
 * Gets ALL the comments, by calling the API endpoint numberOfComments/[DEFAULT_COMMENTS_LIMIT] times.
 *
 * Should be used only when the number of comments is known beforehand and the number of API calls is deemed acceptable.
 */
private suspend fun LemmyClient.getAllComments(form: GetCommentsForm): ApiResult<List<CommentView>> {
    require(form.page == null) { "Cannot pass page in getAllComments: this field is handled automatically" }

    return ApiResult {
        buildList {
            var page = 1
            while (page > 0) {
                val pageResult = getComments(
                    form.copy(
                        page = GetCommentsFormPageInfo(page, DEFAULT_COMMENTS_LIMIT),
                    ),
                ).getOr { err -> return err }
                addAll(pageResult)
                page++
                if (pageResult.size < DEFAULT_COMMENTS_LIMIT) {
                    page = 0
                }
            }
        }
    }
}

suspend fun LemmyClient.getPostComments(postView: PostView, form: GetCommentsForm): ApiResult<PostComments> {
    require(form.context == null) { "Cannot pass context in getPostComments form: this field is handled automatically" }
    require(form.page == null) { "Cannot pass page in getPostComments: this field is handled automatically" }
    require(form.maxDepth == null) { "Cannot pass maxDepth in getPostComments: this field is handled automatically" }

    val formWithContext = form.copy(context = GetCommentsFormContextualInfo.PostId(postView.post.id))
    if (postView.counts.comments <= DEFAULT_COMMENTS_LIMIT * MAX_API_CALLS_FOR_FULL_COMMENTS - 1) {
        // The post has very few comments, let's get them all
        // This is guaranteed to make at most MAX_API_CALLS_FOR_FULL_COMMENTS API calls
        return getAllComments(formWithContext).mapSuccess {
            PostComments(
                postView = postView,
                commentList = RootCommentList(
                    comments = it.toRoots(form.userPreferences),
                    state = CommentListState.Finished,
                ),
            )
        }
    }

    // If we have a lot of comments we get the first page of the root comments
    // For now, any nested comment will have to be loaded manually
    val formWithPage = formWithContext.copy(
        maxDepth = 1,
        page = GetCommentsFormPageInfo(1, DEFAULT_COMMENTS_LIMIT),
    )
    return getComments(formWithPage).mapSuccess {
        PostComments(
            postView = postView,
            commentList = RootCommentList(
                comments = it.toRoots(form.userPreferences),
                state = CommentListState.MoreComments(formWithPage.withIncreasedPage()), // TODO check if finished
            ),
        )
    }
}
