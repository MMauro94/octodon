package io.github.mmauro94.common.client

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.HttpResponse

sealed interface ApiResult<out R> {

    data class Error(val exception: Exception) : ApiResult<Nothing>

    data class Success<R>(val result: R) : ApiResult<R>
}

typealias LemmyErrorHandler<R> = (error: LemmyError, response: HttpResponse) -> R?

@Suppress("FunctionNaming", "TooGenericExceptionCaught")
suspend inline fun <R : Any> ApiResult(
    errorHandler: LemmyErrorHandler<R> = { _, _ -> null },
    block: () -> R,
): ApiResult<R> {
    return try {
        ApiResult.Success(block())
    } catch (e: ClientRequestException) {
        handleError(e, errorHandler)
    } catch (e: Exception) {
        ApiResult.Error(e)
    }
}

suspend inline fun <R : Any> handleError(e: ClientRequestException, errorHandler: LemmyErrorHandler<R>): ApiResult<R> {
    val error = try {
        e.response.body<LemmyError>()
    } catch (ignored: Exception) {
        null
    }
    if (error != null) {
        val result = errorHandler(error, e.response)
        if (result != null) {
            return ApiResult.Success(result)
        }
    }
    return ApiResult.Error(e)
}
