package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import com.example.data.NontonPreferences
import com.example.model.BookmarkItem
import com.example.model.MirrorDomain
import com.example.model.MovieCategory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class WebCommand {
    data class LoadUrl(val url: String) : WebCommand()
    object GoBack : WebCommand()
    object GoForward : WebCommand()
    object Reload : WebCommand()
    object StopLoading : WebCommand()
    data class SetDesktopMode(val enable: Boolean) : WebCommand()
    object ClearCache : WebCommand()
}

class NontonViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = NontonPreferences(application)

    private val _currentUrl = MutableStateFlow(preferences.getInitialUrl())
    val currentUrl: StateFlow<String> = _currentUrl.asStateFlow()

    private val _pageTitle = MutableStateFlow("Nonton - Layar Kaca Streaming")
    val pageTitle: StateFlow<String> = _pageTitle.asStateFlow()

    private val _loadingProgress = MutableStateFlow(0)
    val loadingProgress: StateFlow<Int> = _loadingProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()

    private val _isDesktopMode = MutableStateFlow(preferences.isDesktopMode())
    val isDesktopMode: StateFlow<Boolean> = _isDesktopMode.asStateFlow()

    private val _isAdBlockEnabled = MutableStateFlow(preferences.isAdBlockEnabled())
    val isAdBlockEnabled: StateFlow<Boolean> = _isAdBlockEnabled.asStateFlow()

    private val _blockedAdsCount = MutableStateFlow(0)
    val blockedAdsCount: StateFlow<Int> = _blockedAdsCount.asStateFlow()

    private val _bookmarks = MutableStateFlow(preferences.getBookmarks())
    val bookmarks: StateFlow<List<BookmarkItem>> = _bookmarks.asStateFlow()

    private val _isOfflineError = MutableStateFlow(false)
    val isOfflineError: StateFlow<Boolean> = _isOfflineError.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _activeMirror = MutableStateFlow(MirrorDomain.DEFAULT_MIRRORS[0])
    val activeMirror: StateFlow<MirrorDomain> = _activeMirror.asStateFlow()

    // Fullscreen video state from HTML5 WebChromeClient
    private val _customFullscreenView = MutableStateFlow<View?>(null)
    val customFullscreenView: StateFlow<View?> = _customFullscreenView.asStateFlow()
    var customViewCallback: WebChromeClient.CustomViewCallback? = null

    // UI Dialog & Sheet states
    private val _showMirrorDialog = MutableStateFlow(false)
    val showMirrorDialog: StateFlow<Boolean> = _showMirrorDialog.asStateFlow()

    private val _showBookmarksSheet = MutableStateFlow(false)
    val showBookmarksSheet: StateFlow<Boolean> = _showBookmarksSheet.asStateFlow()

    private val _showSearchDialog = MutableStateFlow(false)
    val showSearchDialog: StateFlow<Boolean> = _showSearchDialog.asStateFlow()

    // Commands to the WebView
    private val _webCommand = MutableSharedFlow<WebCommand>(extraBufferCapacity = 10)
    val webCommand: SharedFlow<WebCommand> = _webCommand.asSharedFlow()

    // User toast/snack message event
    private val _userMessage = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    init {
        // Match active mirror to initial URL
        val initial = _currentUrl.value
        val matched = MirrorDomain.DEFAULT_MIRRORS.find { initial.startsWith(it.url) }
        if (matched != null) {
            _activeMirror.value = matched
        }
    }

    fun onPageStarted(url: String) {
        _currentUrl.value = url
        _isLoading.value = true
        _isOfflineError.value = false
        _errorMessage.value = null
        preferences.saveCurrentUrl(url)
    }

    fun onPageFinished(url: String, canBack: Boolean, canForward: Boolean) {
        _currentUrl.value = url
        _isLoading.value = false
        _loadingProgress.value = 100
        _canGoBack.value = canBack
        _canGoForward.value = canForward
        preferences.saveCurrentUrl(url)
    }

    fun onProgressChanged(newProgress: Int) {
        _loadingProgress.value = newProgress
        _isLoading.value = newProgress < 100
    }

    fun onReceivedTitle(title: String?) {
        if (!title.isNullOrBlank() && !title.startsWith("http")) {
            _pageTitle.value = title
        }
    }

    fun onReceivedError(description: String, failingUrl: String) {
        _isLoading.value = false
        _isOfflineError.value = true
        _errorMessage.value = description
    }

    fun onAdBlocked() {
        _blockedAdsCount.value += 1
    }

    fun navigateToUrl(url: String) {
        var cleanUrl = url.trim()
        if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
            cleanUrl = "https://$cleanUrl"
        }
        _currentUrl.value = cleanUrl
        _isOfflineError.value = false
        _webCommand.tryEmit(WebCommand.LoadUrl(cleanUrl))
    }

    fun searchMovies(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        val baseUrl = _activeMirror.value.url.trimEnd('/')
        // Layarkaca search query format: ?s=movie_name
        val searchUrl = "$baseUrl/?s=${android.net.Uri.encode(trimmed)}"
        navigateToUrl(searchUrl)
    }

    fun reload() {
        _isOfflineError.value = false
        _webCommand.tryEmit(WebCommand.Reload)
    }

    fun goBack() {
        if (_customFullscreenView.value != null) {
            hideCustomFullscreenView()
        } else {
            _webCommand.tryEmit(WebCommand.GoBack)
        }
    }

    fun goForward() {
        _webCommand.tryEmit(WebCommand.GoForward)
    }

    fun goHome() {
        navigateToUrl(_activeMirror.value.url)
    }

    fun selectCategory(category: MovieCategory) {
        val baseUrl = _activeMirror.value.url.trimEnd('/')
        val targetUrl = if (category.path.isEmpty()) {
            "$baseUrl/"
        } else {
            "$baseUrl/${category.path}"
        }
        navigateToUrl(targetUrl)
    }

    fun selectMirror(mirror: MirrorDomain) {
        _activeMirror.value = mirror
        setShowMirrorDialog(false)
        navigateToUrl(mirror.url)
        _userMessage.tryEmit("Beralih ke ${mirror.name}")
    }

    fun toggleBookmark() {
        val current = _currentUrl.value
        val title = _pageTitle.value
        val existing = _bookmarks.value.find { it.url == current }

        if (existing != null) {
            _bookmarks.value = preferences.removeBookmark(existing.id)
            _userMessage.tryEmit("Dihapus dari Favorit")
        } else {
            val newItem = BookmarkItem(title = title, url = current)
            _bookmarks.value = preferences.addBookmark(newItem)
            _userMessage.tryEmit("Disimpan ke Favorit!")
        }
    }

    fun removeBookmark(id: String) {
        _bookmarks.value = preferences.removeBookmark(id)
        _userMessage.tryEmit("Favorit dihapus")
    }

    fun toggleDesktopMode() {
        val newMode = !_isDesktopMode.value
        _isDesktopMode.value = newMode
        preferences.setDesktopMode(newMode)
        _webCommand.tryEmit(WebCommand.SetDesktopMode(newMode))
        _userMessage.tryEmit(if (newMode) "Mode Desktop Aktif" else "Mode Ponsel Aktif")
    }

    fun toggleAdBlock() {
        val newMode = !_isAdBlockEnabled.value
        _isAdBlockEnabled.value = newMode
        preferences.setAdBlockEnabled(newMode)
        _userMessage.tryEmit(if (newMode) "Pelindung Iklan Aktif" else "Pelindung Iklan Dinonaktifkan")
    }

    fun resetAdBlockedCount() {
        _blockedAdsCount.value = 0
    }

    fun clearBrowserCache() {
        _webCommand.tryEmit(WebCommand.ClearCache)
        _userMessage.tryEmit("Cache dibersihkan")
    }

    // HTML5 Video Fullscreen Handling
    fun showCustomFullscreenView(view: View, callback: WebChromeClient.CustomViewCallback) {
        _customFullscreenView.value = view
        customViewCallback = callback
    }

    fun hideCustomFullscreenView() {
        customViewCallback?.onCustomViewHidden()
        customViewCallback = null
        _customFullscreenView.value = null
    }

    fun isOnline(): Boolean {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Dialog state setters
    fun setShowMirrorDialog(show: Boolean) {
        _showMirrorDialog.value = show
    }

    fun setShowBookmarksSheet(show: Boolean) {
        _showBookmarksSheet.value = show
    }

    fun setShowSearchDialog(show: Boolean) {
        _showSearchDialog.value = show
    }
}
