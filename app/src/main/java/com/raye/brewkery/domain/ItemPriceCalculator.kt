package com.raye.brewkery.domain

import com.raye.brewkery.data.model.MilkOption
import com.raye.brewkery.data.model.SizeOption

/**
 * Pure Kotlin class handling item price calculations.
 * Calculates unit prices and total item prices based on size, milk option,
 * sugar level extra charges, and item quantity.
 */
class ItemPriceCalculator {

    /**
     * Calculates the unit price of a single item including option price adjustments.
     */
    fun calculateUnitPrice(
        basePrice: Double,
        selectedSize: SizeOption?,
        selectedMilk: MilkOption?,
        sugarLevel: String? = null
    ): Double {
        val sizeExtra = selectedSize?.extraPrice ?: 0.0
        val milkExtra = selectedMilk?.extraPrice ?: 0.0
        val sugarExtra = parseSugarExtraPrice(sugarLevel)
        return basePrice + sizeExtra + milkExtra + sugarExtra
    }

    /**
     * Calculates the final total price based on unit price and quantity.
     */
    fun calculateTotalPrice(
        basePrice: Double,
        selectedSize: SizeOption?,
        selectedMilk: MilkOption?,
        sugarLevel: String? = null,
        quantity: Int
    ): Double {
        val unitPrice = calculateUnitPrice(basePrice, selectedSize, selectedMilk, sugarLevel)
        val validQuantity = quantity.coerceAtLeast(1)
        return unitPrice * validQuantity
    }

    /**
     * Helper method to parse extra prices embedded in sugar level string labels (e.g., "(+0.40)").
     */
    fun parseSugarExtraPrice(sugarLevel: String?): Double {
        if (sugarLevel.isNullOrBlank()) return 0.0
        val regex = Regex("""\(\+\$?(\d+(?:\.\d+)?)\)""")
        val match = regex.find(sugarLevel)
        return match?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
    }
}