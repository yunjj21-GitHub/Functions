package com.yjp.functions.ui.webview

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.yjp.functions.R
import com.yjp.functions.util.WebViewDownloadUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    url: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 다운로드 처리 공통 함수 — DownloadListener / WebViewClient 둘 다 여기로 모음
    val startDownload = remember(context, scope) {
        { downloadUrl: String, userAgent: String?, contentDisposition: String?, mimeType: String? ->
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    WebViewDownloadUtil.download(
                        context = context,
                        url = downloadUrl,
                        userAgent = userAgent,
                        contentDisposition = contentDisposition,
                        mimeType = mimeType,
                    )
                }

                val message = result.fold(
                    onSuccess = { "다운로드 폴더에 저장되었습니다" },
                    onFailure = { "다운로드에 실패했습니다" },
                )
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        AndroidView(
            factory = { webContext ->
                WebView(webContext).apply {
                    CookieManager.getInstance().setAcceptCookie(true)
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadsImagesAutomatically = true

                    // 1) 서버가 attachment로 내려주면 WebView가 DownloadListener 호출
                    setDownloadListener(DownloadListener { downloadUrl, userAgent, contentDisposition, mimeType, _ ->
                        startDownload(downloadUrl, userAgent, contentDisposition, mimeType)
                    })

                    // 2) PDF 링크가 페이지 이동으로 처리되면 여기서 가로채서 다운로드
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest,
                        ): Boolean {
                            val downloadUrl = request.url.toString()
                            if (WebViewDownloadUtil.shouldDownload(downloadUrl)) {
                                startDownload(
                                    downloadUrl,
                                    view.settings.userAgentString,
                                    null,
                                    null,
                                )
                                return true
                            }
                            return false
                        }
                    }

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
