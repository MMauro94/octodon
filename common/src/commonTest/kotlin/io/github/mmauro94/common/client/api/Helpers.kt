package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.client.LemmyClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel

fun Any.getMockedLemmyClient(fileName: String): LemmyClient {
    val postsJson = javaClass.getResource("/api_examples/$fileName.json")!!.readText()
    val mockEngine = MockEngine {
        respond(
            content = ByteReadChannel(postsJson),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }
    return LemmyClient("", null, mockEngine)
}
