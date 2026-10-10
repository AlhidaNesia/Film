package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MovieCategory
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaSurfaceVariant

@Composable
fun CategoryChipsRow(
    categories: List<MovieCategory>,
    currentUrl: String,
    onCategoryClick: (MovieCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = if (category.path.isEmpty()) {
                currentUrl.endsWith(".cc/") || currentUrl.endsWith(".cc")
            } else {
                currentUrl.contains(category.path)
            }

            FilterChip(
                selected = isSelected,
                onClick = { onCategoryClick(category) },
                label = {
                    Text(
                        text = category.title,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = CinemaSurfaceVariant,
                    selectedContainerColor = CinemaRed
                ),
                border = null,
                modifier = Modifier.testTag("chip_${category.title.lowercase()}")
            )
        }
    }
}
