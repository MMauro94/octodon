package io.github.mmauro94.common.markdown

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

sealed interface MarkdownElement {
    data class Text(val text: AnnotatedString, val style: TextStyle) : MarkdownElement
    data class Quote(val text: AnnotatedString) : MarkdownElement
    data class CodeBlock(val text: String) : MarkdownElement
    data class Image(val url: String) : MarkdownElement
    object Divider : MarkdownElement
    data class Table(val text: String) : MarkdownElement //TODO
}

@OptIn(ExperimentalTextApi::class)
private class MarkdownElementsBuilder {

    val elements = mutableListOf<MarkdownElement>()
    val stylesStack = mutableListOf<Pair<SpanStyle, Int>>()
    val urlAnnotationsStack = mutableListOf<Pair<UrlAnnotation, Int>>()
    var currentString: AnnotatedString.Builder = AnnotatedString.Builder()
    var skipNextNewLine = false

    private sealed interface TextMode {
        data class Normal(val style: TextStyle? = null) : TextMode
        object Quote : TextMode
    }

    private var currentMode: TextMode = TextMode.Normal()

    private fun pushStyle(style: SpanStyle) {
        stylesStack.add(style to currentString.length)
    }

    private fun popStyle() {
        val (style, actualStart) = stylesStack.removeLast()
        currentString.addStyle(style, actualStart, currentString.length)
    }

    private fun pushUrlAnnotation(annotation: UrlAnnotation) {
        urlAnnotationsStack.add(annotation to currentString.length)
    }

    private fun popUrlAnnotation() {
        val (style, actualStart) = urlAnnotationsStack.removeLast()
        currentString.addUrlAnnotation(style, actualStart, currentString.length)
    }

    @Composable
    private fun commitString(newMode: TextMode) {
        if (currentString.length > 0) {
            stylesStack.forEach { (style, start) ->
                currentString.addStyle(style, start, currentString.length)
            }
            urlAnnotationsStack.forEach { (url, start) ->
                currentString.addUrlAnnotation(url, start, currentString.length)
            }
            elements.add(
                when (val mode = currentMode) {
                    is TextMode.Normal -> MarkdownElement.Text(
                        currentString.toAnnotatedString(),
                        mode.style ?: MaterialTheme.typography.bodyMedium,
                    )

                    TextMode.Quote -> MarkdownElement.Quote(currentString.toAnnotatedString())
                },
            )
        }
        skipNextNewLine = true
        currentString = AnnotatedString.Builder()
        currentMode = newMode
        stylesStack.replaceAll { (style, _) -> style to 0 }
        urlAnnotationsStack.replaceAll { (style, _) -> style to 0 }
    }

    private fun addText(text: String) {
        if (!skipNextNewLine || text != "\n") {
            currentString.append(text)
        }
        skipNextNewLine = false
    }

    @Composable
    fun parse(node: ASTNode, plainText: String) {
        addNode(node, plainText)
        commitString(TextMode.Normal())
    }

