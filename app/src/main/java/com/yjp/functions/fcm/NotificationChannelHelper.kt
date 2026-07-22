package com.yjp.functions.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.yjp.functions.BuildConfig

/** FCM 알림 채널 생성/관리 */
object NotificationChannelHelper {

    val CHANNEL_ID: String = BuildConfig.FCM_NOTIFICATION_CHANNEL_ID
    private const val CHANNEL_NAME = "일반 알림"
    private const val CHANNEL_DESCRIPTION = "앱 푸시 알림"

    /** 앱 전용 FCM 알림 채널을 생성함 (이미 있으면 그대로 둠) */
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = CHANNEL_DESCRIPTION
        }
        manager.createNotificationChannel(channel)
    }
}
