package com.raye.brewkery.data.model

import com.raye.brewkery.domain.OrderSummary

data class OrderDetails(
    val ticketId: String,
    val status: String = "PREPARING",
    val estimatedWait: String,
    val summary: OrderSummary,
    val items: List<CartItem>
)