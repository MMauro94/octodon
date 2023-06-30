package io.github.mmauro94.common.markdown

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.image.attributes.ImageAttributesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.CustomBlock
import org.commonmark.node.CustomNode
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.HtmlBlock
import org.commonmark.node.HtmlInline
import org.commonmark.node.Image
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.LinkReferenceDefinition
import org.commonmark.node.ListBlock
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak
import org.commonmark.parser.Parser


sealed interface MarkdownElement {
    data class Text(val text: AnnotatedString, val style: TextStyle) : MarkdownElement
    data class Quote(val content: List<MarkdownElement>) : MarkdownElement
    data class CodeBlock(val text: String) : MarkdownElement
    data class ListItem(val bullet: String, val content: List<MarkdownElement>) : MarkdownElement
    data class Image(val url: String, val title: String?) : MarkdownElement
    object Divider : MarkdownElement
    data class Spacer(val height: Dp = 16.dp) : MarkdownElement
    data class Table(val text: String) : MarkdownElement //TODO
}

@OptIn(ExperimentalTextApi::class)
private class MarkdownElementsBuilder(
    val plainText: String,
    val typography: Typography,
    val codeBackground: Color,
    val linkColor: Color,
    val addDecorativeSpaces: Boolean = true,
) : AbstractVisitor() {

    private val elements = mutableListOf<MarkdownElement>()
    private val stylesStack = mutableListOf<Pair<SpanStyle, Int>>()
    private val urlAnnotationsStack = mutableListOf<Pair<UrlAnnotation, Int>>()
    private var currentString: AnnotatedString.Builder = AnnotatedString.Builder()
    private var currentStyle: TextStyle = typography.bodyMedium

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

    private fun commitString(newStyle: TextStyle = typography.bodyMedium) {
        if (currentString.length > 0) {
            stylesStack.forEach { (style, start) ->
                currentString.addStyle(style, start, currentString.length)
            }
            urlAnnotationsStack.forEach { (url, start) ->
                currentString.addUrlAnnotation(url, start, currentString.length)
            }
            elements.add(
                MarkdownElement.Text(currentString.toAnnotatedString(), currentStyle),
            )
        }
        currentString = AnnotatedString.Builder()
        currentStyle = newStyle
        stylesStack.replaceAll { (style, _) -> style to 0 }
        urlAnnotationsStack.replaceAll { (style, _) -> style to 0 }
    }

    private fun addText(text: String) {
        currentString.append(text)
    }

    private fun ensureWhitespace() {
        // TODO: only if there's not already a whitespace
        addText(" ")
    }

    private fun addElement(element: MarkdownElement) {
        commitString()
        elements.add(element)
    }

    fun parse(node: Node, andNext: Boolean = true): List<MarkdownElement> {
        var n: Node? = node
        while (n != null) {
            n.accept(this)
            n = n.next
            if (!andNext) break
        }
        commitString()
        return elements
            .dropLastWhile { it is MarkdownElement.Spacer }
            .dropWhile { it is MarkdownElement.Spacer }
    }

    private fun nestedParsing(
        node: Node,
        andNext: Boolean = true,
        addDecorativeSpaces: Boolean = this.addDecorativeSpaces,
    ): List<MarkdownElement> {
        return MarkdownElementsBuilder(plainText, typography, codeBackground, linkColor, addDecorativeSpaces)
            .parse(node, andNext = andNext)
    }

    override fun visit(blockQuote: BlockQuote) {
        addElement(MarkdownElement.Quote(nestedParsing(blockQuote.firstChild)))
        if (addDecorativeSpaces) {
            addElement(MarkdownElement.Spacer())
        }
    }

    override fun visit(code: Code) {
        pushStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = codeBackground))
        addText(code.literal)
        popStyle()
    }

    override fun visit(emphasis: Emphasis) {
        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
        super.visit(emphasis)
        popStyle()
    }

    override fun visit(fencedCodeBlock: FencedCodeBlock) {
        addElement(MarkdownElement.CodeBlock(fencedCodeBlock.literal.removeSuffix("\n")))
        addElement(MarkdownElement.Spacer())
    }

    override fun visit(hardLineBreak: HardLineBreak) {
        commitString()
    }

    override fun visit(heading: Heading) {
        addElement(
            MarkdownElement.Spacer(
                when (heading.level) {
                    1 -> 16.dp
                    2 -> 8.dp
                    else -> 4.dp
                },
            ),
        )
        val style = when (heading.level) {
            1 -> typography.headlineLarge
            2 -> typography.headlineMedium
            3 -> typography.headlineSmall
            4 -> typography.titleLarge
            5 -> typography.titleMedium
            6 -> typography.titleSmall
            7 -> typography.bodyLarge
            else -> typography.bodyMedium
        }
        commitString(style)
        super.visit(heading)
        addElement(
            MarkdownElement.Spacer(
                when (heading.level) {
                    1 -> 8.dp
                    2 -> 4.dp
                    else -> 2.dp
                },
            ),
        )
    }

    override fun visit(thematicBreak: ThematicBreak) {
        addElement(MarkdownElement.Divider)
    }

    override fun visit(htmlInline: HtmlInline) {
        super.visit(htmlInline)
    }

    override fun visit(htmlBlock: HtmlBlock) {
        super.visit(htmlBlock)
    }

    override fun visit(image: Image) {
        addElement(MarkdownElement.Image(image.destination, image.title))
    }

    override fun visit(indentedCodeBlock: IndentedCodeBlock) {
        addElement(MarkdownElement.CodeBlock(indentedCodeBlock.literal.removeSuffix("\n")))
        addElement(MarkdownElement.Spacer())
    }

    override fun visit(link: Link) {
        pushStyle(SpanStyle(color = linkColor))
        pushUrlAnnotation(UrlAnnotation(link.destination))
        super.visit(link)
        popUrlAnnotation()
        popStyle()
    }

    override fun visit(bulletList: BulletList) {
        renderList(bulletList) { "• " }
    }

    override fun visit(orderedList: OrderedList) {
        renderList(orderedList) { n ->
            "${n + orderedList.startNumber - 1}. "
        }
    }

    private fun renderList(block: ListBlock, prefix: (Int) -> String) {
        var n: Node? = block.firstChild
        var index = 1
        while (n != null) {
            val children = nestedParsing(n, andNext = false, addDecorativeSpaces = addDecorativeSpaces && !block.isTight)
            addElement(
                MarkdownElement.ListItem(
                    prefix(index++),
                    children,
                ),
            )
            n = n.next
        }
        if (addDecorativeSpaces) {
            addElement(MarkdownElement.Spacer())
        }
    }

    override fun visit(paragraph: Paragraph) {
        super.visit(paragraph)
        if (addDecorativeSpaces) {
            addElement(MarkdownElement.Spacer())
        } else {
            commitString()
        }
    }

    override fun visit(softLineBreak: SoftLineBreak) {
        ensureWhitespace()
    }

    override fun visit(strongEmphasis: StrongEmphasis) {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        super.visit(strongEmphasis)
        popStyle()
    }

    override fun visit(text: Text) {
        addText(text.literal)
    }

    override fun visit(linkReferenceDefinition: LinkReferenceDefinition) {
    }

    override fun visit(customBlock: CustomBlock) {
        super.visit(customBlock)
        addElement(MarkdownElement.Spacer())
    }

    override fun visit(customNode: CustomNode) {
        super.visit(customNode)
    }
}

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

@Composable
fun parse(markdown: String): List<MarkdownElement> {
    val node = parser.parse(markdown)
    return MarkdownElementsBuilder(
        markdown,
        typography = MaterialTheme.typography,
        codeBackground = codeBackgroundColor(),
        linkColor = MaterialTheme.colorScheme.primary,
    ).parse(node)
}
