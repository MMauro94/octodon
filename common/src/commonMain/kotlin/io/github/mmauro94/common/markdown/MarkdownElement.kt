package io.github.mmauro94.common.markdown

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed interface MarkdownElement {
    data class Text(val text: AnnotatedString, val style: TextStyle) : MarkdownElement
    data class Quote(val content: List<MarkdownElement>) : MarkdownElement
    data class CodeBlock(val text: String) : MarkdownElement
    data class ListItem(val bullet: String, val content: List<MarkdownElement>) : MarkdownElement
    data class Image(val url: String, val title: String?) : MarkdownElement
    object Divider : MarkdownElement
    data class Spacer(val height: Dp = 16.dp) : MarkdownElement
    data class Table(val text: String) : MarkdownElement // TODO
}
