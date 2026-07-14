package com.yjp.functions.ui.webview

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.yjp.functions.R

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    url: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 8.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "뒤로",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

object WebViewUrls {
    const val DATA_GO_KR_PDF =
        "https://www.data.go.kr/bbs/rcr/selectRecsroom.do?pageIndex=1&originId=PDS_0000000001255+++&atchFileId=FILE_000000003599310&searchCondition3=&searchCondition2=&cndCtgryLaword=Y&cndCtgryEdc=Y&cndCtgryBigdata=Y&cndCtgryStd=Y&cndCtgryNews=Y&cndCtgryContest=&cndCtgryEtc=Y&cndCtgryCardNews=&bindCndCtgry=&sort-post=2&searchKeyword1=&Laword=PDTY01&Edc=PDTY02&Bigdata=PDTY03&Std=PDTY04&News=PDTY05&Etc=PDTY06"
}
