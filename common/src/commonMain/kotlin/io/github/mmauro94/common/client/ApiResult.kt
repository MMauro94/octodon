package io.github.mmauro94.common.client

sealed interface ApiResult<out R> {

    data class Error(val exception: Exception) : ApiResult<Nothing>

    data class Success<R>(val result: R) : ApiResult<R>
}

@Suppress("FunctionNaming", "TooGenericExceptionCaught")
inline fun <R> ApiResult(block: () -> R): ApiResult<R> {
    return try {
        ApiResult.Success(block())
    } catch (e: Exception) {
        ApiResult.Error(e)
    }
}
