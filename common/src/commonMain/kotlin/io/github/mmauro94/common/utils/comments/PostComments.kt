package io.github.mmauro94.common.utils.comments

import io.github.mmauro94.common.client.entities.PostView

data class PostComments(
    val postView: PostView,
    val commentList: CommentList<CommentTree.Root>,
)
