package com.example.ui.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.DhikrSummaryCard
import com.example.ui.components.HisnTopBar
import com.example.ui.theme.IslamicGold
import com.example.ui.viewmodel.HisnViewModel

@Composable
fun FavoritesScreen(
    viewModel: HisnViewModel,
    onOpenReader: () -> Unit
) {
    val favoriteDhikrs by viewModel.favoriteDhikrs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("favorites_screen")
    ) {
        HisnTopBar(
            title = "الأذكار المفضلة"
        )

        if (favoriteDhikrs.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = IslamicGold
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "قائمتك المفضلة فارغة",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "اضغط على أيقونة الإشارة المرجعية بجانب أي ذكر لإضافته هنا والوصول إليه سريعاً.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "عدد الأذكار في المفضلة: ${favoriteDhikrs.size}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                itemsIndexed(favoriteDhikrs) { index, dhikr ->
                    DhikrSummaryCard(
                        dhikr = dhikr,
                        isFavorite = true,
                        onCardClick = {
                            viewModel.openDhikrReader(favoriteDhikrs, index)
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
}
