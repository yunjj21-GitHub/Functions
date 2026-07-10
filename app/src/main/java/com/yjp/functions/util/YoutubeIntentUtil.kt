package com.yjp.functions.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

object YoutubeIntentUtil {

    private const val YOUTUBE_PACKAGE = "com.google.android.youtube"

    fun openYoutubeApp(context: Context, videoId: String) {
        val youtubeIntent = Intent(Intent.ACTION_VIEW, "vnd.youtube:$videoId".toUri()).apply {
            setPackage(YOUTUBE_PACKAGE)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(youtubeIntent)
        } catch (_: ActivityNotFoundException) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://www.youtube.com/watch?v=$videoId".toUri(),
            )
            context.startActivity(webIntent)
        }
    }
}
