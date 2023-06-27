package io.github.mmauro94.common.ui.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.utils.AsyncState
import io.github.mmauro94.common.utils.WorkerMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@Composable
internal fun LoginStepContainer(
    modifier: Modifier,
    stepIcon: ImageVector,
    stepTitle: String,
    stepSubtitle: String? = null,
    stepDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 640.dp)
                .fillMaxHeight()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))
            Spacer(Modifier.weight(1f))
            Icon(stepIcon, null, Modifier.size(112.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                stepTitle,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
            if (stepSubtitle != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    stepSubtitle,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(Modifier.height(32.dp))
            if (stepDescription != null) {
                HighlightedElement(border = true) {
                    Text(stepDescription, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(32.dp))
            }
            content()
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
internal fun HighlightedElement(
    modifier: Modifier = Modifier,
    border: Boolean,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        border = if (border) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
            content()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun <T> ActionButton(
    action: String,
    inputs: () -> T,
    workerChannel: Channel<WorkerMessage<T>>,
    state: AsyncState<*, StringResource>,
) {
    val cs = rememberCoroutineScope()
    AnimatedContent(
        state,
        transitionSpec = { fadeIn() with fadeOut() using SizeTransform() },
    ) { ws ->
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            when (ws) {
                AsyncState.Loading -> CircularProgressIndicator()
                is AsyncState.Error -> {
                    Button({ cs.launch { workerChannel.send(WorkerMessage.Process(inputs())) } }) {
                        Text(stringResource(MR.strings.retry))
                    }
                }

                else -> {
                    Button({ cs.launch { workerChannel.send(WorkerMessage.Process(inputs())) } }) {
                        Text(action)
                    }
                }
            }
        }
    }
}
