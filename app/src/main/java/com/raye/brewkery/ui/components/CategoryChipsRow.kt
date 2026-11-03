package com.raye.brewkery.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.raye.brewkery.data.model.Category

@Composable
fun CategoryChipsRow(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // "All Items" Chip
        item {
            CategoryChip(
                label = "All Items",
                icon = null,
                isSelected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) }
            )
        }

        // Category Chips
        items(categories) { category ->
            CategoryChip(
                label = category.name,
                icon = category.icon,
                isSelected = selectedCategoryId == category.id,
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}