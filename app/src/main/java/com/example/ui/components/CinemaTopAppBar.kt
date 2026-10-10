package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopMac
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinemaTopAppBar(
    pageTitle: String,
    currentUrl: String,
    isLoading: Boolean,
    progress: Int,
    blockedAdsCount: Int,
    isAdBlockEnabled: Boolean,
    isDesktopMode: Boolean,
    onSearchClick: () -> Unit,
    onMirrorClick: () -> Unit,
    onReloadClick: () -> Unit,
    onToggleDesktop: () -> Unit,
    onToggleAdBlock: () -> Unit,
    onClearCache: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    val hostText = remember(currentUrl) {
        try {
            val uri = Uri.parse(currentUrl)
            uri.host ?: "lk21"
        } catch (_: Exception) {
            "lk21"
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cinema Brand Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CinemaRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "NONTON",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pageTitle.ifEmpty { "Streaming Film" },
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Secure",
                                tint = CinemaGold,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = hostText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            },
            actions = {
                // Search Action
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("action_search")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari Film",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Ad Protection Shield with Counter Badge
                IconButton(
                    onClick = onToggleAdBlock,
                    modifier = Modifier.testTag("action_adshield")
                ) {
                    if (isAdBlockEnabled && blockedAdsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = CinemaRed,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (blockedAdsCount > 99) "99+" else "$blockedAdsCount",
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Pelindung Iklan",
                                tint = if (isAdBlockEnabled) CinemaGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Pelindung Iklan",
                            tint = if (isAdBlockEnabled) CinemaGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Mirror Server Switcher
                IconButton(
                    onClick = onMirrorClick,
                    modifier = Modifier.testTag("action_mirror")
                ) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = "Ganti Server",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Overflow Menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("action_overflow_menu")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu Lainnya",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(CinemaSurfaceVariant)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (isDesktopMode) "Beralih ke Tampilan Mobile" else "Beralih ke Tampilan Desktop",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    if (isDesktopMode) Icons.Default.Smartphone else Icons.Default.DesktopMac,
                                    contentDescription = null,
                                    tint = CinemaGold
                                )
                            },
                            onClick = {
                                showMenu = false
                                onToggleDesktop()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Bagikan Tautan Film", color = MaterialTheme.colorScheme.onSurface) },
                            leadingIcon = {
                                Icon(Icons.Default.Share, contentDescription = null, tint = CinemaGold)
                            },
                            onClick = {
                                showMenu = false
                                shareUrl(context, currentUrl, pageTitle)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Buka di Browser Eksternal", color = MaterialTheme.colorScheme.onSurface) },
                            leadingIcon = {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = CinemaGold)
                            },
                            onClick = {
                                showMenu = false
                                openInExternalBrowser(context, currentUrl)
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (isAdBlockEnabled) "Nonaktifkan Anti-Popup" else "Aktifkan Anti-Popup",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = CinemaGold)
                            },
                            onClick = {
                                showMenu = false
                                onToggleAdBlock()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Bersihkan Cache", color = MaterialTheme.colorScheme.onSurface) },
                            leadingIcon = {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = CinemaGold)
                            },
                            onClick = {
                                showMenu = false
                                onClearCache()
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CinemaSurface
            )
        )

        // Web Loading Progress Bar
        AnimatedVisibility(visible = isLoading && progress < 100) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = CinemaRed,
                trackColor = CinemaSurfaceVariant
            )
        }
    }
}

private fun shareUrl(context: Context, url: String, title: String) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Nonton film gratis: $title\n$url")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Film")
        context.startActivity(shareIntent)
    } catch (_: Exception) {
    }
}

private fun openInExternalBrowser(context: Context, url: String) {
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    } catch (_: Exception) {
    }
}
