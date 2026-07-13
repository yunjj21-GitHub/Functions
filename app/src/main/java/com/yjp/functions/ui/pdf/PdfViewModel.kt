package com.yjp.functions.ui.pdf

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjp.functions.data.remote.result.FunctionsResult
import com.yjp.functions.data.repository.PdfRepository
import com.yjp.functions.util.FunctionsLog
import com.yjp.functions.util.PdfDownloadUtil
import com.yjp.functions.util.PdfUploadUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class PdfViewModel @Inject constructor(
    private val pdfRepository: PdfRepository,
) : ViewModel() {

    private val _downloadMessage = MutableStateFlow<String?>(null)
    val downloadMessage: StateFlow<String?> = _downloadMessage.asStateFlow()

    private val _uploadMessage = MutableStateFlow<String?>(null)
    val uploadMessage: StateFlow<String?> = _uploadMessage.asStateFlow()

    /** 업로드(첨부)에 성공한 PDF 파일 이름 목록 */
    private val _uploadedFileNames = MutableStateFlow<List<String>>(emptyList())
    val uploadedFileNames: StateFlow<List<String>> = _uploadedFileNames.asStateFlow()

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

    fun uploadPdf(context: Context, uri: Uri) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                PdfUploadUtil.uploadPdf(context, uri)
            }

            result.fold(
                onSuccess = { fileName ->
                    _uploadedFileNames.update { it + fileName }
                    _uploadMessage.value = "업로드되었습니다"
                    FunctionsLog.d("PDF 업로드 성공: $fileName")
                },
                onFailure = { error ->
                    _uploadMessage.value = "업로드에 실패했습니다"
                    FunctionsLog.e("PDF 업로드 실패", error)
                },
            )
        }
    }

    fun onDownloadMessageShown() {
        _downloadMessage.value = null
    }

    fun onUploadMessageShown() {
        _uploadMessage.value = null
    }
}
