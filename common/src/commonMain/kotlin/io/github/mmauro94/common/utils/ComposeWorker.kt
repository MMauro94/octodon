package io.github.mmauro94.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

sealed interface WorkerState<out I, out O> {
    object Resting : WorkerState<Nothing, Nothing>
    object Loading : WorkerState<Nothing, Nothing>
    data class Done<I, O>(val input: I, val result: O) : WorkerState<I, O>
}

sealed interface WorkerMessage<in I> {
    object Stop : WorkerMessage<Any?>
    data class Process<I>(val input: I) : WorkerMessage<I>
}

@Composable
fun <I, O> composeWorker(
    process: suspend (I) -> O,
    onStateChange: (WorkerState<I, O>) -> Unit,
): Channel<WorkerMessage<I>> {
    val channel = remember { Channel<WorkerMessage<I>>(Channel.CONFLATED) }

    LaunchedEffect(Unit) {
        var job: Job? = null
        for (message in channel) {
            job?.cancel()

            when (message) {
                is WorkerMessage.Process -> {
                    onStateChange(WorkerState.Loading)
                    job = launch {
                        val result = process(message.input)
                        launch(Dispatchers.Main) {
                            onStateChange(WorkerState.Done(message.input, result))
                        }
                    }
                }

                WorkerMessage.Stop -> {
                    onStateChange(WorkerState.Resting)
                }
            }
        }
    }

    return channel
}
