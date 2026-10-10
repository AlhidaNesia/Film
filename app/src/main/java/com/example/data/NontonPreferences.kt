package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.BookmarkItem
import com.example.model.MirrorDomain
import org.json.JSONArray
import org.json.JSONObject

class NontonPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nonton_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BOOKMARKS = "key_bookmarks"
        private const val KEY_CURRENT_URL = "key_current_url"
        private const val KEY_AD_BLOCK_ENABLED = "key_ad_block_enabled"
        private const val KEY_DESKTOP_MODE = "key_desktop_mode"
    }

    fun getInitialUrl(): String {
        return prefs.getString(KEY_CURRENT_URL, MirrorDomain.DEFAULT_MIRRORS[0].url)
            ?: MirrorDomain.DEFAULT_MIRRORS[0].url
    }

    fun saveCurrentUrl(url: String) {
        prefs.edit().putString(KEY_CURRENT_URL, url).apply()
    }

    fun isAdBlockEnabled(): Boolean {
        return prefs.getBoolean(KEY_AD_BLOCK_ENABLED, true)
    }

    fun setAdBlockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AD_BLOCK_ENABLED, enabled).apply()
    }

    fun isDesktopMode(): Boolean {
        return prefs.getBoolean(KEY_DESKTOP_MODE, false)
    }

    fun setDesktopMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DESKTOP_MODE, enabled).apply()
    }

    fun getBookmarks(): List<BookmarkItem> {
        val jsonString = prefs.getString(KEY_BOOKMARKS, null) ?: return emptyList()
        val list = mutableListOf<BookmarkItem>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    BookmarkItem(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        title = obj.optString("title", "Film"),
                        url = obj.optString("url", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {
            // fallback
        }
        return list
    }

    fun addBookmark(item: BookmarkItem): List<BookmarkItem> {
        val current = getBookmarks().toMutableList()
        // Check if already bookmarked
        current.removeAll { it.url == item.url }
        current.add(0, item)
        saveBookmarks(current)
        return current
    }

    fun removeBookmark(id: String): List<BookmarkItem> {
        val current = getBookmarks().filter { it.id != id }
        saveBookmarks(current)
        return current
    }

    private fun saveBookmarks(items: List<BookmarkItem>) {
        val jsonArray = JSONArray()
        for (item in items) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("url", item.url)
                put("timestamp", item.timestamp)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_BOOKMARKS, jsonArray.toString()).apply()
    }
}
