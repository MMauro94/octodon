package io.github.mmauro94.common.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.markdown.Markdown
import com.mikepenz.markdown.MarkdownDefaults

@Composable
fun Material3Markdown(
    content: String,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    Markdown(
        content = content,
        modifier = modifier,
        colors = MarkdownDefaults.markdownColors(
            textColor = MaterialTheme.colorScheme.onSurface,
            codeBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        ),
        typography = MarkdownDefaults.markdownTypography(
            h1 = MaterialTheme.typography.headlineLarge,
            h2 = MaterialTheme.typography.headlineMedium,
            h3 = MaterialTheme.typography.headlineSmall,
            h4 = MaterialTheme.typography.titleLarge,
            h5 = MaterialTheme.typography.titleMedium,
            h6 = MaterialTheme.typography.titleSmall,
            body1 = MaterialTheme.typography.bodyMedium,
            body2 = MaterialTheme.typography.bodySmall,
        ),
    )
}
