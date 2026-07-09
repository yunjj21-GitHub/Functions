package com.yjp.functions.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.yjp.functions.data.remote.api.YoutubeApiService
import com.yjp.functions.data.remote.interceptor.FunctionsNetInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val YOUTUBE_BASE_URL = "https://www.googleapis.com/youtube/v3/"

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideFunctionsNetInterceptor(): FunctionsNetInterceptor = FunctionsNetInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        functionsNetInterceptor: FunctionsNetInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(functionsNetInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(YOUTUBE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideYoutubeApiService(retrofit: Retrofit): YoutubeApiService {
        return retrofit.create(YoutubeApiService::class.java)
    }
}
