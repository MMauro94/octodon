package io.github.mmauro94.common.markdown

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

sealed interface MarkdownElement {
    data class Text(val text: AnnotatedString) : MarkdownElement
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

    private fun commitString() {
        if (currentString.length > 0) {
            stylesStack.forEach { (style, start) ->
                currentString.addStyle(style, start, currentString.length)
            }
            urlAnnotationsStack.forEach { (url, start) ->
                currentString.addUrlAnnotation(url, start, currentString.length)
            }
            elements.add(MarkdownElement.Text(currentString.toAnnotatedString()))
        }
        currentString = AnnotatedString.Builder()
        stylesStack.replaceAll { (style, _) -> style to 0 }
        urlAnnotationsStack.replaceAll { (style, _) -> style to 0 }
    }

    private fun addText(text: String) {
        currentString.append(text)
    }

    @Composable
    fun parse(node: ASTNode, plainText: String) {
        pushStyle(MaterialTheme.typography.bodyMedium.toSpanStyle())
        addNode(node, plainText)
        commitString()
        popStyle()
    }

    @Composable
    private fun addNode(node: ASTNode, plainText: String) {
        @Composable
        fun children() {
            node.children.forEach {
                addNode(it, plainText)
            }
        }

        @Composable
        fun styleChildren(style: SpanStyle) {
            pushStyle(style)
            children()
            popStyle()
        }

        when (node.type) {
            MarkdownElementTypes.IMAGE -> {
                commitString()
                val url = node.childOfType(MarkdownElementTypes.LINK_DESTINATION)?.stringContent(plainText)
                if (url != null) {
                    elements.add(MarkdownElement.Image(url))
                }
            }

            MarkdownTokenTypes.TEXT -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.WHITE_SPACE -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.SINGLE_QUOTE -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.DOUBLE_QUOTE -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.LPAREN -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.RPAREN -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.LBRACKET -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.RBRACKET -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.LT -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.GT -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.COLON -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.EXCLAMATION_MARK -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.HARD_LINE_BREAK -> addText(node.stringContent(plainText))
            MarkdownTokenTypes.EOL -> addText(node.stringContent(plainText))
            MarkdownElementTypes.UNORDERED_LIST -> {
                children()
            }

            MarkdownElementTypes.ORDERED_LIST -> {
                children()
            }

            MarkdownElementTypes.LIST_ITEM -> {
                addText(" - ")
                children()
            }

            MarkdownElementTypes.BLOCK_QUOTE -> {
                addText("> ")
                children()
            }

            MarkdownElementTypes.CODE_FENCE -> {
                commitString()
                val content = node.childOfType(MarkdownTokenTypes.CODE_FENCE_CONTENT)?.stringContent(plainText)
                elements.add(MarkdownElement.CodeBlock(content.orEmpty()))
            }

            MarkdownElementTypes.CODE_BLOCK -> {
                //TODO
                addText("```")
                children()
                addText("```")
            }

            MarkdownElementTypes.CODE_SPAN -> {
                //TODO
                addText("`")
                children()
                addText("`")
            }

            MarkdownElementTypes.HTML_BLOCK -> {
                //TODO
                addText("<html>")
                children()
                addText("</html>")
            }

            MarkdownElementTypes.PARAGRAPH -> {
                children()
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
                val linkTextNode = node.childOfType(MarkdownElementTypes.LINK_TEXT)?.children?.getOrNull(1)
                if (linkTextNode != null) {
                    val destination = node.childOfType(MarkdownElementTypes.LINK_DESTINATION)?.stringContent(plainText)
                    if (destination == null) {
                        addNode(linkTextNode, plainText)
                    } else {
                        pushUrlAnnotation(UrlAnnotation(destination))
                        pushStyle(SpanStyle(color = MaterialTheme.colorScheme.primary))
                        addNode(linkTextNode, plainText)
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

            MarkdownElementTypes.EMPH -> styleChildren(SpanStyle(fontStyle = FontStyle.Italic))
            MarkdownElementTypes.STRONG -> styleChildren(SpanStyle(fontWeight = FontWeight.Bold))
            MarkdownElementTypes.SETEXT_1 -> styleChildren(MaterialTheme.typography.headlineLarge.toSpanStyle())
            MarkdownElementTypes.ATX_1 -> styleChildren(MaterialTheme.typography.headlineLarge.toSpanStyle())
            MarkdownElementTypes.SETEXT_2 -> styleChildren(MaterialTheme.typography.headlineMedium.toSpanStyle())
            MarkdownElementTypes.ATX_2 -> styleChildren(MaterialTheme.typography.headlineMedium.toSpanStyle())
            MarkdownElementTypes.ATX_3 -> styleChildren(MaterialTheme.typography.headlineSmall.toSpanStyle())
            MarkdownElementTypes.ATX_4 -> styleChildren(MaterialTheme.typography.titleLarge.toSpanStyle())
            MarkdownElementTypes.ATX_5 -> styleChildren(MaterialTheme.typography.titleMedium.toSpanStyle())
            MarkdownElementTypes.ATX_6 -> styleChildren(MaterialTheme.typography.titleSmall.toSpanStyle())

            else -> children()
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
