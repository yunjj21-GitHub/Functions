package com.yjp.functions.ui.youtube

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjp.functions.ui.youtube.player.YoutubePlayerScreen

@Composable
fun YoutubeScreen(
    viewModel: YoutubeViewModel,
    modifier: Modifier = Modifier,
) {
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val playingVideoId by viewModel.playingVideoId.collectAsStateWithLifecycle()

    if (playingVideoId != null) {
        YoutubePlayerScreen(
            videoId = playingVideoId!!,
            onBack = viewModel::stopPlayback,
            modifier = modifier,
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = videos,
            key = { it.videoId },
        ) { video ->
            YoutubeVideoItem(
                video = video,
                onClick = { viewModel.playVideo(video.videoId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
