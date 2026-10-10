package com.example.util

import android.net.Uri

object AdFilter {

    private val BLOCKED_KEYWORDS = listOf(
        "adsterra", "popads", "propellerads", "exoclick", "syndication",
        "juicyads", "trafficjunky", "bet88", "slot88", "gacor", "judi",
        "casino", "poker", "togel", "1xbet", "sbobet", "mpo", "rtp-live",
        "onclickpredictiv", "adnxs", "doubleclick.net/pagead", "doublepimp",
        "yllix", "ad-maven", "monetag", "directrev", "adkeeper", "clickadu",
        "hilltopads", "richads", "galaksislot", "zeus", "pragmatic", "maxwin",
        "apk-download", "trackvoluum", "deloplen", "wpadmngr"
    )

    private val SAFE_STREAM_DOMAINS = listOf(
        "lk21", "layarkaca21", "dunia21", "indoxxi", "stream", "player",
        "embed", "video", "cdn", "cloud", "googlevideo", "youtube",
        "streamwish", "filelions", "doodstream", "vidhide", "mixdrop",
        "upstream", "dropload", "turboviplay", "streamsb", "mp4upload"
    )

    fun isAdOrPopup(url: String?): Boolean {
        if (url.isNullOrBlank()) return false

        val lowerUrl = url.lowercase()

        // Explicitly check blocked keywords
        for (keyword in BLOCKED_KEYWORDS) {
            if (lowerUrl.contains(keyword)) {
                return true
            }
        }

        // Suspicious schemes or downloads
        if (lowerUrl.endsWith(".apk") || lowerUrl.contains("download_apk") || lowerUrl.contains("install_cleaner")) {
            return true
        }

        return false
    }

    fun isMovieDomain(url: String?, baseHost: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val uri = try { Uri.parse(url) } catch (_: Exception) { return false }
        val host = uri.host?.lowercase() ?: return false

        if (baseHost != null && host.contains(baseHost.lowercase())) {
            return true
        }

        for (domain in SAFE_STREAM_DOMAINS) {
            if (host.contains(domain)) {
                return true
            }
        }

        return false
    }
}
