package io.github.mmauro94.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import io.github.mmauro94.common.client.entities.Community

@Composable
fun CommunityHeader(
    community: Community,
) {
    Box(Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
        if (community.banner != null) {
            Box(Modifier.matchParentSize().alpha(.2f)) {
                LoadableImage(
                    Modifier.fillMaxSize(),
                    Modifier.fillMaxSize(),
                    community.banner,
                )
            }
        }

        Column(Modifier.padding(32.dp).fillMaxWidth()) {
            Surface(Modifier.size(96.dp), shape = CircleShape) {
                if (community.icon != null) {
                    LoadableImage(
                        Modifier.fillMaxSize(),
                        Modifier.fillMaxSize(),
                        community.icon,
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            Text(community.name, style = MaterialTheme.typography.titleMedium)
            Text(community.title, style = MaterialTheme.typography.titleSmall)
        }
    }
}
