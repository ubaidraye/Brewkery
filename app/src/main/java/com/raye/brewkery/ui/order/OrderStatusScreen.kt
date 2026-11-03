package com.raye.brewkery.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raye.brewkery.data.model.OrderDetails
import com.raye.brewkery.ui.theme.ActiveOrderBg
import com.raye.brewkery.ui.theme.ActiveOrderBorder
import com.raye.brewkery.ui.theme.CardBorder
import com.raye.brewkery.ui.theme.CardSurface
import com.raye.brewkery.ui.theme.CreamBackground
import com.raye.brewkery.ui.theme.TerracottaPrimary
import com.raye.brewkery.ui.theme.TextPrimary
import com.raye.brewkery.ui.theme.TextSecondary
import com.raye.brewkery.ui.theme.TrackButtonGreen
import java.util.Locale

@Composable
fun OrderStatusScreen(
    orderDetails: OrderDetails,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CreamBackground, // Reusing your background color token
        bottomBar = {
            // 4. Fixed Bottom Action Bar matching your theme styling
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CreamBackground, // Blends seamlessly into background
                tonalElevation = 0.dp
            ) {
                Button(
                    onClick = onBackToMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1C130E) // Using theme Deep Coffee color
                    )
                ) {
                    Text(
                        text = "Back to Menu",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 1. Success Coffee Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(ActiveOrderBg, shape = CircleShape)
                    .border(1.dp, ActiveOrderBorder, CircleShape)
            ) {
                Text(text = "☕", fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Updated Header Typography Layout
            Text(
                text = "ORDER DISPATCHED",
                color = TerracottaPrimary, // Reusing your orange theme token
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Brewing in Progress!",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your ticket was dispatched to our barista.",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Unified Order Details Ticket Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header Sub-row: Title and State Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ORDER TICKET",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "#${orderDetails.ticketId}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        // Status Badge Pill
                        Surface(
                            color = Color(0xFFFFF5E6), // Light orange container
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = orderDetails.status.uppercase(Locale.ROOT),
                                color = Color(0xFFD97706), // Vivid Amber status text
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = CardBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Estimated Wait Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Estimated Wait:", color = TextSecondary, fontSize = 14.sp)
                        Text(
                            text = orderDetails.estimatedWait,
                            color = TerracottaPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dynamic Item Quantity Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Items Ordered:", color = TextSecondary, fontSize = 14.sp)
                        Text(
                            text = "${orderDetails.items.sumOf { it.quantity }} Items",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = CardBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Message Block
                    Text(text = "Status:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Barista accepted your order!",
                        color = TrackButtonGreen, // Utilizing your green state token
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp)) // Padding cushion at the bottom scroll end
        }
    }
}