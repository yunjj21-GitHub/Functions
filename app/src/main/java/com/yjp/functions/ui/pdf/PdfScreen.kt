package com.yjp.functions.ui.pdf

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjp.functions.ui.theme.FunctionsTheme

@Composable
fun PdfScreen(
    viewModel: PdfViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val downloadMessage by viewModel.downloadMessage.collectAsStateWithLifecycle()
    val uploadMessage by viewModel.uploadMessage.collectAsStateWithLifecycle()
    val uploadedFileNames by viewModel.uploadedFileNames.collectAsStateWithLifecycle()

    val pickPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let { viewModel.uploadPdf(context, it) }
    }

    LaunchedEffect(downloadMessage) {
        downloadMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onDownloadMessageShown()
        }
    }

    LaunchedEffect(uploadMessage) {
        uploadMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onUploadMessageShown()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // 위 여백 — 버튼이 화면 중앙에 오도록 맞춤
        Spacer(modifier = Modifier.weight(1f))

        PdfActionButton(
            text = "PDF 다운로드하기",
            onClick = { viewModel.downloadPdf(context) },
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        )
        PdfActionButton(
            text = "PDF 업로드하기",
            onClick = { pickPdfLauncher.launch(arrayOf("application/pdf")) },
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        )

        // 아래 영역 — 첨부 목록만 스크롤 (버튼 위치는 그대로)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (uploadedFileNames.isNotEmpty()) {
                UploadedFileList(
                    fileNames = uploadedFileNames,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun UploadedFileList(
    fileNames: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "첨부된 파일 목록",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp),
        )
        fileNames.forEach { fileName ->
            Text(
                text = fileName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun PdfActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
            .then(
                if (onClick != null) {
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
private fun PdfScreenPreview() {
    FunctionsTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            PdfActionButton(
                text = "PDF 다운로드하기",
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
            )
            PdfActionButton(
                text = "PDF 업로드하기",
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                UploadedFileList(
                    fileNames = listOf(
                        "[샘플] 광고1번지.pdf",
                        "보고서.pdf",
                        "계약서.pdf",
                        "안내문.pdf",
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                )
            }
        }
    }
}
