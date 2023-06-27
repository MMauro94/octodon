package io.github.mmauro94.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@Composable
fun <I, R, E> composeWorker(
    process: suspend (I) -> Result<R, E>,
    onSuccess: (I, R) -> Unit = { _, _ -> },
    onError: (I, E) -> Unit = { _, _ -> },
): Pair<AsyncState<R, E>, Channel<WorkerMessage<I>>> {
    var state by remember { mutableStateOf<AsyncState<R, E>>(AsyncState.Resting) }
    val channel = remember { Channel<WorkerMessage<I>>(Channel.CONFLATED) }

    LaunchedEffect(Unit) {
        var job: Job? = null
        for (message in channel) {
            job?.cancel()

            if (message is WorkerMessage.Process) {
                state = AsyncState.Loading
                job = launch {
                    when (val result = process(message.input)) {
                        is Result.Error -> {
                            state = AsyncState.Error(result.error)
                            onError(message.input, result.error)
                        }
                        is Result.Success -> {
                            state = AsyncState.Success(result.result)
                            onSuccess(message.input, result.result)
                        }
                    }
                }
            } else {
                state = AsyncState.Resting
            }
        }
    }
    return state to channel
}

sealed interface AsyncState<out O, out E> {
    object Resting : AsyncState<Nothing, Nothing>
    object Loading : AsyncState<Nothing, Nothing>
    data class Success<O>(val result: O) : AsyncState<O, Nothing>
    data class Error<E>(val error: E) : AsyncState<Nothing, E>
}

sealed interface WorkerMessage<in I> {
    object Stop : WorkerMessage<Any?>
    data class Process<I>(val input: I) : WorkerMessage<I>
}
