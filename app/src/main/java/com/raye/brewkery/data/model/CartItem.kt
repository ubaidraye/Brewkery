package com.raye.brewkery.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Data class representing a customized item added to the shopping cart.
 */

@Serializable
data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val item: BrewkeryItem,
    val selectedSize: SizeOption?,
    val selectedMilk: MilkOption?,
    val selectedSugarLevel: String?,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)