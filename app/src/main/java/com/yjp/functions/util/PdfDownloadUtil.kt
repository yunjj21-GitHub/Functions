package com.yjp.functions.util

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 서버에서 받은 PDF를 기기 다운로드 폴더에 저장하는 유틸
 */
object PdfDownloadUtil {

    private const val MIME_TYPE = "application/pdf"

    /**
     * PDF 데이터를 다운로드 폴더에 저장함
     *
     * @param displayName 서버 Content-Disposition에서 받은 파일 이름
     * @return 성공 시 저장된 파일 이름, 실패 시 Exception
     */
    fun savePdf(
        context: Context,
        inputStream: InputStream,
        displayName: String,
    ): Result<String> = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveWithMediaStore(context, inputStream, displayName)
        } else {
            saveLegacy(inputStream, displayName)
        }
    }

    /** 사용자 디바이스가 Android 10 이상인 경우 */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveWithMediaStore(
        context: Context,
        inputStream: InputStream,
        displayName: String,
    ): String {
        val resolver = context.contentResolver

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("다운로드 파일을 만들 수 없습니다")

        resolver.openOutputStream(uri)?.use { output ->
            inputStream.copyTo(output)
        } ?: error("다운로드 파일을 열 수 없습니다")

        values.clear()
        values.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(uri, values, null, null)

        return displayName
    }

    /** 사용자 디바이스가 Android 10 미만인 경우 */
    @Suppress("DEPRECATION")
    private fun saveLegacy(inputStream: InputStream, displayName: String): String {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val file = File(downloadsDir, displayName)
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }

        return displayName
    }
}
