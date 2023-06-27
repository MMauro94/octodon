package io.github.mmauro94.common.utils

sealed interface Result<out R, out E> {
    data class Error<E>(val error: E) : Result<Nothing, E>
    data class Success<R>(val result: R) : Result<R, Nothing>
}
