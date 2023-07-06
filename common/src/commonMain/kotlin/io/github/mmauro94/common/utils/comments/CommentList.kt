package io.github.mmauro94.common.utils.comments

import io.github.mmauro94.common.utils.Result

data class CommentList<CT : CommentTree>(
    val comments: Map<Long, CT>,
    val state: Result<CommentListState, Exception> = Result.Success(CommentListState.MORE_COMMENTS),
)
