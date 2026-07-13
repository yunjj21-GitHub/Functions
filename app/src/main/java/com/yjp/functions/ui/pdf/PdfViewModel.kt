package com.yjp.functions.ui.pdf

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjp.functions.data.remote.result.FunctionsResult
import com.yjp.functions.data.repository.PdfRepository
import com.yjp.functions.util.FunctionsLog
import com.yjp.functions.util.PdfDownloadUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class PdfViewModel @Inject constructor(
    private val pdfRepository: PdfRepository,
) : ViewModel() {

    private val _downloadMessage = MutableStateFlow<String?>(null)
    val downloadMessage: StateFlow<String?> = _downloadMessage.asStateFlow()

    fun downloadPdf(context: Context) {
        viewModelScope.launch {
            val message = withContext(Dispatchers.IO) {
                when (val result = pdfRepository.downloadPdf()) {
                    is FunctionsResult.Success -> {
                        PdfDownloadUtil.savePdf(
                            context = context,
                            inputStream = result.data.bytes.inputStream(),
                            displayName = result.data.fileName,
                        ).fold(
                            onSuccess = { fileName ->
                                FunctionsLog.d("PDF 다운로드 성공: $fileName")
                                "다운로드 폴더에 저장되었습니다"
                            },
                            onFailure = { error ->
                                FunctionsLog.e("PDF 저장 실패", error)
                                "다운로드에 실패했습니다"
                            },
                        )
                    }
                    is FunctionsResult.Fail -> {
                        FunctionsLog.e("PDF 다운로드 실패: ${result.message}", result.throwable)
                        "다운로드에 실패했습니다"
                    }
                }
            }
            _downloadMessage.value = message
        }
    }

    fun onDownloadMessageShown() {
        _downloadMessage.value = null
    }
}
