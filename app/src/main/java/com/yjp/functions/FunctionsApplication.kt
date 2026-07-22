package com.yjp.functions

import android.app.Application
import com.yjp.functions.fcm.NotificationChannelHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FunctionsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 앱 시작 시 알림 채널을 미리 생성 (권한/알림 유실 방지)
        NotificationChannelHelper.createChannel(this)
    }
}
