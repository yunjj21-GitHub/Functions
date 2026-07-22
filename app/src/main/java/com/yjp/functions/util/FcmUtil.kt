package com.yjp.functions.util

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
}
