package io.github.mmauro94.common.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.github.mmauro94.common.client.api.GetPostsItem

@Composable
fun Post(
    post: GetPostsItem
) {
    Text(post.post.name)
}
