package io.github.mmauro94.common.utils.comments

import io.github.mmauro94.common.client.entities.GetCommentsForm

sealed class CommentListState {

    data class MoreComments(val nextPageForm: GetCommentsForm) : CommentListState()

    data class Error(val failedPageForm: GetCommentsForm, val exception: Exception) : CommentListState()

    object Finished : CommentListState()
    object Loading : CommentListState()
}
