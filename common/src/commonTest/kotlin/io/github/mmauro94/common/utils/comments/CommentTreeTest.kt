package io.github.mmauro94.common.utils.comments

import io.github.mmauro94.common.client.api.getComments
import io.github.mmauro94.common.client.api.getMockedLemmyClient
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.utils.Result
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class CommentTreeTest : FunSpec(
    {

        context("toPostComments works") {
            withData(
                "get_post_comments_1",
                "get_post_comments_1",
                "get_post_comments_1",
            ) { fileName ->
                val comments = getMockedLemmyClient(fileName).getComments()
                check(comments is Result.Success)

                val postView = mockk<PostView>()
                val commentTree = comments.result.toPostComments(postView)

                commentTree.postView shouldBe postView
            }
        }
    },
)
