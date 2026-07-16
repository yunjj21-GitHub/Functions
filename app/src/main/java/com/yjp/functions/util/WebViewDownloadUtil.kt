package com.yjp.functions.util

import android.content.Context
import android.webkit.CookieManager
import android.webkit.URLUtil
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * WebView에서 눌린 파일 다운로드를 처리하는 유틸
 *
 * WebView는 링크 클릭만으로 기기 Downloads에 저장하지 않음
 * → DownloadListener / WebViewClient에서 URL을 받아 직접 받아 저장함
 */
object WebViewDownloadUtil {

    private val httpClient = OkHttpClient()

    /**
     * WebView가 전달한 다운로드 URL을 받아 Downloads 폴더에 저장함
     *
     * @param contentDisposition DownloadListener가 넘겨준 Content-Disposition (없을 수 있음)
     * @param mimeType DownloadListener가 넘겨준 MIME 타입
     * @return 성공 시 저장된 파일 이름
     */
    fun download(
        context: Context,
        url: String,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?,
    ): Result<String> = runCatching {
        // WebView에 쌓인 쿠키를 같이 보내야 로그인/세션 페이지의 파일도 받을 수 있음
        CookieManager.getInstance().flush()
        val cookie = CookieManager.getInstance().getCookie(url)

        val request = Request.Builder()
            .url(url)
            .apply {
                if (!userAgent.isNullOrBlank()) {
                    header("User-Agent", userAgent)
                }
                if (!cookie.isNullOrBlank()) {
                    header("Cookie", cookie)
                }
            }
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("다운로드 실패 (${response.code})")
            }

            val body = response.body ?: error("응답이 비어 있습니다")

            // 응답 헤더 → DownloadListener 값 → URL 순으로 파일명 결정
            val headerDisposition = response.header("Content-Disposition")
            val resolvedDisposition = headerDisposition ?: contentDisposition
            val rawFileName = ContentDispositionUtil.parseFileName(resolvedDisposition)
                ?: URLUtil.guessFileName(url, resolvedDisposition, mimeType)
            val fileName = ContentDispositionUtil.sanitizeFileName(rawFileName)

            body.byteStream().use { inputStream ->
                PdfDownloadUtil.savePdf(
                    context = context,
                    inputStream = inputStream,
                    displayName = fileName,
                ).getOrThrow()
            }
        }
    }

    /**
     * WebView에서 이 URL을 페이지 이동 대신 다운로드로 처리할지 판단함
     */
    fun shouldDownload(url: String, mimeType: String? = null): Boolean {
        if (mimeType?.contains("pdf", ignoreCase = true) == true) return true
        if (mimeType?.contains("octet-stream", ignoreCase = true) == true) return true

        val lowerUrl = url.lowercase()
        return lowerUrl.endsWith(".pdf") ||
            lowerUrl.contains("download") ||
            lowerUrl.contains("atchfileid") ||
            lowerUrl.contains("filedown")
    }
}
