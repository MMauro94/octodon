package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

suspend fun LemmyClient.login(
    usernameOrEmail: String,
    password: String,
    totp2faToken: String? = null,
): ApiResult<LoginResponse> {
    return ApiResult {
        ktorClient.post("user/login") {
            setBody(
                LoginRequestBody(
                    usernameOrEmail = usernameOrEmail,
                    password = password,
                    totp2faToken = totp2faToken,
                ),
            )
        }.body<LoginResponse>()
    }
}

@Serializable
data class LoginResponse(
    val jwt: String?,
    @SerialName("registration_created") val registrationCreated: Boolean,
    @SerialName("verify_email_sent") val verifyEmailSent: Boolean,
)

@Serializable
private data class LoginRequestBody(
    @SerialName("username_or_email") val usernameOrEmail: String,
    val password: String,
    @SerialName("totp_2fa_token") val totp2faToken: String?,
)
