package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaSurface

@Composable
fun CinemaBottomNavBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    isBookmarked: Boolean,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onHomeClick: () -> Unit,
    onToggleBookmarkClick: () -> Unit,
    onOpenBookmarksClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CinemaSurface,
        tonalElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                enabled = canGoBack,
                modifier = Modifier.testTag("nav_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = if (canGoBack) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                )
            }

            // Forward Button
            IconButton(
                onClick = onForwardClick,
                enabled = canGoForward,
                modifier = Modifier.testTag("nav_forward")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Maju",
                    tint = if (canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                )
            }

            // Home Button
            IconButton(
                onClick = onHomeClick,
                modifier = Modifier.testTag("nav_home")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Beranda Film",
                    tint = CinemaRed
                )
            }

            // Toggle Bookmark Button
            IconButton(
                onClick = onToggleBookmarkClick,
                modifier = Modifier.testTag("nav_bookmark_toggle")
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = if (isBookmarked) "Hapus dari Favorit" else "Tambah ke Favorit",
                    tint = if (isBookmarked) CinemaGold else MaterialTheme.colorScheme.onSurface
                )
            }

            // Bookmarks Drawer/Sheet Button
            IconButton(
                onClick = onOpenBookmarksClick,
                modifier = Modifier.testTag("nav_bookmarks_list")
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmarks,
                    contentDescription = "Daftar Favorit",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Reload Button
            IconButton(
                onClick = onReloadClick,
                modifier = Modifier.testTag("nav_reload")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Muat Ulang",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
