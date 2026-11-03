package com.raye.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raye.brewkery.data.repository.BrewkeryRepository
import com.raye.brewkery.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BrewkeryViewModel @Inject constructor(
    private val repository: BrewkeryRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BrewkeryUiState>(BrewkeryUiState.Loading)
    val uiState: StateFlow<BrewkeryUiState> = _uiState.asStateFlow()

    val cartItemCount: StateFlow<Int> = cartRepository.totalItemCount

    val cartTotalPrice: StateFlow<String> = combine(
        cartRepository.cartItems,
        _uiState
    ) { cartItems, menuState ->
        if (menuState is BrewkeryUiState.Success) {
            val cuurencySymbol = menuState.meta.currencySymbol

            val subtotal = cartItems.sumOf { it.totalPrice }

            "$cuurencySymbol${String.format(Locale.US, "%.2f", subtotal)}"
        } else {
            "$0.00"
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "$0.00"
    )

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
                        message = throwable.localizedMessage
                            ?: "An unexpected error occurred while loading menu."
                    )
                }
        }
    }
}