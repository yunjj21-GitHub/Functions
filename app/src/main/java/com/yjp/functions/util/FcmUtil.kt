package com.yjp.functions.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

/** FCM 관련 동작을 담당하는 유틸 */
object FcmUtil {

    /**
     * 현재 FCM 등록 토큰을 가져옴
     *
     * @param onSuccess 토큰 조회 성공 콜백
     * @param onFailure 토큰 조회 실패 콜백
     */
    fun getToken(
        onSuccess: (token: String) -> Unit = {},
        onFailure: (exception: Exception?) -> Unit = {},
    ) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                val exception = task.exception
                FunctionsLog.w("FCM 토큰 가져오기 실패", exception)
                onFailure(exception)
                return@addOnCompleteListener
            }

            val token = task.result
            FunctionsLog.d("FCM registration token: $token")
            onSuccess(token)
        }
    }

    /** Android 13+ 알림 권한이 있는지 확인함 */
    fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 알림 권한이 없으면 요청 콜백을 호출함
     * (실제 시스템 다이얼로그는 화면/Activity에서 launcher로 띄움)
     *
     * @param onAlreadyGranted 이미 허용된 경우(또는 API 33 미만)
     * @param onRequestPermission POST_NOTIFICATIONS 요청이 필요할 때
     */
    fun requestNotificationPermissionIfNeeded(
        context: Context,
        onAlreadyGranted: () -> Unit = {},
        onRequestPermission: () -> Unit,
    ) {
        if (hasNotificationPermission(context)) {
            onAlreadyGranted()
        } else {
            onRequestPermission()
        }
    }
}
