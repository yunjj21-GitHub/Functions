package com.yjp.functions.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

/**
 * 기기에서 고른 PDF를 앱 내부 저장소에 첨부(업로드)하는 유틸
 */
object PdfUploadUtil {

    private const val UPLOAD_DIR = "pdf_uploads"

    /**
     * URI에서 표시용 파일 이름을 꺼냄
     */
    fun resolveDisplayName(context: Context, uri: Uri): String {
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    val name = cursor.getString(index)
                    if (!name.isNullOrBlank()) return name
                }
            }
        }
        return uri.lastPathSegment ?: "unknown.pdf"
    }

    /**
     * 선택한 PDF를 앱 내부 폴더(files/pdf_uploads)에 복사함
     *
     * @return 성공 시 저장된 파일 이름
     */
    fun uploadPdf(context: Context, uri: Uri): Result<String> = runCatching {
        // 표시용 파일명 정리 (금지 문자 제거)
        val displayName = ContentDispositionUtil.sanitizeFileName(
            resolveDisplayName(context, uri),
        )
        // 앱 전용 저장 폴더 준비
        val uploadDir = File(context.filesDir, UPLOAD_DIR).also { dir ->
            if (!dir.exists()) dir.mkdirs()
        }

        // 같은 이름이면 (1), (2)... 붙여서 경로 결정
        val target = uniqueFile(uploadDir, displayName)
        // 선택한 파일 → 앱 내부 폴더로 복사
        context.contentResolver.openInputStream(uri)?.use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: error("선택한 파일을 열 수 없습니다")

        target.name
    }

    /**
     * 같은 이름이 이미 있으면 덮어쓰지 않고 `이름 (1).pdf` 형태로 만듦
     */
    private fun uniqueFile(dir: File, displayName: String): File {
        val candidate = File(dir, displayName)
        if (!candidate.exists()) return candidate

        // 확장자 분리: "보고서.pdf" → base="보고서", ext=".pdf"
        val dot = displayName.lastIndexOf('.')
        val base = if (dot > 0) displayName.substring(0, dot) else displayName
        val ext = if (dot > 0) displayName.substring(dot) else ""

        var index = 1
        while (true) {
            val next = File(dir, "$base ($index)$ext")
            if (!next.exists()) return next
            index++
        }
    }
}