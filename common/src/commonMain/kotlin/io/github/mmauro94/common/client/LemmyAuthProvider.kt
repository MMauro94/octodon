package io.github.mmauro94.common.client

import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.AuthProvider
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.auth.HttpAuthHeader

class LemmyAuthProvider(private val token: String) : AuthProvider {

    @Deprecated("Please use sendWithoutRequest function instead")
    override val sendWithoutRequest = true

    override suspend fun addRequestHeaders(request: HttpRequestBuilder, authHeader: HttpAuthHeader?) {
        when (request.method) {
            HttpMethod.Get -> request.parameter("auth", token)
        }
    }

    override fun isApplicable(auth: HttpAuthHeader): Boolean {
        return true
    }

    override suspend fun refreshToken(response: HttpResponse): Boolean {
        // Tokens cannot be refreshed, they have infinite validity
        return false
    }
}

fun Auth.lemmyAuth(token: String) {
    providers.add(LemmyAuthProvider(token))
}
