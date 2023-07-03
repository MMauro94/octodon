package io.github.mmauro94.common.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.mmauro94.common.ui.components.LoadableImage

@Composable
fun ReadOnlyMarkdown(
    markdown: Markdown,
    enableClicks: Boolean,
) {
    ReadOnlyMarkdown(markdown.elements, enableClicks)
}

@Composable
fun ReadOnlyMarkdown(
    markdownElements: List<MarkdownElement>,
    enableClicks: Boolean,
) {
    Column {
        ReadOnlyMarkdownElements(markdownElements, enableClicks)
    }
}

/**
 * Should be composed inside a Column-like layout
 */
@Composable
fun ReadOnlyMarkdownElements(
    markdownElements: List<MarkdownElement>,
    enableClicks: Boolean,
) {
    markdownElements.forEach { element ->
        when (element) {
            is MarkdownElement.Text -> {
                DefaultClickableText(element.text.toAnnotatedString(), style = element.style.toTextStyle(), enabled = enableClicks)
            }

            is MarkdownElement.Quote -> {
                Row(Modifier.height(IntrinsicSize.Min)) {
                    Box(Modifier.fillMaxHeight().width(12.dp).padding(horizontal = 4.dp).background(MaterialTheme.colorScheme.primary))
                    ReadOnlyMarkdown(element.content, enableClicks)
                }
            }

            is MarkdownElement.Divider -> {
                Spacer(Modifier.height(8.dp))
                // TODO: color
                Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
            }

            is MarkdownElement.Spacer -> {
                Spacer(Modifier.height(element.height))
            }

            is MarkdownElement.ListItem -> {
                Row {
                    Spacer(Modifier.width(8.dp))
                    Text(element.bullet, style = MaterialTheme.typography.bodyMedium)
                    ReadOnlyMarkdown(element.content, enableClicks)
                }
            }

            is MarkdownElement.CodeBlock -> {
                Surface(
                    color = codeBackgroundColor(),
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
                LoadableImage(
                    Modifier.fillMaxWidth(),
                    Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                    image = element.url,
                    contentScale = ContentScale.FillWidth,
                )
            }

            is MarkdownElement.Table -> {
                // TODO
                Text("Pretend I'm a table")
            }
        }
    }
}

@Composable
@OptIn(ExperimentalTextApi::class)
private fun DefaultClickableText(
    text: AnnotatedString,
    style: TextStyle,
    enabled: Boolean,
) {
    if (!enabled) {
        Text(text, style = style)
    } else {
        val uriHandler = LocalUriHandler.current
        ClickableText(
            text,
            style = style.merge(TextStyle(color = LocalContentColor.current)),
            onClick = { pos ->
                text
                    .getUrlAnnotations(pos, pos)
                    .firstOrNull()?.let { urlAnnotation ->
                        uriHandler.openUri(urlAnnotation.item.url)
                    }
            },
        )
    }
}
