package com.raye.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raye.brewkery.data.model.CartItem
import com.raye.brewkery.data.model.Meta
import com.raye.brewkery.data.model.OrderDetails
import com.raye.brewkery.data.repository.BrewkeryRepository
import com.raye.brewkery.data.repository.CartRepository
import com.raye.brewkery.domain.OrderSummary
import com.raye.brewkery.domain.OrderSummaryCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val brewkeryRepository: BrewkeryRepository,
    private val summaryCalculator: OrderSummaryCalculator
) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItems

    private val _meta = MutableStateFlow<Meta?>(null)
    val meta: StateFlow<Meta?> = _meta.asStateFlow()

    val orderSummary: StateFlow<OrderSummary> = combine(
        cartRepository.subtotalPrice,
        _meta
    ) { subtotal, meta ->
        val deliveryFee = meta?.deliveryFee ?: 2.50
        val taxRatePercent = meta?.taxRatePercent ?: 8.0
        summaryCalculator.calculateSummary(
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            taxRatePercent = taxRatePercent
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = summaryCalculator.calculateSummary(0.0, 2.50, 8.0)
    )

    init {
        loadMetaInfo()
    }

    private fun loadMetaInfo() {
        viewModelScope.launch {
            brewkeryRepository.getBrewkeryData().onSuccess { response ->
                _meta.value = response.meta
            }
        }
    }

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        cartRepository.updateQuantity(cartItemId, newQuantity)
    }

    fun removeFromCart(cartItemId: String) {
        cartRepository.removeFromCart(cartItemId)
    }

    fun clearCart() {
        cartRepository.clearCart()
    }

    fun placeOrder(): OrderDetails {
        val currentItems = cartItems.value
        val summary = orderSummary.value
        val estimatedWait = _meta.value?.estimatedDeliveryTime ?: "20 - 30 mins"
        val randomNum = Random.nextInt(10000, 99999)
        val ticketId = "BK-$randomNum"

        val orderDetails = OrderDetails(
            ticketId = ticketId,
            status = "PREPARING",
            estimatedWait = estimatedWait,
            summary = summary,
            items = currentItems
        )

        // Clear cart on successful order placement
        cartRepository.clearCart()

        return orderDetails
    }
}