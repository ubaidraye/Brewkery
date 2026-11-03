package com.raye.brewkery

import com.raye.brewkery.data.model.MilkOption
import com.raye.brewkery.data.model.SizeOption
import com.raye.brewkery.domain.ItemPriceCalculator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ItemPriceCalculatorTest {

    private lateinit var calculator: ItemPriceCalculator

    @Before
    fun setUp() {
        calculator = ItemPriceCalculator()
    }

    @Test
    fun `calculateUnitPrice with base price only returns base price`() {
        val basePrice = 4.85
        val unitPrice = calculator.calculateUnitPrice(basePrice, null, null, null)
        assertEquals(4.85, unitPrice, 0.001)
    }

    @Test
    fun `calculateUnitPrice with size and milk extra prices returns correct sum`() {
        val basePrice = 4.85
        val sizeOption = SizeOption(id = "sz_medium", label = "Grande (12 oz)", extraPrice = 0.65)
        val milkOption = MilkOption(id = "m_almond", name = "Roasted Almond Milk", extraPrice = 0.50)

        val unitPrice = calculator.calculateUnitPrice(basePrice, sizeOption, milkOption, "50% Mild")
        assertEquals(6.00, unitPrice, 0.001)
    }

    @Test
    fun `calculateUnitPrice with sugar level extra price parses correctly`() {
        val basePrice = 5.60
        val sugarLevelWithExtra = "Light Wildflower Honey (+0.40)"

        val unitPrice = calculator.calculateUnitPrice(basePrice, null, null, sugarLevelWithExtra)
        assertEquals(6.00, unitPrice, 0.001)
    }

    @Test
    fun `calculateTotalPrice multiplies unit price by quantity`() {
        val basePrice = 4.85
        val sizeOption = SizeOption(id = "sz_large", label = "Venti (16 oz)", extraPrice = 1.25)
        val milkOption = MilkOption(id = "m_almond", name = "Roasted Almond Milk", extraPrice = 0.50)

        val totalPrice = calculator.calculateTotalPrice(
            basePrice = basePrice,
            selectedSize = sizeOption,
            selectedMilk = milkOption,
            sugarLevel = "100% Standard Sweet",
            quantity = 3
        )
        // Unit price: 4.85 + 1.25 + 0.50 = 6.60
        // Total price for qty 3: 6.60 * 3 = 19.80
        assertEquals(19.80, totalPrice, 0.001)
    }

    @Test
    fun `calculateTotalPrice coerces negative or zero quantity to 1`() {
        val basePrice = 3.90
        val totalPrice = calculator.calculateTotalPrice(
            basePrice = basePrice,
            selectedSize = null,
            selectedMilk = null,
            sugarLevel = null,
            quantity = 0
        )
        assertEquals(3.90, totalPrice, 0.001)
    }
}