package com.raye.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raye.brewkery.data.repository.BrewkeryRepository
import com.raye.brewkery.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrewkeryViewModel @Inject constructor(
    private val repository: BrewkeryRepository,
    cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BrewkeryUiState>(BrewkeryUiState.Loading)
    val uiState: StateFlow<BrewkeryUiState> = _uiState.asStateFlow()

    val cartItemCount: StateFlow<Int> = cartRepository.totalItemCount

    init {
        fetchBrewkeryData()
    }

    fun fetchBrewkeryData() {
        viewModelScope.launch {
            _uiState.value = BrewkeryUiState.Loading
            repository.getBrewkeryData()
                .onSuccess { response ->
                    _uiState.value = BrewkeryUiState.Success(
                        meta = response.meta,
                        categories = response.categories,
                        items = response.items
                    )
                }
                .onFailure { throwable ->
                    _uiState.value = BrewkeryUiState.Error(
                        message = throwable.localizedMessage ?: "An unexpected error occurred while loading menu."
                    )
                }
        }
    }
}