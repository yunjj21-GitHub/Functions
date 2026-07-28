package com.yjp.functions.ui.apppush

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
    val title by viewModel.title.collectAsStateWithLifecycle()
    val body by viewModel.body.collectAsStateWithLifecycle()
    val sendMessage by viewModel.sendMessage.collectAsStateWithLifecycle()
    val isSending by viewModel.isSending.collectAsStateWithLifecycle()

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
            },
        )
    }

    LaunchedEffect(sendMessage) {
        sendMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onSendMessageShown()
        }
    }

    FcmScreenContent(
        token = token,
        errorMessage = errorMessage,
        isNotificationPermissionGranted = isNotificationPermissionGranted,
        title = title,
        body = body,
        isSending = isSending,
        onTitleChange = viewModel::onTitleChange,
        onBodyChange = viewModel::onBodyChange,
        onSendPushClick = viewModel::sendPush,
        modifier = modifier,
    )
}

@Composable
private fun FcmScreenContent(
    token: String?,
    errorMessage: String?,
    isNotificationPermissionGranted: Boolean?,
    title: String,
    body: String,
    isSending: Boolean,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onSendPushClick: () -> Unit,
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
            fontWeight = FontWeight.Bold,
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

        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = "푸시 보내기",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "제목",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        // 제목: 1줄 입력
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("제목을 입력하세요") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        Text(
            text = "내용",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        // 내용: 5줄 입력
        OutlinedTextField(
            value = body,
            onValueChange = onBodyChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            minLines = 5,
            maxLines = 5,
            placeholder = { Text("내용을 입력하세요") },
        )

        SendPushButton(
            text = if (isSending) "전송 중…" else "보내기",
            enabled = !isSending && !token.isNullOrBlank(),
            onClick = onSendPushClick,
        )
    }
}

@Composable
private fun SendPushButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) Color.Black else Color(0xFFB0B0B0))
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
        )
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
            title = "테스트 제목",
            body = "테스트 내용",
            isSending = false,
            onTitleChange = {},
            onBodyChange = {},
            onSendPushClick = {},
        )
    }
}
