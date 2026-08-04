package com.yjp.functions.ui.fingerprint

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjp.functions.R
import com.yjp.functions.ui.theme.FunctionsTheme
@Composable
fun FingerprintScreen(
    viewModel: FingerprintViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val authResult by viewModel.authResult.collectAsStateWithLifecycle()

    FingerprintScreenContent(
        authResult = authResult,
        onFingerprintClick = {
            (context as? FragmentActivity)?.let(viewModel::authenticate)
        },
        modifier = modifier,
    )
}

@Composable
private fun FingerprintScreenContent(
    authResult: String,
    onFingerprintClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_fingerprint),
            contentDescription = "Fingerprint",
            modifier = Modifier
                .size(72.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onFingerprintClick,
                ),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "생체 인증 결과: $authResult",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FingerprintScreenPreview() {
    FunctionsTheme {
        FingerprintScreenContent(
            authResult = "미인증",
            onFingerprintClick = {},
        )
    }
}
