package io.github.mmauro94.common.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

data class LemmyClient(
    val url: String,
) {

    val ktorClient by lazy {
        HttpClient(OkHttp) {
            expectSuccess = true
            defaultRequest {
                url(this@LemmyClient.url + "api/v3/")
            }
            install(ContentEncoding) {
                deflate(1.0f)
                gzip(0.9f)
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        encodeDefaults = true
                        ignoreUnknownKeys = true
                    },
                )
            }
        }
    }
}
