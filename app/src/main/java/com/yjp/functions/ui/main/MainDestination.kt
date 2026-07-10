package com.yjp.functions.ui.main

import androidx.annotation.DrawableRes
import com.yjp.functions.R

/**
 * 하단 네비게이션 바에 들어가는 탭 정보를 모아둔 enum
 *
 * - [route] : Navigation에서 쓰는 화면 이름 (주소)
 * - [iconRes] : 하단 바에 보여줄 아이콘
 * - [contentDescription] : 접근성용 설명
 *
 * youtube / pdf / image / fingerprint 4개가 원뎁스(1층) 탭임
 */
enum class MainDestination(
    val route: String,
    @DrawableRes val iconRes: Int,
    val contentDescription: String,
) {
    Youtube(
        route = "youtube",
        iconRes = R.drawable.ic_youtube,
        contentDescription = "YouTube",
    ),
    Pdf(
        route = "pdf",
        iconRes = R.drawable.ic_pdf,
        contentDescription = "PDF",
    ),
    Image(
        route = "image",
        iconRes = R.drawable.ic_image,
        contentDescription = "Image",
    ),
    Fingerprint(
        route = "fingerprint",
        iconRes = R.drawable.ic_fingerprint,
        contentDescription = "Fingerprint",
    ),
    ;

    companion object {
        private val routeSet: Set<String> = entries.map { it.route }.toSet()

        /** 원뎁스(탭) 화면이면 true, 하위 화면이면 false */
        fun isTopLevel(route: String?): Boolean = route in routeSet
    }
}
