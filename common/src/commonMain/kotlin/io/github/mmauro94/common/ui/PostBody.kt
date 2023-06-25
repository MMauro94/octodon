package io.github.mmauro94.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.mmauro94.common.utils.Material3Markdown

@Composable
fun PostBody(
    body: String,
    maxLines: Int = Int.MAX_VALUE,
) {
    // TODO: improve maxLines logic
    val cropped = remember(body, maxLines) {
        body.split("\n").take(maxLines).joinToString(separator = "\n")
    }
    Material3Markdown(cropped)
}
