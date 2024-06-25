package com.dtran.scanner.ui.screen.web

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.dtran.scanner.ui.widget.ProgressIndicator
import com.dtran.scanner.ui.widget.TopBar

@Composable
@SuppressLint("SetJavaScriptEnabled")
fun WebScreen(
    url: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val progressIndicator = remember { mutableStateOf(false) }
    var webView: WebView? = remember { null }

    BackHandler {
        webView?.let { if (it.canGoBack()) it.goBack() else navController.popBackStack() }
            ?: run { navController.popBackStack() }
    }

    Scaffold(topBar = {
        TopBar(isHome = false, title = "Web", onBackArrowPressed = {
            webView?.let { if (it.canGoBack()) it.goBack() else navController.popBackStack() }
                ?: run { navController.popBackStack() }
        })
    }) {
        AndroidView(modifier = modifier.padding(it), factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.apply {
                    javaScriptEnabled = true
                    displayZoomControls = false
                    useWideViewPort = true
                    builtInZoomControls = true
                    loadWithOverviewMode = true
                    javaScriptCanOpenWindowsAutomatically = true
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        if (request.url.toString().endsWith(".pdf")) {
//                            view.settings.apply {
//                                allowFileAccess = true
//                                allowFileAccessFromFileURLs = true
//                                allowUniversalAccessFromFileURLs = true
//                            }
                            loadUrl("https://docs.google.com/gview?embedded=true&url=${request.url}")
//                            loadUrl("file:///android_asset/pdfjs/index.html?${request.url}")
//                            loadUrl("file:///android_asset/pdfjs_mobile/web/viewer.html?file=${request.url}")
                            return true
                        } else return super.shouldOverrideUrlLoading(view, request)
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        progressIndicator.value = newProgress in 0..99
                    }
                }
                loadUrl(url)
            }
        }, update = { wv -> webView = wv })
    }

    ProgressIndicator(showProgressBarState = progressIndicator.value, modifier = modifier.fillMaxSize())
}