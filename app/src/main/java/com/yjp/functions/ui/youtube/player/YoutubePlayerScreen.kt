package com.yjp.functions.ui.youtube.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yjp.functions.R
import com.yjp.functions.ui.theme.FunctionsTheme

@Composable
fun YoutubePlayerScreen(
    videoId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    YoutubePlayerScreenContent(
        onBack = onBack,
        modifier = modifier,
        player = { playerModifier ->
            YoutubePlayer(
                videoId = videoId,
                modifier = playerModifier,
            )
        },
    )
}

@Composable
private fun YoutubePlayerScreenContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    player: @Composable (Modifier) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center,
    ) {
        player(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 8.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "뒤로",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun YoutubePlayerScreenPreview() {
    FunctionsTheme {
        YoutubePlayerScreenContent(
            onBack = {},
            player = { modifier ->
                Box(
                    modifier = modifier.background(Color(0xFF212121)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "YouTube Player",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            },
        )
    }
}
