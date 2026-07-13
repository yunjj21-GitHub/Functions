package com.yjp.functions.data.repository

import com.yjp.functions.data.model.DownloadedPdf
import com.yjp.functions.data.remote.api.PdfApiService
import com.yjp.functions.data.remote.result.FunctionsResult
import com.yjp.functions.util.ContentDispositionUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfRepository @Inject constructor(
    private val pdfApiService: PdfApiService,
) {
    suspend fun downloadPdf(): FunctionsResult<DownloadedPdf> {
        return try {
            val response = pdfApiService.downloadPdf()
            if (!response.isSuccessful) {
                return FunctionsResult.Fail(message = "다운로드 실패 (${response.code()})")
            }

            val bytes = response.body()?.use { it.bytes() }
                ?: return FunctionsResult.Fail(message = "응답이 비어 있습니다")

            val contentDisposition = response.headers()["Content-Disposition"]
            val fileName = ContentDispositionUtil.parseFileName(contentDisposition)
                ?.let(ContentDispositionUtil::sanitizeFileName)
                ?: ContentDispositionUtil.FALLBACK_FILE_NAME

            FunctionsResult.Success(
                DownloadedPdf(
                    bytes = bytes,
                    fileName = fileName,
                ),
            )
        } catch (e: Exception) {
            FunctionsResult.Fail(
                message = e.message ?: "네트워크 오류",
                throwable = e,
            )
        }
    }
}
