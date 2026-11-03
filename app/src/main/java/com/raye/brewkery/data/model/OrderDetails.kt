package com.raye.brewkery.data.model

import com.raye.brewkery.domain.OrderSummary
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OrderDetails(
    val ticketId: String,
    val status: String = "PREPARING",
    val estimatedWait: String,
    val summary: OrderSummary,
    val items: List<CartItem>
)

// Helper Functions
fun convertOrderDetailsToJson(orderDetails: OrderDetails): String {
    return Json.encodeToString(orderDetails)
}

fun convertJsonToOrderDetails(json: String): OrderDetails {
    return Json.decodeFromString(json)
}