package com.yjp.functions.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YoutubeVideoListResponseDto(
    @SerializedName("items") val items: List<YoutubeVideoDto>?,
)

data class YoutubeVideoDto(
    @SerializedName("id") val id: String?,
    @SerializedName("snippet") val snippet: YoutubeVideoSnippetDto?,
    @SerializedName("contentDetails") val contentDetails: YoutubeVideoContentDetailsDto?,
    @SerializedName("statistics") val statistics: YoutubeVideoStatisticsDto?,
)

data class YoutubeVideoSnippetDto(
    @SerializedName("title") val title: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("channelId") val channelId: String?,
    @SerializedName("channelTitle") val channelTitle: String?,
    @SerializedName("thumbnails") val thumbnails: YoutubeThumbnailsDto?,
)

data class YoutubeVideoContentDetailsDto(
    @SerializedName("duration") val duration: String?,
)

data class YoutubeVideoStatisticsDto(
    @SerializedName("viewCount") val viewCount: String?,
)

data class YoutubeChannelListResponseDto(
    @SerializedName("items") val items: List<YoutubeChannelDto>?,
)

data class YoutubeChannelDto(
    @SerializedName("id") val id: String?,
    @SerializedName("snippet") val snippet: YoutubeChannelSnippetDto?,
)

data class YoutubeChannelSnippetDto(
    @SerializedName("title") val title: String?,
    @SerializedName("thumbnails") val thumbnails: YoutubeThumbnailsDto?,
)

data class YoutubeThumbnailsDto(
    @SerializedName("default") val default: YoutubeThumbnailDto?,
    @SerializedName("medium") val medium: YoutubeThumbnailDto?,
    @SerializedName("high") val high: YoutubeThumbnailDto?,
    @SerializedName("standard") val standard: YoutubeThumbnailDto?,
    @SerializedName("maxres") val maxres: YoutubeThumbnailDto?,
)

data class YoutubeThumbnailDto(
    @SerializedName("url") val url: String?,
)
