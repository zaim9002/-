package com.example.ui.screens.duas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.DhikrSummaryCard
import com.example.ui.components.HisnTopBar
import com.example.ui.viewmodel.HisnViewModel

@Composable
fun DuasScreen(
    viewModel: HisnViewModel,
    onBackClick: () -> Unit,
    onOpenReader: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("quran_duas") }

    val duaCategories = listOf(
        Pair("quran_duas", "أدعية القرآن"),
        Pair("sunnah_duas", "أدعية السنة"),
        Pair("grief", "الكرب والهم"),
        Pair("sickness", "الشفاء وعيادة المريض"),
        Pair("travel", "دعاء السفر"),
        Pair("istighfar", "الاستغفار والتوبة")
    )

    val currentDhikrs = remember(selectedTab) {
        viewModel.allDhikrs.filter { it.categoryId == selectedTab }
    }
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("duas_screen")
    ) {
        HisnTopBar(
            title = "جوامع الأدعية المأثورة",
            onBackClick = onBackClick
        )

        // Dua category chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(duaCategories) { item ->
                val isSelected = selectedTab == item.first
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedTab = item.first },
                    label = { Text(item.second) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(currentDhikrs) { index, dhikr ->
                val isFav = favoriteIds.contains(dhikr.id)
                DhikrSummaryCard(
                    dhikr = dhikr,
                    isFavorite = isFav,
                    onCardClick = {
                        viewModel.openDhikrReader(currentDhikrs, index)
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
