package com.raye.brewkery

import com.raye.brewkery.domain.OrderSummaryCalculator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class OrderSummaryCalculatorTest {

    private lateinit var calculator: OrderSummaryCalculator

    @Before
    fun setUp() {
        calculator = OrderSummaryCalculator()
    }

    @Test
    fun `calculateSummary with prompt example subtotal 9_40 delivery 2_50 returns tax 0_75 and total 12_65`() {
        // subtotal 9.40, delivery 2.50, tax (8.0% of 9.40 = 0.752 -> 0.75), total = 12.65
        val summary = calculator.calculateSummary(
            subtotal = 9.40,
            deliveryFee = 2.50,
            taxRatePercent = 8.0
        )

        assertEquals(9.40, summary.subtotal, 0.001)
        assertEquals(2.50, summary.deliveryFee, 0.001)
        assertEquals(8.0, summary.taxRatePercent, 0.001)
        assertEquals(0.75, summary.taxAmount, 0.001)
        assertEquals(12.65, summary.totalPayable, 0.001)
    }

    @Test
    fun `calculateSummary tests HALF_UP tax rounding up`() {
        // subtotal 9.44 -> tax 8% = 0.7552 -> rounds up to 0.76
        val summary = calculator.calculateSummary(
            subtotal = 9.44,
            deliveryFee = 2.50,
            taxRatePercent = 8.0
        )

        assertEquals(0.76, summary.taxAmount, 0.001)
        assertEquals(12.70, summary.totalPayable, 0.001)
    }

    @Test
    fun `calculateSummary with empty cart (0_0 subtotal) returns 0 tax, 2_50 delivery fee, and 0_00 total payable`() {
        val summary = calculator.calculateSummary(
            subtotal = 0.0,
            deliveryFee = 2.50,
            taxRatePercent = 8.0
        )

        assertEquals(0.00, summary.subtotal, 0.001)
        assertEquals(2.50, summary.deliveryFee, 0.001)
        assertEquals(0.00, summary.taxAmount, 0.001)
        assertEquals(0.00, summary.totalPayable, 0.001)
    }
}