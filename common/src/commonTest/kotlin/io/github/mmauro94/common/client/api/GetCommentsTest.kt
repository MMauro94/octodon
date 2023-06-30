package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.utils.Result
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.types.shouldBeInstanceOf

class GetCommentsTest : FunSpec(
    {

        context("getPosts is able to parse a sample response") {
            withData(
                "get_comments_1",
                "get_comments_2",
                "get_comments_3",
            ) { fileName ->
                getMockedLemmyClient(fileName).getComments().shouldBeInstanceOf<Result.Success<*>>()
            }
        }
    },
)
