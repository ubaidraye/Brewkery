package com.raye.brewkery.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Root data response from `data.json`
 */
data class BrewkeryResponse(
    @SerializedName("meta") val meta: Meta,
    @SerializedName("categories") val categories: List<Category>,
    @SerializedName("items") val items: List<BrewkeryItem>
)

/**
 * Metadata containing general store info and configuration
 */
data class Meta(
    @SerializedName("app") val app: String,
    @SerializedName("version") val version: String,
    @SerializedName("tagline") val tagline: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("currency_symbol") val currencySymbol: String,
    @SerializedName("delivery_fee") val deliveryFee: Double,
    @SerializedName("tax_rate_percent") val taxRatePercent: Double,
    @SerializedName("estimated_delivery_time") val estimatedDeliveryTime: String
)

/**
 * Menu Category model
 */
data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("item_count") val itemCount: Int
)

/**
 * Food/Beverage Item model returned in lists or single item details (`api/items/{id}.json`)
 */
@Serializable
data class BrewkeryItem(
    @SerializedName("id") val id: Int,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("name") val name: String,
    @SerializedName("tagline") val tagline: String,
    @SerializedName("description") val description: String,
    @SerializedName("base_price") val basePrice: Double,
    @SerializedName("rating") val rating: Double,
    @SerializedName("review_count") val reviewCount: Int,
    @SerializedName("prep_time") val prepTime: String,
    @SerializedName("calories") val calories: Int,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("badge") val badge: String? = null,
    @SerializedName("ingredients") val ingredients: List<String>,
    @SerializedName("customizations") val customizations: Customizations
)

/**
 * Customization options available for an item
 */
@Serializable
data class Customizations(
    @SerializedName("sizes") val sizes: List<SizeOption>,
    @SerializedName("sugar_levels") val sugarLevels: List<String>,
    @SerializedName("milk_options") val milkOptions: List<MilkOption>
)

/**
 * Size selection customization option
 */
@Serializable
data class SizeOption(
    @SerializedName("id") val id: String,
    @SerializedName("label") val label: String,
    @SerializedName("extra_price") val extraPrice: Double
)

/**
 * Milk / Topping customization option
 */
@Serializable
data class MilkOption(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("extra_price") val extraPrice: Double
)