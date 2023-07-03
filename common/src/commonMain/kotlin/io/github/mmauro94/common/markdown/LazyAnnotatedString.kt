package io.github.mmauro94.common.markdown

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.font.FontFamily

data class LazyAnnotatedString(
    val baseString: AnnotatedString,
    val links: List<IntRange>,
    val codeBlocks: List<IntRange>,
) {
    @Composable
    fun toAnnotatedString(): AnnotatedString {
        return AnnotatedString.Builder(baseString.length).apply {
            append(baseString)
            links.forEach {
                addStyle(SpanStyle(color = MaterialTheme.colorScheme.primary), it.first, it.last + 1)
            }
            codeBlocks.forEach {
                addStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = codeBackgroundColor()), it.first, it.last + 1)
            }
        }.toAnnotatedString()
    }

    @OptIn(ExperimentalTextApi::class)
    class Builder {

        private val strBuilder = AnnotatedString.Builder()
        private val links = mutableListOf<IntRange>()
        private val codeBlocks = mutableListOf<IntRange>()
        val length get() = strBuilder.length
        fun addStyle(style: SpanStyle, start: Int, end: Int) {
            strBuilder.addStyle(style, start, end)
        }

        fun addUrlAnnotation(urlAnnotation: UrlAnnotation, start: Int, end: Int) {
            strBuilder.addUrlAnnotation(urlAnnotation, start, end)
        }

        fun append(text: String) {
            strBuilder.append(text)
        }

        fun addLink(start: Int, end: Int) {
            links.add(start until end)
        }

        fun addCodeBlock(start: Int, end: Int) {
            codeBlocks.add(start until end)
        }

        fun build(): LazyAnnotatedString {
            return LazyAnnotatedString(
                baseString = strBuilder.toAnnotatedString(),
                links = links,
                codeBlocks = codeBlocks,
            )
        }
    }
}
