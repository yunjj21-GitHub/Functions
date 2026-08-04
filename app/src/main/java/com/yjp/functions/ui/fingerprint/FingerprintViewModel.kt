package com.yjp.functions.ui.fingerprint

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.yjp.functions.util.BiometricUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class FingerprintViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _authResult = MutableStateFlow(DEFAULT_RESULT)
    val authResult: StateFlow<String> = _authResult.asStateFlow()

    /** 아이콘 클릭 시 생체 인증 실행 */
    fun authenticate(activity: FragmentActivity) {
        if (!BiometricUtil.canAuthenticate(context)) {
            _authResult.value = "사용 불가"
            return
        }

        BiometricUtil.authenticate(
            activity = activity,
            onSuccess = {
                _authResult.value = "성공"
            },
            onFailure = { message ->
                _authResult.value = message
            },
        )
    }

    companion object {
        private const val DEFAULT_RESULT = "미인증"
    }
}
