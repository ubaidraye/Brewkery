package com.raye.brewkery.ui.viewmodel

import com.raye.brewkery.data.model.BrewkeryItem
import com.raye.brewkery.data.model.Category
import com.raye.brewkery.data.model.Meta

/**
 * Sealed interface representing UI state for the main Brewkery catalog/menu
 */
sealed interface BrewkeryUiState {
    data object Loading : BrewkeryUiState

    data class Success(
        val meta: Meta,
        val categories: List<Category>,
        val items: List<BrewkeryItem>
    ) : BrewkeryUiState

    data class Error(
        val message: String
    ) : BrewkeryUiState
}

/**
 * Sealed interface representing UI state for individual item details (`api/items/{id}.json`)
 */
sealed interface ItemDetailUiState {
    data object Loading : ItemDetailUiState

    data class Success(
        val item: BrewkeryItem
    ) : ItemDetailUiState

    data class Error(
        val message: String
    ) : ItemDetailUiState
}