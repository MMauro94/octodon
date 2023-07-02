package io.github.mmauro94.common.client

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.mmauro94.common.serializers.InstantSerializer
import io.github.mmauro94.common.serializers.UrlSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

/**
 * For list of types [https://join-lemmy.org/api/modules.html](https://join-lemmy.org/api/modules.html)
 */
class LemmyClient(
    val url: String,
    private val token: String? = null,
    engineFactory: HttpClientEngineFactory<*> = OkHttp,
) {

    constructor(url: String, token: String?, engine: HttpClientEngine) : this(
        url = url,
        token = token,
        engineFactory = object : HttpClientEngineFactory<HttpClientEngineConfig> {
            override fun create(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine {
                return engine
            }
        },
    )

    @OptIn(ExperimentalSerializationApi::class)
    val ktorClient by lazy {
        HttpClient(engineFactory) {
            expectSuccess = true
            defaultRequest {
                url(this@LemmyClient.url.removeSuffix("/") + "/api/v3/")
                contentType(ContentType.Application.Json)
            }
            install(ContentEncoding) {
                deflate(1.0f)
                gzip(0.9f)
            }
            install(Auth) {
                if (token != null) {
                    lemmyAuth(token)
                }
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        encodeDefaults = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                        coerceInputValues = true

                        serializersModule = SerializersModule {
                            contextual(InstantSerializer)
                            contextual(UrlSerializer)
                        }
                    },
                )
            }
        }
    }
}

val LocalLemmyClient = staticCompositionLocalOf<LemmyClient> {
    error("LocalLemmyClient must be explicitly initialized")
}
