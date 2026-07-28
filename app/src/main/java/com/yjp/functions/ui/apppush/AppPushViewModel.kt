package com.yjp.functions.ui.apppush

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjp.functions.data.remote.result.FunctionsResult
import com.yjp.functions.data.repository.FcmRepository
import com.yjp.functions.util.FcmUtil
import com.yjp.functions.util.FunctionsLog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AppPushViewModel @Inject constructor(
    private val fcmRepository: FcmRepository,
) : ViewModel() {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /** null: 아직 확인 전, true: 허용, false: 거부 */
    private val _isNotificationPermissionGranted = MutableStateFlow<Boolean?>(null)
    val isNotificationPermissionGranted: StateFlow<Boolean?> =
        _isNotificationPermissionGranted.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _body = MutableStateFlow("")
    val body: StateFlow<String> = _body.asStateFlow()

    private val _sendMessage = MutableStateFlow<String?>(null)
    val sendMessage: StateFlow<String?> = _sendMessage.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    init {
        loadToken()
    }

    /** 알림 권한 요청 결과를 반영함 */
    fun onNotificationPermissionResult(granted: Boolean) {
        _isNotificationPermissionGranted.value = granted
    }

    fun onTitleChange(value: String) {
        _title.value = value
    }

    fun onBodyChange(value: String) {
        _body.value = value
    }

    fun onSendMessageShown() {
        _sendMessage.value = null
    }

    /** 현재 기기 토큰으로 입력한 제목/내용 푸시를 보냄 */
    fun sendPush() {
        val currentToken = _token.value
        if (currentToken.isNullOrBlank()) {
            _sendMessage.value = "FCM 토큰이 아직 없습니다"
            return
        }
        val pushTitle = _title.value.trim()
        val pushBody = _body.value.trim()
        if (pushTitle.isEmpty() || pushBody.isEmpty()) {
            _sendMessage.value = "제목과 내용을 입력해 주세요"
            return
        }
        if (_isSending.value) return

        viewModelScope.launch {
            _isSending.value = true
            val message = withContext(Dispatchers.IO) {
                when (
                    val result = fcmRepository.sendPush(
                        deviceToken = currentToken,
                        title = pushTitle,
                        body = pushBody,
                    )
                ) {
                    is FunctionsResult.Success -> {
                        FunctionsLog.d("FCM 푸시 전송 성공")
                        "푸시 전송 성공"
                    }

                    is FunctionsResult.Fail -> {
                        FunctionsLog.e("FCM 푸시 전송 실패: ${result.message}", result.throwable)
                        result.message
                    }
                }
            }
            _isSending.value = false
            _sendMessage.value = message
        }
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
