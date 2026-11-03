package com.raye.brewkery.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.raye.brewkery.data.model.BrewkeryItem
import com.raye.brewkery.data.model.Category
import com.raye.brewkery.data.model.Meta
import com.raye.brewkery.ui.theme.TextSecondary

@Composable
fun MenuContentView(
    meta: Meta,
    categories: List<Category>,
    items: List<BrewkeryItem>,
    cartCount: Int,
    onItemClick: (BrewkeryItem) -> Unit,
    onCartClick: () -> Unit
) {
    var selectedCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Filter items based on selected category and search query
    val filteredItems = items.filter { item ->
        val matchesCategory = selectedCategoryId == null || item.categoryId == selectedCategoryId
        val matchesSearch = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.tagline.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar Header
        item {
            HeaderView(meta = meta, cartCount = cartCount, onCartClick = onCartClick)
        }

        // Active Order / Delivery Banner
        item {
            ActiveOrderBanner(meta = meta)
        }

        // Search Bar
        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { focusManager.clearFocus() }
            )
        }

        // Horizontal Category Chips Row
        item {
            CategoryChipsRow(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { categoryId ->
                    selectedCategoryId = if (selectedCategoryId == categoryId) null else categoryId
                }
            )
        }

        // Items List
        items(
            items = filteredItems,
            key = { item -> item.id }
        ) { item ->
            BrewkeryItemCard(
                item = item,
                currencySymbol = meta.currencySymbol,
                onItemClick = { onItemClick(item) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Empty state when no items match filter
        if (filteredItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items match your search or filter.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
