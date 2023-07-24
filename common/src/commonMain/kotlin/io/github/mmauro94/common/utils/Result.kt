package io.github.mmauro94.common.utils

sealed interface Result<out R, out E> {
    data class Error<E>(val error: E) : Result<Nothing, E>
    data class Success<R>(val result: R) : Result<R, Nothing>
}

inline fun <R, T> Result.Success<R>.map(mapFn: (R) -> T): Result.Success<T> = Result.Success(mapFn(result))

inline fun <E, T> Result.Error<E>.map(mapFn: (E) -> T) = Result.Error(mapFn(error))

inline fun <R, T, E> Result<R, E>.mapSuccess(mapFn: (R) -> T): Result<T, E> {
    return when (this) {
        is Result.Error -> this
        is Result.Success -> map(mapFn)
    }
}

inline fun <R, E> Result<R, E>.getOr(block: (error: Result.Error<E>) -> Nothing): R {
    return when (this) {
        is Result.Error -> block(this)
        is Result.Success -> result
    }
}
