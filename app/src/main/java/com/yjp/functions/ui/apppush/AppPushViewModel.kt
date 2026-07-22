package com.yjp.functions.ui.apppush

import androidx.lifecycle.ViewModel
import com.yjp.functions.util.FcmUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AppPushViewModel @Inject constructor() : ViewModel() {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /** null: 아직 확인 전, true: 허용, false: 거부 */
    private val _isNotificationPermissionGranted = MutableStateFlow<Boolean?>(null)
    val isNotificationPermissionGranted: StateFlow<Boolean?> =
        _isNotificationPermissionGranted.asStateFlow()

    init {
        loadToken()
    }

    /** 알림 권한 요청 결과를 반영함 */
    fun onNotificationPermissionResult(granted: Boolean) {
        _isNotificationPermissionGranted.value = granted
    }

    /** FCM 토큰을 조회해 화면에 표시할 상태를 갱신함 */
    private fun loadToken() {
        FcmUtil.getToken(
            onSuccess = { result ->
                _token.value = result
                _errorMessage.value = null
            },
            onFailure = { exception ->
                _token.value = null
                _errorMessage.value = exception?.message ?: "FCM 토큰을 가져오지 못했습니다"
            },
        )
    }
}
