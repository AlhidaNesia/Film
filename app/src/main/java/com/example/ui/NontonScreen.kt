package com.example.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.MovieCategory
import com.example.ui.components.BookmarksBottomSheet
import com.example.ui.components.CategoryChipsRow
import com.example.ui.components.CinemaBottomNavBar
import com.example.ui.components.CinemaTopAppBar
import com.example.ui.components.ErrorScreen
import com.example.ui.components.FullscreenVideoOverlay
import com.example.ui.components.MirrorSelectionDialog
import com.example.ui.components.SearchDialog
import com.example.ui.theme.CinemaBg
import com.example.util.AdFilter
import com.example.viewmodel.NontonViewModel
import com.example.viewmodel.WebCommand
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NontonScreen(
    viewModel: NontonViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentUrl by viewModel.currentUrl.collectAsState()
    val pageTitle by viewModel.pageTitle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val progress by viewModel.loadingProgress.collectAsState()
    val canGoBack by viewModel.canGoBack.collectAsState()
    val canGoForward by viewModel.canGoForward.collectAsState()
    val isDesktopMode by viewModel.isDesktopMode.collectAsState()
    val isAdBlockEnabled by viewModel.isAdBlockEnabled.collectAsState()
    val blockedAdsCount by viewModel.blockedAdsCount.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val isOfflineError by viewModel.isOfflineError.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val activeMirror by viewModel.activeMirror.collectAsState()
    val customFullscreenView by viewModel.customFullscreenView.collectAsState()

    val showMirrorDialog by viewModel.showMirrorDialog.collectAsState()
    val showBookmarksSheet by viewModel.showBookmarksSheet.collectAsState()
    val showSearchDialog by viewModel.showSearchDialog.collectAsState()

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var defaultUserAgent by remember { mutableStateOf<String?>(null) }

    val isCurrentBookmarked = remember(bookmarks, currentUrl) {
        bookmarks.any { it.url == currentUrl }
    }

    // Collect snackbar notifications
    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Back button handling
    BackHandler(enabled = customFullscreenView != null || canGoBack) {
        if (customFullscreenView != null) {
            viewModel.hideCustomFullscreenView()
        } else if (canGoBack && webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        }
    }

    // Process WebView commands from ViewModel
    LaunchedEffect(webViewInstance) {
        viewModel.webCommand.collectLatest { command ->
            val webView = webViewInstance ?: return@collectLatest
            when (command) {
                is WebCommand.LoadUrl -> webView.loadUrl(command.url)
                is WebCommand.GoBack -> {
                    if (webView.canGoBack()) webView.goBack()
                }
                is WebCommand.GoForward -> {
                    if (webView.canGoForward()) webView.goForward()
                }
                is WebCommand.Reload -> webView.reload()
                is WebCommand.StopLoading -> webView.stopLoading()
                is WebCommand.SetDesktopMode -> {
                    val settings = webView.settings
                    if (command.enable) {
                        settings.userAgentString = DESKTOP_USER_AGENT
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                    } else {
                        settings.userAgentString = defaultUserAgent
                    }
                    webView.reload()
                }
                is WebCommand.ClearCache -> {
                    webView.clearCache(true)
                    webView.clearHistory()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (customFullscreenView == null) {
                CinemaTopAppBar(
                    pageTitle = pageTitle,
                    currentUrl = currentUrl,
                    isLoading = isLoading,
                    progress = progress,
                    blockedAdsCount = blockedAdsCount,
                    isAdBlockEnabled = isAdBlockEnabled,
                    isDesktopMode = isDesktopMode,
                    onSearchClick = { viewModel.setShowSearchDialog(true) },
                    onMirrorClick = { viewModel.setShowMirrorDialog(true) },
                    onReloadClick = { viewModel.reload() },
                    onToggleDesktop = { viewModel.toggleDesktopMode() },
                    onToggleAdBlock = { viewModel.toggleAdBlock() },
                    onClearCache = { viewModel.clearBrowserCache() }
                )
            }
        },
        bottomBar = {
            if (customFullscreenView == null) {
                CinemaBottomNavBar(
                    canGoBack = canGoBack,
                    canGoForward = canGoForward,
                    isBookmarked = isCurrentBookmarked,
                    onBackClick = { viewModel.goBack() },
                    onForwardClick = { viewModel.goForward() },
                    onHomeClick = { viewModel.goHome() },
                    onToggleBookmarkClick = { viewModel.toggleBookmark() },
                    onOpenBookmarksClick = { viewModel.setShowBookmarksSheet(true) },
                    onReloadClick = { viewModel.reload() }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CinemaBg)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Movie Category Quick Filter Pills
                CategoryChipsRow(
                    categories = MovieCategory.CATEGORIES,
                    currentUrl = currentUrl,
                    onCategoryClick = { category ->
                        viewModel.selectCategory(category)
                    }
                )

                // Embedded Web Player Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                defaultUserAgent = settings.userAgentString

                                val settings: WebSettings = settings
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.databaseEnabled = true
                                settings.mediaPlaybackRequiresUserGesture = false
                                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                settings.allowFileAccess = true
                                settings.allowContentAccess = true
                                settings.useWideViewPort = true
                                settings.loadWithOverviewMode = true
                                settings.setSupportZoom(true)
                                settings.builtInZoomControls = true
                                settings.displayZoomControls = false
                                settings.cacheMode = WebSettings.LOAD_DEFAULT
                                settings.setSupportMultipleWindows(true)

                                if (isDesktopMode) {
                                    settings.userAgentString = DESKTOP_USER_AGENT
                                }

                                webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(
                                        view: WebView,
                                        request: WebResourceRequest
                                    ): Boolean {
                                        val url = request.url.toString()

                                        // 1. Check Ad & Popup blocker
                                        if (viewModel.isAdBlockEnabled.value && AdFilter.isAdOrPopup(url)) {
                                            viewModel.onAdBlocked()
                                            return true // Block navigation
                                        }

                                        // 2. Handle external non-web schemes (intent, tel, mailto, etc.)
                                        val scheme = request.url.scheme
                                        if (scheme != "http" && scheme != "https") {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, request.url)
                                                ctx.startActivity(intent)
                                            } catch (_: Exception) {
                                            }
                                            return true
                                        }

                                        return false
                                    }

                                    override fun onPageStarted(
                                        view: WebView,
                                        url: String,
                                        favicon: Bitmap?
                                    ) {
                                        viewModel.onPageStarted(url)
                                    }

                                    override fun onPageFinished(view: WebView, url: String) {
                                        viewModel.onPageFinished(
                                            url = url,
                                            canBack = view.canGoBack(),
                                            canForward = view.canGoForward()
                                        )
                                    }

                                    override fun onReceivedError(
                                        view: WebView,
                                        request: WebResourceRequest,
                                        error: WebResourceError
                                    ) {
                                        if (request.isForMainFrame) {
                                            viewModel.onReceivedError(
                                                description = error.description.toString(),
                                                failingUrl = request.url.toString()
                                            )
                                        }
                                    }
                                }

                                webChromeClient = object : WebChromeClient() {
                                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                                        viewModel.onProgressChanged(newProgress)
                                    }

                                    override fun onReceivedTitle(view: WebView, title: String?) {
                                        viewModel.onReceivedTitle(title)
                                    }

                                    override fun onShowCustomView(
                                        view: android.view.View,
                                        callback: CustomViewCallback
                                    ) {
                                        viewModel.showCustomFullscreenView(view, callback)
                                    }

                                    override fun onHideCustomView() {
                                        viewModel.hideCustomFullscreenView()
                                    }

                                    override fun onCreateWindow(
                                        view: WebView?,
                                        isDialog: Boolean,
                                        isUserGesture: Boolean,
                                        resultMsg: Message?
                                    ): Boolean {
                                        // Intercept popups so ads cannot open secondary window
                                        if (resultMsg != null) {
                                            val transport = resultMsg.obj as? WebView.WebViewTransport
                                            if (transport != null && view != null) {
                                                transport.webView = view
                                                resultMsg.sendToTarget()
                                                return true
                                            }
                                        }
                                        return false
                                    }
                                }

                                loadUrl(viewModel.currentUrl.value)
                                webViewInstance = this
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("nonton_webview")
                    )

                    // Error Screen Overlay if connection fails
                    if (isOfflineError) {
                        ErrorScreen(
                            errorMessage = errorMessage,
                            failedUrl = currentUrl,
                            onRetry = { viewModel.reload() },
                            onChangeMirror = { viewModel.setShowMirrorDialog(true) }
                        )
                    }
                }
            }

            // HTML5 Video Fullscreen Overlay
            customFullscreenView?.let { customView ->
                FullscreenVideoOverlay(
                    customView = customView,
                    onExitFullscreen = { viewModel.hideCustomFullscreenView() }
                )
            }
        }
    }

    // Mirror Server Switcher Dialog
    if (showMirrorDialog) {
        MirrorSelectionDialog(
            currentMirror = activeMirror,
            onSelectMirror = { mirror -> viewModel.selectMirror(mirror) },
            onCustomUrlSubmit = { customUrl ->
                viewModel.setShowMirrorDialog(false)
                viewModel.navigateToUrl(customUrl)
            },
            onDismiss = { viewModel.setShowMirrorDialog(false) }
        )
    }

    // Bookmarks Sheet
    if (showBookmarksSheet) {
        BookmarksBottomSheet(
            bookmarks = bookmarks,
            onSelectBookmark = { item ->
                viewModel.setShowBookmarksSheet(false)
                viewModel.navigateToUrl(item.url)
            },
            onDeleteBookmark = { id -> viewModel.removeBookmark(id) },
            onDismiss = { viewModel.setShowBookmarksSheet(false) }
        )
    }

    // Search Movie / Jump URL Dialog
    if (showSearchDialog) {
        SearchDialog(
            onSearchMovie = { query -> viewModel.searchMovies(query) },
            onNavigateUrl = { url -> viewModel.navigateToUrl(url) },
            onDismiss = { viewModel.setShowSearchDialog(false) }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewInstance?.destroy()
            webViewInstance = null
        }
    }
}
