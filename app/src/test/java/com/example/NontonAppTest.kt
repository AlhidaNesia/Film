package com.example

import com.example.model.MirrorDomain
import com.example.model.MovieCategory
import com.example.util.AdFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NontonAppTest {

    @Test
    fun defaultMirrors_areValid() {
        val mirrors = MirrorDomain.DEFAULT_MIRRORS
        assertTrue(mirrors.isNotEmpty())
        assertEquals("https://tv12.lk21official.cc/", mirrors[0].url)
    }

    @Test
    fun movieCategories_containEssentialGenres() {
        val categories = MovieCategory.CATEGORIES
        assertTrue(categories.any { it.title == "Beranda" })
        assertTrue(categories.any { it.title == "Action" })
        assertTrue(categories.any { it.title == "Horror" })
    }

    @Test
    fun adFilter_blocksKnownSpamNetworks() {
        assertTrue(AdFilter.isAdOrPopup("https://adsterra.com/click"))
        assertTrue(AdFilter.isAdOrPopup("https://popads.net/serve"))
        assertTrue(AdFilter.isAdOrPopup("https://slot88gacor.com/daftar"))
        assertTrue(AdFilter.isAdOrPopup("https://malicious.org/download.apk"))
    }

    @Test
    fun adFilter_allowsSafeStreamingUrls() {
        assertFalse(AdFilter.isAdOrPopup("https://tv12.lk21official.cc/"))
        assertFalse(AdFilter.isAdOrPopup("https://tv12.lk21official.cc/genre/action/"))
    }
}
