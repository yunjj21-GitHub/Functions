package com.yjp.functions.ui.apppush

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjp.functions.ui.theme.FunctionsTheme

@Composable
fun FcmScreen(
    viewModel: AppPushViewModel,
    modifier: Modifier = Modifier,
) {
    val token by viewModel.token.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    FcmScreenContent(
        token = token,
        errorMessage = errorMessage,
        modifier = modifier,
    )
}

@Composable
private fun FcmScreenContent(
    token: String?,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
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
                    text = "토큰을 불러오는 중…",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            else -> {
                SelectionContainer {
                    Text(
                        text = token,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
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
        )
    }
}
