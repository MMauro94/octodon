package io.github.mmauro94.common.markdown

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
            // TODO: render all element types
            when (element) {
                is MarkdownElement.Text -> {
                    Text(element.text)
                }
                is MarkdownElement.Divider -> {
                    Divider()
                }

                is MarkdownElement.CodeBlock -> {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.extraSmall,
                    ) {
                        Text(element.text, modifier = Modifier.padding(8.dp).fillMaxWidth(), fontFamily = FontFamily.Monospace)
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
                    Text("Pretend I'm a table")
                }
            }
        }
    }
}
