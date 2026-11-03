package com.raye.brewkery.domain

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode


@Serializable
data class OrderSummary(
    val subtotal: Double,
    val deliveryFee: Double,
    val taxRatePercent: Double,
    val taxAmount: Double,
    val totalPayable: Double
)

/**
 * Pure Kotlin calculator for order financial summaries.
 * Calculates tax (as taxRatePercent of subtotal, rounded to 2 decimal places)
 * and final total payable amount.
 */
class OrderSummaryCalculator {

    fun calculateSummary(
        subtotal: Double,
        deliveryFee: Double,
        taxRatePercent: Double = 8.0
    ): OrderSummary {
        val safeSubtotal = subtotal.coerceAtLeast(0.0)
        val safeDeliveryFee = deliveryFee.coerceAtLeast(0.0)

        // Calculate tax: subtotal * (taxRatePercent / 100), rounded to 2 decimal places HALF_UP
        val rawTax = safeSubtotal * (taxRatePercent / 100.0)
        val taxAmount = BigDecimal(rawTax)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()

        val rawTotal = safeSubtotal + safeDeliveryFee + taxAmount
        val totalPayable = if (safeSubtotal == 0.0) {
            0.0
        } else {
            BigDecimal(rawTotal)
                .setScale(2, RoundingMode.HALF_UP)
                .toDouble()
        }

        return OrderSummary(
            subtotal = safeSubtotal,
            deliveryFee = safeDeliveryFee,
            taxRatePercent = taxRatePercent,
            taxAmount = taxAmount,
            totalPayable = totalPayable
        )
    }
}