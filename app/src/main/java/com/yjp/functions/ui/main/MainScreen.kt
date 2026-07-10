package com.yjp.functions.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yjp.functions.ui.youtube.YoutubeScreen
import com.yjp.functions.ui.youtube.YoutubeViewModel
import com.yjp.functions.ui.youtube.player.YoutubePlayerScreen

@Composable
fun MainScreen() {
    // 화면 이동을 관리하는 컨트롤러
    val navController = rememberNavController()

    // 지금 보고 있는 화면 정보를 가져옴
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = MainDestination.isTopLevel(currentRoute)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        // Scaffold가 시스템 바 패딩을 자동으로 넣지 않게 함 (직접 처리함)
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onItemClick = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        // 화면들이 들어가는 컨테이너
        NavHost(
            navController = navController,
            startDestination = MainDestination.Youtube.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // 하단 네비게이션바 높이만큼 본문을 위로 밀어줌
                .windowInsetsPadding(WindowInsets.statusBars), // 상태바 아래부터 그림
        ) {
            // 1-Depth 화면 (하단 네비게이션바 노출)
            composable(
                route = MainDestination.Youtube.route
            ) {
                YoutubeScreen(
                    viewModel = hiltViewModel<YoutubeViewModel>(),
                    onVideoClick = { videoId ->
                        navController.navigate("youtube/player/$videoId")
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(
                route = MainDestination.Pdf.route
            ) {
                PlaceholderScreen(title = "PDF")
            }

            composable(
                route = MainDestination.Image.route
            ) {
                PlaceholderScreen(title = "Image")
            }

            composable(
                route = MainDestination.Fingerprint.route
            ) {
                PlaceholderScreen(title = "Fingerprint")
            }

            // 2-Depth 이상 화면 (하단 네비게이션바 미노출)
            composable(
                route = "youtube/player/{videoId}",
                arguments = listOf(
                    navArgument("videoId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val videoId = backStackEntry.arguments?.getString("videoId").orEmpty()

                YoutubePlayerScreen(
                    videoId = videoId,
                    onBack = { navController.popBackStack() }, // 뒤로가면 목록으로 복귀함
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

/** 아직 만들지 않은 탭 화면용 임시 UI */
@Composable
private fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}
