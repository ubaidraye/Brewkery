package com.raye.brewkery.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raye.brewkery.data.model.CartItem
import com.raye.brewkery.data.model.OrderDetails
import com.raye.brewkery.domain.OrderSummary
import com.raye.brewkery.ui.theme.CardBorder
import com.raye.brewkery.ui.theme.CardSurface
import com.raye.brewkery.ui.theme.CreamBackground
import com.raye.brewkery.ui.theme.TerracottaPrimary
import com.raye.brewkery.ui.theme.TextPrimary
import com.raye.brewkery.ui.theme.TextSecondary
import com.raye.brewkery.ui.viewmodel.CartViewModel
import java.util.Locale

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onOrderPlaced: (OrderDetails) -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val summary by viewModel.orderSummary.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CreamBackground,
        topBar = {
            CartTopBar(
                onBackClick = onBackClick,
                onClearCartClick = { viewModel.clearCart() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CartContentView(
                cartItems = cartItems,
                summary = summary,
                onUpdateQuantity = { cartItemId, newQty ->
                    viewModel.updateQuantity(cartItemId, newQty)
                },
                onPlaceOrder = {
                    if (cartItems.isNotEmpty()) {
                        val orderDetails = viewModel.placeOrder()
                        onOrderPlaced(orderDetails)
                    }
                }
            )
        }
    }
}

@Composable
private fun CartTopBar(
    onBackClick: () -> Unit,
    onClearCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, CardBorder, CircleShape)
                .background(CardSurface)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        // Title
        Text(
            text = "YOUR CART",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp
        )

        // Clear Cart Action
        Text(
            text = "Clear Cart",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TerracottaPrimary,
            modifier = Modifier.clickable(onClick = onClearCartClick)
        )
    }
}

@Composable
private fun CartContentView(
    cartItems: List<CartItem>,
    summary: OrderSummary,
    onUpdateQuantity: (String, Int) -> Unit,
    onPlaceOrder: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Area: Items List or Empty Cart Card
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (cartItems.isEmpty()) {
                EmptyCartCard(modifier = Modifier.padding(top = 8.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = cartItems,
                        key = { item -> item.id }
                    ) { cartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            onUpdateQuantity = { newQty ->
                                onUpdateQuantity(cartItem.id, newQty)
                            }
                        )
                    }
                }
            }
        }

        // Bottom Section: Summary Card & Place Order Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CreamBackground,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Order Summary Card
                OrderSummaryCard(summary = summary)

                Spacer(modifier = Modifier.height(14.dp))

                // Place Order Button
                Button(
                    onClick = onPlaceOrder,
                    enabled = cartItems.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TerracottaPrimary,
                        disabledContainerColor = TerracottaPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Place Order Now • $${
                            String.format(
                                Locale.US,
                                "%.2f",
                                summary.totalPayable
                            )
                        }",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CardSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCartCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 36.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Empty Cart",
                tint = Color(0xFFE5DCD0),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Your in-memory cart is empty.",
                fontSize = 14.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onUpdateQuantity: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                val customizationText = buildCustomizationString(
                    size = cartItem.selectedSize?.label,
                    milk = cartItem.selectedMilk?.name,
                    sugar = cartItem.selectedSugarLevel
                )

                if (customizationText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = customizationText,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$${String.format(Locale.US, "%.2f", cartItem.totalPrice)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TerracottaPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Quantity Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .background(CardSurface)
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            ) {
                IconButton(
                    onClick = { onUpdateQuantity(cartItem.quantity - 1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = TextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = "${cartItem.quantity}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                IconButton(
                    onClick = { onUpdateQuantity(cartItem.quantity + 1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = TextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(summary: OrderSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryLineRow(
                label = "Subtotal",
                amount = "$${String.format(Locale.US, "%.2f", summary.subtotal)}"
            )

            SummaryLineRow(
                label = "Delivery Fee",
                amount = "$${String.format(Locale.US, "%.2f", summary.deliveryFee)}"
            )

            SummaryLineRow(
                label = "Est. Tax (${summary.taxRatePercent}%)",
                amount = "$${String.format(Locale.US, "%.2f", summary.taxAmount)}"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = CardBorder
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Payable",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "$${String.format(Locale.US, "%.2f", summary.totalPayable)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TerracottaPrimary
                )
            }
        }
    }
}

@Composable
private fun SummaryLineRow(
    label: String,
    amount: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextSecondary
        )

        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

private fun buildCustomizationString(
    size: String?,
    milk: String?,
    sugar: String?
): String {
    val parts = mutableListOf<String>()
    if (!size.isNullOrBlank()) parts.add(size)
    if (!milk.isNullOrBlank()) parts.add(milk)
    if (!sugar.isNullOrBlank() && parts.size < 2) parts.add(sugar)
    return parts.joinToString(" • ")
}