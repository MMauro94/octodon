package io.github.mmauro94.common.markdown

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun codeBackgroundColor() = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
