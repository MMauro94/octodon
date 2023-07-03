package io.github.mmauro94.common.markdown

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ktor.http.URLParserException
import io.ktor.http.Url
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

private typealias StartIndex = Int

@Suppress("TooManyFunctions")
@OptIn(ExperimentalTextApi::class)
class MarkdownParser(
    val plainText: String,
    val addDecorativeSpaces: Boolean = true,
) : AbstractVisitor() {

    private val elements = mutableListOf<MarkdownElement>()
    private val linksStack = mutableListOf<StartIndex>()
    private val codeBlocksStack = mutableListOf<StartIndex>()
    private val stylesStack = mutableListOf<Pair<SpanStyle, StartIndex>>()
    private val urlAnnotationsStack = mutableListOf<Pair<UrlAnnotation, StartIndex>>()
    private var currentString = LazyAnnotatedString.Builder()
    private var currentStyle: MarkdownTextType = MarkdownTextType.Body

    private fun pushLink() {
        linksStack.add(currentString.length)
    }

    private fun popLink() {
        val start = linksStack.removeLast()
        currentString.addLink(start, currentString.length)
    }

    private fun pushCodeBlock() {
        codeBlocksStack.add(currentString.length)
    }

    private fun popCodeBlock() {
        val start = codeBlocksStack.removeLast()
        currentString.addCodeBlock(start, currentString.length)
    }

    private fun pushStyle(style: SpanStyle) {
        stylesStack.add(style to currentString.length)
    }

    private fun popStyle() {
        val (style, start) = stylesStack.removeLast()
        currentString.addStyle(style, start, currentString.length)
    }

    private fun pushUrlAnnotation(annotation: UrlAnnotation) {
        urlAnnotationsStack.add(annotation to currentString.length)
    }

    private fun popUrlAnnotation() {
        val (url, start) = urlAnnotationsStack.removeLast()
        currentString.addUrlAnnotation(url, start, currentString.length)
    }

    private fun commitString(newStyle: MarkdownTextType = MarkdownTextType.Body) {
        if (currentString.length > 0) {
            linksStack.forEach { start ->
                currentString.addLink(start, currentString.length)
            }
            codeBlocksStack.forEach { start ->
                currentString.addCodeBlock(start, currentString.length)
            }
            stylesStack.forEach { (style, start) ->
                currentString.addStyle(style, start, currentString.length)
            }
            urlAnnotationsStack.forEach { (url, start) ->
                currentString.addUrlAnnotation(url, start, currentString.length)
            }
            elements.add(MarkdownElement.Text(currentString.build(), currentStyle))
        }
        currentString = LazyAnnotatedString.Builder()
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
        return MarkdownParser(plainText, addDecorativeSpaces).parse(node, andNext = andNext)
    }

    override fun visit(blockQuote: BlockQuote) {
        addElement(MarkdownElement.Quote(nestedParsing(blockQuote.firstChild)))
        if (addDecorativeSpaces) {
            addElement(MarkdownElement.Spacer())
        }
    }

    override fun visit(code: Code) {
        pushCodeBlock()
        addText(code.literal)
        popCodeBlock()
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
            1 -> MarkdownTextType.H1
            2 -> MarkdownTextType.H2
            3 -> MarkdownTextType.H3
            4 -> MarkdownTextType.H4
            5 -> MarkdownTextType.H5
            6 -> MarkdownTextType.H6
            7 -> MarkdownTextType.H7
            else -> MarkdownTextType.Body
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
        try {
            addElement(MarkdownElement.Image(Url(image.destination), image.title))
        } catch (ignored: URLParserException) {
        }
    }

    override fun visit(indentedCodeBlock: IndentedCodeBlock) {
        addElement(MarkdownElement.CodeBlock(indentedCodeBlock.literal.removeSuffix("\n")))
        addElement(MarkdownElement.Spacer())
    }

    override fun visit(link: Link) {
        pushLink()
        pushUrlAnnotation(UrlAnnotation(link.destination))
        super.visit(link)
        popUrlAnnotation()
        popLink()
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
            val children = nestedParsing(
                n,
                andNext = false,
                addDecorativeSpaces = addDecorativeSpaces && !block.isTight,
            )
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
