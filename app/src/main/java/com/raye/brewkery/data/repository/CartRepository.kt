package com.raye.brewkery.data.repository

import com.raye.brewkery.data.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface CartRepository {
    val cartItems: StateFlow<List<CartItem>>
    val totalItemCount: StateFlow<Int>
    val subtotalPrice: StateFlow<Double>

    fun addToCart(cartItem: CartItem)
    fun updateQuantity(cartItemId: String, newQuantity: Int)
    fun removeFromCart(cartItemId: String)
    fun clearCart()
}

@Singleton
class CartRepositoryImpl @Inject constructor() : CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    override val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _totalItemCount = MutableStateFlow(0)
    override val totalItemCount: StateFlow<Int> = _totalItemCount.asStateFlow()

    private val _subtotalPrice = MutableStateFlow(0.0)
    override val subtotalPrice: StateFlow<Double> = _subtotalPrice.asStateFlow()

    override fun addToCart(cartItem: CartItem) {
        val currentList = _cartItems.value.toMutableList()

        // Match existing item with identical customization options
        val existingIndex = currentList.indexOfFirst { existing ->
            existing.item.id == cartItem.item.id &&
                    existing.selectedSize?.id == cartItem.selectedSize?.id &&
                    existing.selectedMilk?.id == cartItem.selectedMilk?.id &&
                    existing.selectedSugarLevel == cartItem.selectedSugarLevel
        }

        if (existingIndex != -1) {
            val existing = currentList[existingIndex]
            val updatedQuantity = existing.quantity + cartItem.quantity
            val updatedTotalPrice = existing.unitPrice * updatedQuantity
            currentList[existingIndex] = existing.copy(
                quantity = updatedQuantity,
                totalPrice = updatedTotalPrice
            )
        } else {
            currentList.add(cartItem)
        }

        _cartItems.value = currentList
        recalculateTotals()
    }

    override fun updateQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItemId)
            return
        }

        val currentList = _cartItems.value.map { item ->
            if (item.id == cartItemId) {
                item.copy(
                    quantity = newQuantity,
                    totalPrice = item.unitPrice * newQuantity
                )
            } else {
                item
            }
        }
        _cartItems.value = currentList
        recalculateTotals()
    }

    override fun removeFromCart(cartItemId: String) {
        _cartItems.value = _cartItems.value.filter { it.id != cartItemId }
        recalculateTotals()
    }

    override fun clearCart() {
        _cartItems.value = emptyList()
        recalculateTotals()
    }

    private fun recalculateTotals() {
        _totalItemCount.value = _cartItems.value.sumOf { it.quantity }
        _subtotalPrice.value = _cartItems.value.sumOf { it.totalPrice }
    }
}