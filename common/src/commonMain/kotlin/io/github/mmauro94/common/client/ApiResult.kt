package io.github.mmauro94.common.client

import io.github.mmauro94.common.utils.Result
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.HttpResponse

typealias ApiResult<R> = Result<R, Exception>

typealias LemmyErrorHandler<R> = (error: LemmyError, response: HttpResponse) -> R?

@Suppress("FunctionNaming", "TooGenericExceptionCaught")
suspend inline fun <R : Any> ApiResult(
    errorHandler: LemmyErrorHandler<R> = { _, _ -> null },
    block: () -> R,
): ApiResult<R> {
    return try {
        Result.Success(block())
    } catch (e: ClientRequestException) {
        handleError(e, errorHandler)
    } catch (e: Exception) {
        Result.Error(e)
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
            return Result.Success(result)
        }
    }
    return Result.Error(e)
}
