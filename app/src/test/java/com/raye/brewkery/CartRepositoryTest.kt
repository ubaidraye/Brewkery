package com.raye.brewkery

import com.raye.brewkery.data.model.BrewkeryItem
import com.raye.brewkery.data.model.CartItem
import com.raye.brewkery.data.model.Customizations
import com.raye.brewkery.data.model.MilkOption
import com.raye.brewkery.data.model.SizeOption
import com.raye.brewkery.data.repository.CartRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CartRepositoryTest {

    private lateinit var cartRepository: CartRepositoryImpl

    private val sampleItem1 = BrewkeryItem(
        id = 1,
        categoryId = "cat_hot_coffee",
        name = "Caramel Macchiato",
        tagline = "Double shot espresso",
        description = "Rich espresso roast",
        basePrice = 4.85,
        rating = 4.9,
        reviewCount = 312,
        prepTime = "5-7 mins",
        calories = 240,
        imageUrl = "",
        badge = "BESTSELLER",
        ingredients = listOf("Espresso"),
        customizations = Customizations(
            sizes = listOf(SizeOption("sz_medium", "Grande (12 oz)", 0.65)),
            sugarLevels = listOf("100% Standard Sweet"),
            milkOptions = listOf(MilkOption("m_oat", "Oat Milk (Barista Blend)", 0.00))
        )
    )

    private val sampleItem2 = BrewkeryItem(
        id = 3,
        categoryId = "cat_bakery",
        name = "Normandy Croissant",
        tagline = "Flakier pastry",
        description = "European butter croissant",
        basePrice = 3.90,
        rating = 4.95,
        reviewCount = 420,
        prepTime = "2 mins",
        calories = 290,
        imageUrl = "",
        badge = "FRESHLY BAKED",
        ingredients = listOf("Butter"),
        customizations = Customizations(
            sizes = emptyList(),
            sugarLevels = emptyList(),
            milkOptions = emptyList()
        )
    )

    @Before
    fun setUp() {
        cartRepository = CartRepositoryImpl()
    }

    @Test
    fun `addToCart adds item and updates cart total count and subtotal`() {
        val cartItem = CartItem(
            item = sampleItem1,
            selectedSize = SizeOption("sz_medium", "Grande (12 oz)", 0.65),
            selectedMilk = MilkOption("m_oat", "Oat Milk (Barista Blend)", 0.00),
            selectedSugarLevel = "100% Standard Sweet",
            quantity = 1,
            unitPrice = 5.50,
            totalPrice = 5.50
        )

        cartRepository.addToCart(cartItem)

        assertEquals(1, cartRepository.cartItems.value.size)
        assertEquals(1, cartRepository.totalItemCount.value)
        assertEquals(5.50, cartRepository.subtotalPrice.value, 0.001)
    }

    @Test
    fun `addToCart with identical customization merges item quantity`() {
        val cartItem1 = CartItem(
            item = sampleItem1,
            selectedSize = SizeOption("sz_medium", "Grande (12 oz)", 0.65),
            selectedMilk = MilkOption("m_oat", "Oat Milk (Barista Blend)", 0.00),
            selectedSugarLevel = "100% Standard Sweet",
            quantity = 1,
            unitPrice = 5.50,
            totalPrice = 5.50
        )

        val cartItem2 = cartItem1.copy(quantity = 2, totalPrice = 11.00)

        cartRepository.addToCart(cartItem1)
        cartRepository.addToCart(cartItem2)

        assertEquals(1, cartRepository.cartItems.value.size)
        assertEquals(3, cartRepository.totalItemCount.value)
        assertEquals(16.50, cartRepository.subtotalPrice.value, 0.001)
    }

    @Test
    fun `subtotal calculates correctly across multiple distinct items`() {
        val cartItem1 = CartItem(
            item = sampleItem1,
            selectedSize = SizeOption("sz_medium", "Grande (12 oz)", 0.65),
            selectedMilk = MilkOption("m_oat", "Oat Milk (Barista Blend)", 0.00),
            selectedSugarLevel = "100% Standard Sweet",
            quantity = 1,
            unitPrice = 5.50,
            totalPrice = 5.50
        )

        val cartItem2 = CartItem(
            item = sampleItem2,
            selectedSize = null,
            selectedMilk = null,
            selectedSugarLevel = null,
            quantity = 1,
            unitPrice = 3.90,
            totalPrice = 3.90
        )

        cartRepository.addToCart(cartItem1)
        cartRepository.addToCart(cartItem2)

        assertEquals(2, cartRepository.cartItems.value.size)
        assertEquals(2, cartRepository.totalItemCount.value)
        assertEquals(9.40, cartRepository.subtotalPrice.value, 0.001)
    }

    @Test
    fun `updateQuantity recalculates item total and cart subtotal`() {
        val cartItem = CartItem(
            item = sampleItem2,
            selectedSize = null,
            selectedMilk = null,
            selectedSugarLevel = null,
            quantity = 1,
            unitPrice = 3.90,
            totalPrice = 3.90
        )

        cartRepository.addToCart(cartItem)
        val addedId = cartRepository.cartItems.value.first().id

        cartRepository.updateQuantity(addedId, 3)

        assertEquals(1, cartRepository.cartItems.value.size)
        assertEquals(3, cartRepository.totalItemCount.value)
        assertEquals(11.70, cartRepository.subtotalPrice.value, 0.001)
    }

    @Test
    fun `updateQuantity with zero quantity removes item from cart`() {
        val cartItem = CartItem(
            item = sampleItem2,
            selectedSize = null,
            selectedMilk = null,
            selectedSugarLevel = null,
            quantity = 1,
            unitPrice = 3.90,
            totalPrice = 3.90
        )

        cartRepository.addToCart(cartItem)
        val addedId = cartRepository.cartItems.value.first().id

        cartRepository.updateQuantity(addedId, 0)

        assertTrue(cartRepository.cartItems.value.isEmpty())
        assertEquals(0, cartRepository.totalItemCount.value)
        assertEquals(0.00, cartRepository.subtotalPrice.value, 0.001)
    }

    @Test
    fun `clearCart resets items, count, and subtotal to initial empty state`() {
        val cartItem = CartItem(
            item = sampleItem1,
            selectedSize = null,
            selectedMilk = null,
            selectedSugarLevel = null,
            quantity = 2,
            unitPrice = 4.85,
            totalPrice = 9.70
        )

        cartRepository.addToCart(cartItem)
        cartRepository.clearCart()

        assertTrue(cartRepository.cartItems.value.isEmpty())
        assertEquals(0, cartRepository.totalItemCount.value)
        assertEquals(0.00, cartRepository.subtotalPrice.value, 0.001)
    }
}