package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel

class GetPostsTest : FunSpec(
    {

        context("test posts api doesn't throw") {
            withData(
                "get_posts_1",
                "get_posts_2",
            ) { fileName ->
                val postsJson = javaClass.getResource("/api_examples/$fileName.json")!!.readText()
                val mockEngine = MockEngine {
                    respond(
                        content = ByteReadChannel(postsJson),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
                val client = LemmyClient("", mockEngine)

                client.getPosts().shouldBeInstanceOf<ApiResult.Success<*>>()
            }
        }
    },
)
