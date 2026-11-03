package com.raye.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raye.brewkery.data.model.BrewkeryItem
import com.raye.brewkery.data.model.CartItem
import com.raye.brewkery.data.model.MilkOption
import com.raye.brewkery.data.model.SizeOption
import com.raye.brewkery.data.repository.BrewkeryRepository
import com.raye.brewkery.data.repository.CartRepository
import com.raye.brewkery.domain.ItemPriceCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val repository: BrewkeryRepository,
    private val cartRepository: CartRepository,
    private val priceCalculator: ItemPriceCalculator
) : ViewModel() {

    private val _uiState = MutableStateFlow<ItemDetailUiState>(ItemDetailUiState.Loading)
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    private val _selectedSize = MutableStateFlow<SizeOption?>(null)
    val selectedSize: StateFlow<SizeOption?> = _selectedSize.asStateFlow()

    private val _selectedMilk = MutableStateFlow<MilkOption?>(null)
    val selectedMilk: StateFlow<MilkOption?> = _selectedMilk.asStateFlow()

    private val _selectedSugarLevel = MutableStateFlow<String?>(null)
    val selectedSugarLevel: StateFlow<String?> = _selectedSugarLevel.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var currentItem: BrewkeryItem? = null

    fun fetchItemDetails(itemId: Int) {
        viewModelScope.launch {
            _uiState.value = ItemDetailUiState.Loading
            repository.getItemDetails(itemId)
                .onSuccess { item ->
                    currentItem = item
                    // Initialize default customization selections
                    _selectedSize.value = item.customizations.sizes.firstOrNull()
                    _selectedMilk.value = item.customizations.milkOptions.firstOrNull()
                    _selectedSugarLevel.value = item.customizations.sugarLevels.firstOrNull()
                    _quantity.value = 1
                    recalculatePrice()
                    _uiState.value = ItemDetailUiState.Success(item = item)
                }
                .onFailure { throwable ->
                    _uiState.value = ItemDetailUiState.Error(
                        message = throwable.localizedMessage ?: "Failed to fetch item details."
                    )
                }
        }
    }

    fun selectSize(sizeOption: SizeOption) {
        _selectedSize.value = sizeOption
        recalculatePrice()
    }

    fun selectMilk(milkOption: MilkOption) {
        _selectedMilk.value = milkOption
        recalculatePrice()
    }

    fun selectSugarLevel(sugarLevel: String) {
        _selectedSugarLevel.value = sugarLevel
        recalculatePrice()
    }

    fun incrementQuantity() {
        _quantity.value += 1
        recalculatePrice()
    }

    fun decrementQuantity() {
        if (_quantity.value > 1) {
            _quantity.value -= 1
            recalculatePrice()
        }
    }

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
    }

    private fun recalculatePrice() {
        val item = currentItem ?: return
        _totalPrice.value = priceCalculator.calculateTotalPrice(
            basePrice = item.basePrice,
            selectedSize = _selectedSize.value,
            selectedMilk = _selectedMilk.value,
            sugarLevel = _selectedSugarLevel.value,
            quantity = _quantity.value
        )
    }

    fun addToCart(): Boolean {
        val item = currentItem ?: return false
        val unitPrice = priceCalculator.calculateUnitPrice(
            basePrice = item.basePrice,
            selectedSize = _selectedSize.value,
            selectedMilk = _selectedMilk.value,
            sugarLevel = _selectedSugarLevel.value
        )
        val cartItem = CartItem(
            item = item,
            selectedSize = _selectedSize.value,
            selectedMilk = _selectedMilk.value,
            selectedSugarLevel = _selectedSugarLevel.value,
            quantity = _quantity.value,
            unitPrice = unitPrice,
            totalPrice = _totalPrice.value
        )
        cartRepository.addToCart(cartItem)
        return true
    }
}