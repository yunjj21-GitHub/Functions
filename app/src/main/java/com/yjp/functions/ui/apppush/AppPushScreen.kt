package com.yjp.functions.ui.apppush

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjp.functions.ui.theme.FunctionsTheme
import com.yjp.functions.util.FcmUtil

@Composable
fun FcmScreen(
    viewModel: AppPushViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val token by viewModel.token.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isNotificationPermissionGranted by
        viewModel.isNotificationPermissionGranted.collectAsStateWithLifecycle()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        viewModel.onNotificationPermissionResult(isGranted)
    }

    // 화면 진입 시 알림 권한 확인/요청
    LaunchedEffect(Unit) {
        FcmUtil.requestNotificationPermissionIfNeeded(
            context = context,
            onAlreadyGranted = {
                viewModel.onNotificationPermissionResult(true)
            },
            onRequestPermission = {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        )
    }

    FcmScreenContent(
        token = token,
        errorMessage = errorMessage,
        isNotificationPermissionGranted = isNotificationPermissionGranted,
        modifier = modifier,
    )
}

@Composable
private fun FcmScreenContent(
    token: String?,
    errorMessage: String?,
    isNotificationPermissionGranted: Boolean?,
    modifier: Modifier = Modifier,
) {
    val permissionText = when (isNotificationPermissionGranted) {
        true -> "허용"
        false -> "거부"
        null -> "확인 중…"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "알림 권한 허용여부",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = permissionText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.padding(2.dp))

        Text(
            text = "FCM 토큰",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        when {
            errorMessage != null -> {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            token == null -> {
                Text(
                    text = "토큰 생성 중…",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            else -> {
                Text(
                    text = token,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FcmScreenContentPreview() {
    FunctionsTheme {
        FcmScreenContent(
            token = "sample-fcm-token-abcdefghijklmnopsample-fcm-token-abcdefghijklmnopsample-fcm-token-abcdefghijklmnopsample-fcm-token-abcdefghijklmnop",
            errorMessage = null,
            isNotificationPermissionGranted = true,
        )
    }
}
