package io.github.mmauro94.common.markdown

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter

@Composable
fun ReadOnlyMarkdown(
    markdown: String,
) {
    val ast = parse(markdown)
    ReadOnlyMarkdown(ast)
}

@Composable
fun ReadOnlyMarkdown(
    markdownElements: List<MarkdownElement>,
) {
    Column {
        markdownElements.forEach { element ->
            when (element) {
                is MarkdownElement.Text -> {
                    Text(element.text, style = element.style)
                }

                is MarkdownElement.Quote -> {
                    Row(Modifier.height(IntrinsicSize.Min)) {
                        Box(Modifier.fillMaxHeight().width(12.dp).padding(horizontal = 4.dp).background(MaterialTheme.colorScheme.primary))
                        Text(element.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                is MarkdownElement.Divider -> {
                    Divider()
                }

                is MarkdownElement.CodeBlock -> {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.extraSmall,
                    ) {
                        Text(
                            element.text,
                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                is MarkdownElement.Image -> {
                    val painter = rememberAsyncImagePainter(element.url)
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                    )
                }

                is MarkdownElement.Table -> {
                    //TODO
                    Text("Pretend I'm a table")
                }
            }
        }
    }
}
