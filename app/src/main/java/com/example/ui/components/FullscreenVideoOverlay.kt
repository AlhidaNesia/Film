package com.example.ui.components

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun FullscreenVideoOverlay(
    customView: View,
    onExitFullscreen: () -> Unit
) {
    BackHandler {
        onExitFullscreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("fullscreen_video_container")
    ) {
        AndroidView(
            factory = { context ->
                val parent = customView.parent
                if (parent is ViewGroup) {
                    parent.removeView(customView)
                }
                FrameLayout(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    addView(
                        customView,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Floating quick exit fullscreen button
        FloatingActionButton(
            onClick = onExitFullscreen,
            containerColor = Color.Black.copy(alpha = 0.6f),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(44.dp)
                .testTag("btn_exit_fullscreen")
        ) {
            Icon(
                imageVector = Icons.Default.FullscreenExit,
                contentDescription = "Keluar dari Layar Penuh",
                tint = Color.White
            )
        }
    }
}
