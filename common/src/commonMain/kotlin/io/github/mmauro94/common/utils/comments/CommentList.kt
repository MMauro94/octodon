package io.github.mmauro94.common.utils.comments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.api.getComments
import io.github.mmauro94.common.client.entities.CommentView
import io.github.mmauro94.common.client.entities.GetCommentsForm
import io.github.mmauro94.common.client.entities.GetCommentsFormContextualInfo
import io.github.mmauro94.common.utils.Result

abstract class CommentList<CT : CommentTree>(
    comments: Map<Long, CT>,
    state: CommentListState,
) {
    private var mutableState by mutableStateOf(comments to state)

    val comments get() = mutableState.first
    val state get() = mutableState.second

    fun update(state: CommentListState, comments: Map<Long, CT> = this.comments) {
        mutableState = comments to state
    }

    suspend fun loadNext(client: LemmyClient) {
        when (val state = state) {
            is CommentListState.MoreComments -> loadPage(client, state.nextPageForm)
            is CommentListState.Error -> loadPage(client, state.failedPageForm)
            else -> {}
        }
    }

    private suspend fun loadPage(client: LemmyClient, form: GetCommentsForm) {
        update(CommentListState.Loading)
        when (val newComments = client.getComments(form)) {
            is Result.Error -> update(CommentListState.Error(failedPageForm = form, exception = newComments.error))
            is Result.Success -> update(
                state = when {
                    form.page != null && newComments.result.size < form.page.limit -> CommentListState.Finished
                    newComments.result.isEmpty() -> CommentListState.Finished
                    else -> CommentListState.MoreComments(form.withIncreasedPage())
                },
                comments = comments + newComments.result.toComments(form),
            )
        }
    }

    protected abstract fun List<CommentView>.toComments(form: GetCommentsForm): Map<Long, CT>
}

class RootCommentList(
    comments: Map<Long, CommentTree.Root>,
    state: CommentListState,
) : CommentList<CommentTree.Root>(comments, state) {

    override fun List<CommentView>.toComments(form: GetCommentsForm): Map<Long, CommentTree.Root> {
        return toRoots(form.userPreferences)
    }
}

class InnerCommentList(
    comments: Map<Long, CommentTree.Inner>,
    state: CommentListState,
) : CommentList<CommentTree.Inner>(comments, state) {

    override fun List<CommentView>.toComments(form: GetCommentsForm): Map<Long, CommentTree.Inner> {
        val parentId = (form.context as GetCommentsFormContextualInfo.ParentId).commentId
        return toInnerComments(parentId, form.userPreferences)
    }

    companion object {
        fun emptyLoading() = InnerCommentList(emptyMap(), CommentListState.Loading)
    }
}
