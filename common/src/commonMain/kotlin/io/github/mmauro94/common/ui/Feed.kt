package io.github.mmauro94.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmauro94.common.MR
import io.github.mmauro94.common.PlatformVerticalScrollbar
import io.github.mmauro94.common.client.entities.Community
import io.github.mmauro94.common.client.entities.PostView
import io.github.mmauro94.common.utils.AsyncState
import io.github.mmauro94.common.utils.DownloadableFeed
import kotlinx.coroutines.launch

@Composable
fun Feed(
    downloadableFeed: DownloadableFeed,
    feedListState: LazyListState,
    modifier: Modifier = Modifier,
    onPostClick: (PostView) -> Unit,
    openCommunity: (Community) -> Unit,
) {
    val cs = rememberCoroutineScope()
    val feed = downloadableFeed.feed
    Box(modifier) {
        // TODO empty feed view
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp), state = feedListState) {
            items(feed.postViews) { post ->
                Post(
                    postView = post,
                    onClick = { onPostClick(post) },
                    onUpdatePost = downloadableFeed::updatePost,
                    openCommunity = openCommunity,
                )
            }
            when (val state = feed.state) {
                is AsyncState.Error -> item {
                    Column {
                        Text(state.error)
                        Button(
                            onClick = { cs.launch { downloadableFeed.downloadNextPage() } },
                        ) {
                            Text(stringResource(MR.strings.retry_action))
                        }
                    }
                }

                is AsyncState.Loading -> item {
                    CircularProgressIndicator()
                }

                is AsyncState.Resting -> item {
                    LaunchedEffect(Unit) {
                        downloadableFeed.downloadNextPage()
                    }
                }

                is AsyncState.Success -> {}
            }
        }

        PlatformVerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            listState = feedListState,
        )
    }
}
