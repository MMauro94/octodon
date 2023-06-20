package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.client.LemmyClient
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel

class GetPostsTest : FunSpec(
    {

        test("test posts api doesn't throw") {
            val postsJson = javaClass.getResource("/api_examples/get_posts.json")!!.readText()
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel(postsJson),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
            val client = LemmyClient("", mockEngine)

            client.getPosts()
        }
    },
)
