package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.client.ApiResult
import io.github.mmauro94.common.client.LemmyClient
import io.github.mmauro94.common.client.LemmyErrorHandler
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

suspend fun LemmyClient.login(
    usernameOrEmail: String,
    password: String,
    totp2faToken: String? = null,
): ApiResult<LoginResponse> {
    return ApiResult(ErrorHandler) {
        ktorClient.post("user/login") {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequestBody(
                    usernameOrEmail = usernameOrEmail,
                    password = password,
                    totp2faToken = totp2faToken,
                ),
            )
        }.body<LoginResponse.Successful>()
    }
}

private val ErrorHandler: LemmyErrorHandler<LoginResponse> = { error, response ->
    if (response.status in listOf(HttpStatusCode.NotFound, HttpStatusCode.BadRequest)) {
        when (error.error) {
            "couldnt_find_that_username_or_email" -> LoginResponse.CouldntFindUsernameOrEmail
            "password_incorrect" -> LoginResponse.PasswordIncorrect
            else -> null
        }
    } else {
        null
    }
}

sealed interface LoginResponse {

    @Serializable
    data class Successful(
        val jwt: String?,
        @SerialName("registration_created") val registrationCreated: Boolean,
        @SerialName("verify_email_sent") val verifyEmailSent: Boolean,
    ) : LoginResponse

    object PasswordIncorrect : LoginResponse

    object CouldntFindUsernameOrEmail : LoginResponse
}

@Serializable
private data class LoginRequestBody(
    @SerialName("username_or_email") val usernameOrEmail: String,
    val password: String,
    @SerialName("totp_2fa_token") val totp2faToken: String?,
)
