package com.yjp.functions.data.remote.api

import com.yjp.functions.data.remote.dto.YoutubeChannelListResponseDto
import com.yjp.functions.data.remote.dto.YoutubeVideoListResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApiService {

    @GET("videos")
    suspend fun getVideos(
        @Query("part") part: String = "snippet,contentDetails,statistics",
        @Query("id") videoIds: String,
        @Query("key") apiKey: String,
    ): YoutubeVideoListResponseDto

    @GET("channels")
    suspend fun getChannels(
        @Query("part") part: String = "snippet",
        @Query("id") channelIds: String,
        @Query("key") apiKey: String,
    ): YoutubeChannelListResponseDto
}
