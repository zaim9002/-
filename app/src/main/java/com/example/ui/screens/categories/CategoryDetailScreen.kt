package com.example.ui.screens.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.DhikrSummaryCard
import com.example.ui.components.HisnTopBar
import com.example.ui.viewmodel.HisnViewModel

@Composable
fun CategoryDetailScreen(
    categoryId: String,
    viewModel: HisnViewModel,
    onBackClick: () -> Unit,
    onOpenReader: () -> Unit
) {
    val category = remember(categoryId) {
        viewModel.categories.find { it.id == categoryId }
    }
    val dhikrs = remember(categoryId) {
        viewModel.allDhikrs.filter { it.categoryId == categoryId }
    }
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("category_detail_screen")
    ) {
        HisnTopBar(
            title = category?.title ?: "الأذكار",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // Header card with "Start Reading All"
                Button(
                    onClick = {
                        viewModel.openDhikrReader(dhikrs, 0)
                        onOpenReader()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_reading_category_btn"),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) {
                    Text(
                        text = "قراءة كافة أذكار الباب (${dhikrs.size} ذكر)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            itemsIndexed(dhikrs) { index, dhikr ->
                val isFav = favoriteIds.contains(dhikr.id)
                DhikrSummaryCard(
                    dhikr = dhikr,
                    isFavorite = isFav,
                    onCardClick = {
                        viewModel.openDhikrReader(dhikrs, index)
                        onOpenReader()
                    },
                    onFavoriteToggle = { viewModel.toggleFavorite(dhikr.id) },
                    onCopyClick = { viewModel.copyDhikr(dhikr) },
                    onShareClick = { viewModel.shareDhikr(dhikr) }
                )
            }
        }
    }
}
