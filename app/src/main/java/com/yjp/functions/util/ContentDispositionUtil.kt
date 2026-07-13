package com.yjp.functions.util

import java.net.URLDecoder

object ContentDispositionUtil {

    /**
     * Content-Disposition 헤더에서 파일 이름을 꺼냄
     *
     * 예:
     * - attachment; filename="[샘플] 광고1번지 2021년 12월호(펼침).pdf";
     */
    fun parseFileName(contentDisposition: String?): String? {
        if (contentDisposition.isNullOrBlank()) return null

        FILENAME_STAR_REGEX.find(contentDisposition)?.groupValues?.get(1)?.let { encoded ->
            return runCatching {
                URLDecoder.decode(encoded, Charsets.UTF_8.name())
            }.getOrNull()
        }

        FILENAME_REGEX.find(contentDisposition)?.groupValues?.get(1)?.let { fileName ->
            return fileName.trim().trim('"')
        }

        return null
    }

    fun sanitizeFileName(fileName: String): String {
        return fileName
            .replace(INVALID_FILE_NAME_CHARS, "_")
            .trim()
            .ifBlank { FALLBACK_FILE_NAME }
    }

    private val FILENAME_STAR_REGEX =
        Regex("""filename\*\s*=\s*UTF-8''([^;]+)""", RegexOption.IGNORE_CASE)

    private val FILENAME_REGEX =
        Regex("""filename\s*=\s*"?([^";]+)"?""", RegexOption.IGNORE_CASE)

    private val INVALID_FILE_NAME_CHARS = Regex("""[\\/:*?"<>|]""")

    const val FALLBACK_FILE_NAME = "sample.pdf"
}
