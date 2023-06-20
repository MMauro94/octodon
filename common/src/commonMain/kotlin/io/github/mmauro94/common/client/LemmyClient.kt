package io.github.mmauro94.common.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class LemmyClient(
    val url: String,
    engineFactory: HttpClientEngineFactory<*> = OkHttp,
) {

    constructor(url: String, engine: HttpClientEngine) : this(
        url = url,
        engineFactory = object : HttpClientEngineFactory<HttpClientEngineConfig> {
            override fun create(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine {
                return engine
            }
        },
    )

    val ktorClient by lazy {
        HttpClient(engineFactory) {
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
