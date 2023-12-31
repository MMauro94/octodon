package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.utils.Result
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.types.shouldBeInstanceOf

class GetPostsTest : FunSpec(
    {

        context("getPosts is able to parse a sample response") {
            withData(
                "get_posts_1",
                "get_posts_2",
            ) { fileName ->
                getMockedLemmyClient(fileName).getPosts().shouldBeInstanceOf<Result.Success<*>>()
            }
        }
    },
)
