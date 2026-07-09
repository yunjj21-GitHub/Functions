package com.yjp.functions.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yjp.functions.data.model.YoutubeVideoInfo
import com.yjp.functions.ui.theme.FunctionsTheme
import com.yjp.functions.util.YoutubeFormatUtil

@Composable
fun HomeVideoItem(
    video: YoutubeVideoInfo,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                LabeledTextItem(label = "영상 제목", value = video.title)

                Spacer(modifier = Modifier.height(10.dp))

                LabeledItem(label = "영상 썸네일") {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LabeledTextItem(
                    label = "영상 길이",
                    value = YoutubeFormatUtil.formatDuration(video.duration),
                )

                Spacer(modifier = Modifier.height(10.dp))

                LabeledTextItem(
                    label = "영상 재생 횟수",
                    value = YoutubeFormatUtil.formatViewCount(video.viewCount),
                )

                Spacer(modifier = Modifier.height(10.dp))

                LabeledTextItem(
                    label = "영상 업로드 일자",
                    value = YoutubeFormatUtil.formatPublishedAt(video.publishedAt),
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color.Black,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                LabeledTextItem(label = "채널명", value = video.channelName)

                Spacer(modifier = Modifier.height(10.dp))

                LabeledItem(label = "채널 프로필 이미지") {
                    AsyncImage(
                        model = video.channelProfileImageUrl,
                        contentDescription = video.channelName,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

@Composable
private fun LabeledTextItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    LabeledItem(label = label, modifier = modifier) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun LabeledItem(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeVideoItemPreview() {
    FunctionsTheme {
        HomeVideoItem(
            video = previewYoutubeVideoInfo(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

private fun previewYoutubeVideoInfo(): YoutubeVideoInfo {
    return YoutubeVideoInfo(
        videoId = "vXiBTHJI1SY",
        thumbnailUrl = "https://i.ytimg.com/vi/vXiBTHJI1SY/maxresdefault.jpg",
        title = "[EN] 여름 방학식은 핑계고｜EP.114",
        duration = "PT58M28S",
        channelProfileImageUrl = "https://yt3.ggpht.com//D0eRDrxMc6Yu0AZSQobnV21TwXPPUo9GWk4a5RunYUXpjyLqJdqw-WNeEZ1RjVfH39TeHr37ng=s800-c-k-c0x00ffffff-no-rj",
        channelName = "뜬뜬 DdeunDdeun",
        viewCount = "2966534",
        publishedAt = "2026-07-04T00:00:06Z",
    )
}
