package com.yjp.functions.fcm

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yjp.functions.R
import com.yjp.functions.ui.main.MainActivity
import com.yjp.functions.util.FunctionsLog

/** FCM 메시지 수신 서비스 */
class FcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        FunctionsLog.d("FCM 토큰 갱신: $token")
        // 필요 시 서버로 토큰 전송
    }

    override fun onMessageReceived(message: RemoteMessage) {
        FunctionsLog.d(
            "FCM 메시지 수신 from=${message.from}, " +
                "data=${message.data}, " +
                "notification=${message.notification?.title}/${message.notification?.body}",
        )

        val notification = message.notification
        if (notification != null) {
            showNotification(
                title = notification.title,
                body = notification.body,
            )
        } else if (message.data.isNotEmpty()) {
            showNotification(
                title = message.data["title"] ?: getString(R.string.app_name),
                body = message.data["body"] ?: message.data.toString(),
            )
        }
    }

    /** 앱 알림 채널로 알림을 표시함 */
    private fun showNotification(title: String?, body: String?) {
        NotificationChannelHelper.createChannel(this)

        // 알림 탭 시 MainActivity로 이동 (이미 열려 있으면 위 화면만 정리)
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        // UPDATE_CURRENT: 기존 PendingIntent 갱신 / IMMUTABLE: 외부에서 수정 불가
        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            }
        // 알림 탭 시점에 Intent를 실행하도록 시스템에 맡김
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            pendingIntentFlags,
        )

        val notification = NotificationCompat.Builder(this, NotificationChannelHelper.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title ?: getString(R.string.app_name))
            .setContentText(body.orEmpty())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // 알림 클릭 시 pendingIntent 실행
            .build()

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
