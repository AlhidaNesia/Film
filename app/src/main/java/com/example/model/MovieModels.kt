package com.example.model

data class BookmarkItem(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MirrorDomain(
    val name: String,
    val url: String,
    val isDefault: Boolean = false
) {
    companion object {
        val DEFAULT_MIRRORS = listOf(
            MirrorDomain(name = "LK21 Server 1 (Utama)", url = "https://tv12.lk21official.cc/", isDefault = true),
            MirrorDomain(name = "LK21 Server 2 (Mirror 14)", url = "https://tv14.lk21official.cc/"),
            MirrorDomain(name = "LK21 Server 3 (Mirror 10)", url = "https://tv10.lk21official.cc/"),
            MirrorDomain(name = "LK21 Server 4 (Mirror 11)", url = "https://tv11.lk21official.cc/"),
            MirrorDomain(name = "LK21 Official Domain", url = "https://lk21official.cc/")
        )
    }
}

data class MovieCategory(
    val title: String,
    val path: String,
    val iconName: String = "movie"
) {
    companion object {
        val CATEGORIES = listOf(
            MovieCategory(title = "Beranda", path = ""),
            MovieCategory(title = "Populer", path = "populer/"),
            MovieCategory(title = "Top IMDb", path = "rating/"),
            MovieCategory(title = "Action", path = "genre/action/"),
            MovieCategory(title = "Horror", path = "genre/horror/"),
            MovieCategory(title = "Komedi", path = "genre/comedy/"),
            MovieCategory(title = "Drama", path = "genre/drama/"),
            MovieCategory(title = "Romance", path = "genre/romance/"),
            MovieCategory(title = "Sci-Fi", path = "genre/sci-fi/"),
            MovieCategory(title = "Series", path = "series/")
        )
    }
}
