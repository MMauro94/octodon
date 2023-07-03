package io.github.mmauro94.common.markdown

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktor.http.Url
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.image.attributes.ImageAttributesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser

@Immutable
data class Markdown(val elements: List<MarkdownElement>) {
    fun isEmpty() = elements.isEmpty()
    fun isBlank() = elements.all { it is MarkdownElement.Spacer }

    companion object {

        private val extensions: List<Extension> = listOf(
            TablesExtension.create(),
            AutolinkExtension.create(),
            StrikethroughExtension.create(),
            ImageAttributesExtension.create(),
            TaskListItemsExtension.create(),
        )
        private val parser: Parser = Parser.builder()
            .extensions(extensions)
            .build()

        fun of(markdown: String): Markdown {
            val node = parser.parse(markdown)
            return Markdown(MarkdownParser(markdown).parse(node))
        }
    }
}

@Immutable
sealed interface MarkdownElement {
    data class Text(val text: LazyAnnotatedString, val style: MarkdownTextType) : MarkdownElement
    data class Quote(val content: List<MarkdownElement>) : MarkdownElement
    data class CodeBlock(val text: String) : MarkdownElement
    data class ListItem(val bullet: String, val content: List<MarkdownElement>) : MarkdownElement
    data class Image(val url: Url, val title: String?) : MarkdownElement
    object Divider : MarkdownElement
    data class Spacer(val height: Dp = 16.dp) : MarkdownElement
    data class Table(val text: String) : MarkdownElement // TODO
}

enum class MarkdownTextType(val toTextStyle: @Composable () -> TextStyle) {
    Body({ MaterialTheme.typography.bodyMedium }),
    H1({ MaterialTheme.typography.headlineLarge }),
    H2({ MaterialTheme.typography.headlineMedium }),
    H3({ MaterialTheme.typography.headlineSmall }),
    H4({ MaterialTheme.typography.titleLarge }),
    H5({ MaterialTheme.typography.titleMedium }),
    H6({ MaterialTheme.typography.titleSmall }),
    H7({ MaterialTheme.typography.bodyLarge }),
}