    @Composable
    private fun addNode(node: ASTNode, plainText: String) {
        @Composable
        fun children(children: List<ASTNode>) {
            children.forEach {
                addNode(it, plainText)
            }
        }

        @Composable
        fun inlineStyleChildren(style: SpanStyle, children: List<ASTNode>) {
            pushStyle(style)
            children(children)
            popStyle()
        }

        @Composable
        fun styleChildren(style: TextStyle, children: List<ASTNode>) {
            commitString(TextMode.Normal(style))
            children(children)
            commitString(TextMode.Normal())
        }

        @Composable
        fun styleATX(style: TextStyle) {
            styleChildren(style, node.childOfType(MarkdownTokenTypes.ATX_CONTENT)?.children?.trim() ?: node.children)
        }

        @Composable
        fun list(prefix: (n: Int) -> String) {
            var n = 1
            node.children.forEach { child ->
                if (child.type == MarkdownElementTypes.LIST_ITEM) {
                    addText(prefix(n++))
                    children(child.children.drop(1))
                } else addNode(child, plainText)
            }
        }

        when (node.type) {
            MarkdownTokenTypes.TEXT -> {
                addText(node.stringContent(plainText).replace("\n", " "))
            }

            MarkdownElementTypes.IMAGE -> {
                commitString(TextMode.Normal())
                val url = node.childOfType(MarkdownElementTypes.LINK_DESTINATION)?.stringContent(plainText)
                if (url != null) {
                    elements.add(MarkdownElement.Image(url))
                }
            }

            MarkdownElementTypes.UNORDERED_LIST -> {
                list { "• " }
            }

            MarkdownElementTypes.ORDERED_LIST -> {
                list { "$it. " }
            }

            MarkdownElementTypes.BLOCK_QUOTE -> {
                commitString(TextMode.Quote)
                children(node.children.trim(MarkdownTokenTypes.BLOCK_QUOTE))
                commitString(TextMode.Normal())
            }

            MarkdownElementTypes.CODE_FENCE -> {
                commitString(TextMode.Normal())
                val content = node.childOfType(MarkdownTokenTypes.CODE_FENCE_CONTENT)?.stringContent(plainText)
                elements.add(MarkdownElement.CodeBlock(content.orEmpty()))
            }

            MarkdownElementTypes.CODE_BLOCK -> {
                //TODO
                addText("```")
                children(node.children)
                addText("```")
            }

            MarkdownElementTypes.CODE_SPAN -> {
                //TODO
                addText("`")
                children(node.children)
                addText("`")
            }

            MarkdownElementTypes.HTML_BLOCK -> {
                //TODO
                addText("<html>")
                children(node.children)
                addText("</html>")
            }

            MarkdownElementTypes.PARAGRAPH -> {
                children(node.children)
            }

            MarkdownElementTypes.LINK_DEFINITION -> {
                val linkLabel = node.childOfType(MarkdownElementTypes.LINK_LABEL)?.stringContent(plainText)
                if (linkLabel != null) {
                    val destination = node.childOfType(MarkdownElementTypes.LINK_DESTINATION)?.stringContent(plainText)
                    if (destination == null) {
                        addText(linkLabel)
                    } else {
                        pushUrlAnnotation(UrlAnnotation(destination))
                        addText(linkLabel)
                        popUrlAnnotation()
                    }
                }
            }

            MarkdownElementTypes.INLINE_LINK -> {
                val linkTextNode = node.childOfType(MarkdownElementTypes.LINK_TEXT)?.children.orEmpty().dropLast(1).drop(1)
                if (linkTextNode.isNotEmpty()) {
                    val destination = node.childOfType(MarkdownElementTypes.LINK_DESTINATION)?.stringContent(plainText)
                    if (destination == null) {
                        linkTextNode.forEach { addNode(it, plainText) }
                    } else {
                        pushUrlAnnotation(UrlAnnotation(destination))
                        pushStyle(SpanStyle(color = MaterialTheme.colorScheme.primary))
                        linkTextNode.forEach { addNode(it, plainText) }
                        popStyle()
                        popUrlAnnotation()
                    }
                }
            }

            MarkdownElementTypes.AUTOLINK -> {
                val link = node.stringContent(plainText)
                pushUrlAnnotation(UrlAnnotation(link))
                pushStyle(SpanStyle(color = MaterialTheme.colorScheme.primary))
                addText(link)
                popStyle()
                popUrlAnnotation()
            }

            MarkdownElementTypes.EMPH -> inlineStyleChildren(
                SpanStyle(fontStyle = FontStyle.Italic),
                node.children.trim(MarkdownTokenTypes.EMPH),
            )

            MarkdownElementTypes.STRONG -> inlineStyleChildren(
                SpanStyle(fontWeight = FontWeight.Bold),
                node.children.trim(MarkdownTokenTypes.EMPH),
            )

            MarkdownElementTypes.SETEXT_1 -> styleChildren(MaterialTheme.typography.headlineLarge, node.children)
            MarkdownElementTypes.ATX_1 -> styleATX(MaterialTheme.typography.headlineLarge)
            MarkdownElementTypes.SETEXT_2 -> styleChildren(MaterialTheme.typography.headlineMedium, node.children)
            MarkdownElementTypes.ATX_2 -> styleATX(MaterialTheme.typography.headlineMedium)
            MarkdownElementTypes.ATX_3 -> styleATX(MaterialTheme.typography.headlineSmall)
            MarkdownElementTypes.ATX_4 -> styleATX(MaterialTheme.typography.titleLarge)
            MarkdownElementTypes.ATX_5 -> styleATX(MaterialTheme.typography.titleMedium)
            MarkdownElementTypes.ATX_6 -> styleATX(MaterialTheme.typography.titleSmall)

            else -> {
                if (node.children.isEmpty()) {
                    addText(node.stringContent(plainText))
                } else {
                    children(node.children)
                }
            }
        }
    }
}

private val parser = MarkdownParser(CommonMarkFlavourDescriptor())

@Composable
fun parse(markdown: String): List<MarkdownElement> {
    val node = parser.buildMarkdownTreeFromString(markdown)
    return MarkdownElementsBuilder().apply {
        parse(node, markdown)
    }.elements
}
