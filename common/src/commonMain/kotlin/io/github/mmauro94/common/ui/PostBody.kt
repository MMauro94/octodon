package io.github.mmauro94.common.ui

import androidx.compose.runtime.Composable
import io.github.mmauro94.common.markdown.ReadOnlyMarkdown

@Composable
fun PostBody(
    body: String,
    maxLines: Int = Int.MAX_VALUE,
) {
    // TODO: improve maxLines logic
//    val cropped = remember(body, maxLines) {
//        body.split("\n").take(maxLines).joinToString(separator = "\n")
//    }
//    Material3Markdown(cropped)
    ReadOnlyMarkdown(body)
}
