package io.github.mmauro94.common.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.github.mmauro94.common.client.entities.Post

@Composable
fun Post(
    post: Post,
) {
    Text(post.post.name)
}
