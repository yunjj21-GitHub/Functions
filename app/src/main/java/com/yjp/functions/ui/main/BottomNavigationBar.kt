package com.yjp.functions.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yjp.functions.ui.theme.FunctionsTheme

/**
 * 화면 하단에 붙는 네비게이션 바 UI
 *
 * - 아이콘 4개를 동일한 너비로 배치함
 * - 상단 좌·우 둥글게 처리함
 * - 그림자를 조금 넣음
 */
@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onItemClick: (MainDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = Color.White,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // 시스템 내비게이션 바(제스처 바) 높이만큼 아래 패딩을 줌
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MainDestination.entries.forEach { destination ->
                BottomNavIcon(
                    destination = destination,
                    selected = currentRoute == destination.route,
                    onClick = { onItemClick(destination) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BottomNavIcon(
    destination: MainDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(destination.iconRes),
        contentDescription = destination.contentDescription,
        modifier = modifier
            .height(32.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // 클릭 시 ripple 효과를 끔
                onClick = onClick,
            ),
        contentScale = ContentScale.Fit,
        colorFilter = if (selected) {
            null // 선택된 탭은 원본 아이콘 색
        } else {
            ColorFilter.tint(Color(0xFFB0B0B0)) // 선택 안 된 탭은 회색 처리
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F2)
@Composable
private fun BottomNavigationBarYoutubeSelectedPreview() {
    FunctionsTheme {
        BottomNavigationBar(
            currentRoute = MainDestination.Youtube.route,
            onItemClick = {},
        )
    }
}