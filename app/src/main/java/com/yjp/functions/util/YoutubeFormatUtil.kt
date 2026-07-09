package com.yjp.functions.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * YouTube API 응답값을 화면 표시용 문자열로 변환하는 유틸
 *
 * API는 duration, publishedAt, viewCount를 원본 형식 그대로 내려주므로
 * UI에서 보여주기 전에 이 유틸로 포맷함
 */
object YoutubeFormatUtil {

    /**
     * ISO 8601 영상 길이를 hh:mm:ss 형식으로 변환
     *
     * 예: PT4M13S → 00:04:13, PT1H2M30S → 01:02:30
     */
    fun formatDuration(isoDuration: String): String {
        val match = DURATION_REGEX.matchEntire(isoDuration) ?: return isoDuration

        val hours = match.groupValues[1].toIntOrNull() ?: 0
        val minutes = match.groupValues[2].toIntOrNull() ?: 0
        val seconds = match.groupValues[3].toIntOrNull() ?: 0

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * ISO 8601 업로드 일시를 상대 시간 문자열로 변환
     *
     * - 24시간 미만: 3시간 전
     * - 14일 미만: 5일 전
     * - 1개월 미만: 2주일 전
     * - 1년 미만: 3개월 전
     * - 1년 이상: 2년 전
     */
    fun formatPublishedAt(isoDateTime: String): String {
        val trimmed = isoDateTime.trim()
        if (trimmed.isEmpty()) return ""

        val publishedMillis = parsePublishedAtMillis(trimmed) ?: return trimmed
        val nowMillis = System.currentTimeMillis()
        val elapsedMillis = (nowMillis - publishedMillis).coerceAtLeast(0)

        val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
        val days = TimeUnit.MILLISECONDS.toDays(elapsedMillis)
        val weeks = days / 7
        val months = monthsBetween(publishedMillis, nowMillis)
        val years = yearsBetween(publishedMillis, nowMillis)

        return when {
            hours < 24 -> "${hours}시간 전"
            days < 14 -> "${days}일 전"
            months < 1 -> "${weeks}주일 전"
            years < 1 -> "${months}개월 전"
            else -> "${years}년 전"
        }
    }

    /**
     * 조회수를 화면 표시용 문자열로 변환
     *
     * - 1,000회 미만: 999회
     * - 1,000회 이상 ~ 10,000회 미만: 3.2천
     * - 10,000회 이상 ~ 1억회 미만: 23.4만
     * - 1억회 이상: 1.2억
     */
    fun formatViewCount(viewCount: String): String {
        val count = viewCount.toLongOrNull() ?: return viewCount

        return when {
            count < 1_000 -> "${count}회"
            count < 10_000 -> "${formatToOneDecimal(count / 1_000.0)}천"
            count < 100_000_000 -> "${formatToOneDecimal(count / 10_000.0)}만"
            else -> "${formatToOneDecimal(count / 100_000_000.0)}억"
        }
    }

    private fun parsePublishedAtMillis(isoDateTime: String): Long? {
        PUBLISHED_AT_PATTERNS.forEach { pattern ->
            runCatching {
                val formatter = SimpleDateFormat(pattern, Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                return formatter.parse(isoDateTime)?.time
            }
        }

        return runCatching {
            Instant.parse(isoDateTime).toEpochMilli()
        }.getOrNull()
    }

    private fun monthsBetween(startMillis: Long, endMillis: Long): Long {
        val start = Calendar.getInstance().apply { timeInMillis = startMillis }
        val end = Calendar.getInstance().apply { timeInMillis = endMillis }

        var months = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12 +
            (end.get(Calendar.MONTH) - start.get(Calendar.MONTH))
        if (end.get(Calendar.DAY_OF_MONTH) < start.get(Calendar.DAY_OF_MONTH)) {
            months--
        }
        return months.toLong().coerceAtLeast(0)
    }

    private fun yearsBetween(startMillis: Long, endMillis: Long): Long {
        val start = Calendar.getInstance().apply { timeInMillis = startMillis }
        val end = Calendar.getInstance().apply { timeInMillis = endMillis }

        var years = end.get(Calendar.YEAR) - start.get(Calendar.YEAR)
        if (end.get(Calendar.DAY_OF_YEAR) < start.get(Calendar.DAY_OF_YEAR)) {
            years--
        }
        return years.toLong().coerceAtLeast(0)
    }

    private fun formatToOneDecimal(value: Double): String {
        val rounded = (value * 10).roundToInt() / 10.0
        return String.format(Locale.KOREA, "%.1f", rounded)
    }

    // YouTube API duration 형식: PT(시간H)(분M)(초S)
    private val DURATION_REGEX = Regex("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?")

    private val PUBLISHED_AT_PATTERNS = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
    )
}
