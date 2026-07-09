package com.yjp.functions.data.repository

import com.yjp.functions.BuildConfig
import com.yjp.functions.common.result.FunctionsResult
import com.yjp.functions.data.model.YoutubeVideoInfo
import com.yjp.functions.data.remote.api.YoutubeApiService
import com.yjp.functions.data.remote.dto.YoutubeThumbnailsDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YoutubeRepository @Inject constructor(
    private val youtubeApiService: YoutubeApiService,
) {

    suspend fun getVideos(videoIds: List<String>): FunctionsResult<List<YoutubeVideoInfo>> {
        return try {
            val uniqueVideoIds = videoIds.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
            if (uniqueVideoIds.isEmpty()) {
                return FunctionsResult.Fail(message = "조회할 유튜브 영상 ID가 없습니다.")
            }

            if (BuildConfig.YOUTUBE_API_KEY.isBlank()) {
                return FunctionsResult.Fail(
                    message = "YOUTUBE_API_KEY가 설정되지 않았습니다. local.properties에 YOUTUBE_API_KEY를 추가해 주세요.",
                )
            }

            val videoResponse = youtubeApiService.getVideos(
                videoIds = uniqueVideoIds.joinToString(","),
                apiKey = BuildConfig.YOUTUBE_API_KEY,
            )

            val videos = videoResponse.items.orEmpty()
            if (videos.isEmpty()) {
                return FunctionsResult.Fail(message = "영상을 찾을 수 없습니다. videoIds=$uniqueVideoIds")
            }

            val channelIds = videos.mapNotNull { it.snippet?.channelId }.distinct()
            val channelMap = if (channelIds.isEmpty()) {
                emptyMap()
            } else {
                youtubeApiService.getChannels(
                    channelIds = channelIds.joinToString(","),
                    apiKey = BuildConfig.YOUTUBE_API_KEY,
                ).items.orEmpty().associateBy { it.id.orEmpty() }
            }

            val videoInfos = videos.mapNotNull { video ->
                val videoId = video.id ?: return@mapNotNull null
                val channelId = video.snippet?.channelId
                val channel = channelId?.let { channelMap[it] }

                YoutubeVideoInfo(
                    videoId = videoId,
                    thumbnailUrl = selectThumbnailUrl(video.snippet?.thumbnails),
                    title = video.snippet?.title.orEmpty(),
                    duration = video.contentDetails?.duration.orEmpty(),
                    channelProfileImageUrl = selectThumbnailUrl(channel?.snippet?.thumbnails),
                    channelName = channel?.snippet?.title ?: video.snippet?.channelTitle.orEmpty(),
                    viewCount = video.statistics?.viewCount.orEmpty(),
                    publishedAt = video.snippet?.publishedAt.orEmpty(),
                )
            }

            if (videoInfos.isEmpty()) {
                return FunctionsResult.Fail(message = "영상 정보를 변환할 수 없습니다. videoIds=$uniqueVideoIds")
            }

            FunctionsResult.Success(data = videoInfos)
        } catch (e: Exception) {
            FunctionsResult.Fail(
                message = e.message ?: "YouTube API 요청에 실패했습니다.",
                throwable = e,
            )
        }
    }

    private fun selectThumbnailUrl(thumbnails: YoutubeThumbnailsDto?): String {
        return thumbnails?.maxres?.url
            ?: thumbnails?.high?.url
            ?: thumbnails?.medium?.url
            ?: thumbnails?.default?.url
            ?: ""
    }
}
