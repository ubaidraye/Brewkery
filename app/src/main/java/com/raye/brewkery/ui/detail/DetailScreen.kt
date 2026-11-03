package com.raye.brewkery.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.raye.brewkery.data.model.BrewkeryItem
import com.raye.brewkery.data.model.MilkOption
import com.raye.brewkery.data.model.SizeOption
import com.raye.brewkery.ui.theme.BadgeBgYellow
import com.raye.brewkery.ui.theme.CardBorder
import com.raye.brewkery.ui.theme.CardSurface
import com.raye.brewkery.ui.theme.CreamBackground
import com.raye.brewkery.ui.theme.EspressoDark
import com.raye.brewkery.ui.theme.TerracottaPrimary
import com.raye.brewkery.ui.theme.TextPrimary
import com.raye.brewkery.ui.theme.TextSecondary
import com.raye.brewkery.ui.viewmodel.ItemDetailUiState
import com.raye.brewkery.ui.viewmodel.ItemDetailViewModel
import java.util.Locale

private val SelectedOptionBg = Color(0xFFFDF0EB)

@Composable
fun DetailScreen(
    itemId: Int,
    viewModel: ItemDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(itemId) {
        viewModel.fetchItemDetails(itemId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CreamBackground,
        topBar = {
            HeaderTopBar(
                isFavorite = isFavorite,
                onBackClick = onBackClick,
                onFavoriteToggle = { viewModel.toggleFavorite() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ItemDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        color = TerracottaPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ItemDetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = state.message, color = TextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchItemDetails(itemId) },
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                        ) {
                            Text("Retry")
                        }
                    }
                }

                is ItemDetailUiState.Success -> {
                    ItemDetailContentView(
                        item = state.item,
                        viewModel = viewModel,
                        onAddToCart = {
                            if (viewModel.addToCart()) {
                                onBackClick()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderTopBar(
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit
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
            text = "ITEM CUSTOMIZER",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp
        )

        // Favorite Heart Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, CardBorder, CircleShape)
                .background(CardSurface)
                .clickable(onClick = onFavoriteToggle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) Color(0xFFEC4899) else TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ItemDetailContentView(
    item: BrewkeryItem,
    viewModel: ItemDetailViewModel,
    onAddToCart: () -> Unit
) {
    val selectedSize by viewModel.selectedSize.collectAsState()
    val selectedMilk by viewModel.selectedMilk.collectAsState()
    val selectedSugarLevel by viewModel.selectedSugarLevel.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Hero Image Card with Badge Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (!item.badge.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EspressoDark)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.badge.uppercase(Locale.ROOT),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BadgeBgYellow,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title & Base Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "$${String.format(Locale.US, "%.2f", item.basePrice)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TerracottaPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Key Ingredients Section
            if (item.ingredients.isNotEmpty()) {
                Text(
                    text = "KEY INGREDIENTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item.ingredients.forEach { ingredient ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CardSurface,
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Text(
                                text = ingredient,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Size Selection Section Card
            if (item.customizations.sizes.isNotEmpty()) {
                CardSectionContainer(title = "Size Selection") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item.customizations.sizes.forEach { sizeOption ->
                            val isSelected = sizeOption.id == selectedSize?.id
                            SizeOptionButton(
                                sizeOption = sizeOption,
                                isSelected = isSelected,
                                onClick = { viewModel.selectSize(sizeOption) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Milk Options / Spreads Section Card
            if (item.customizations.milkOptions.isNotEmpty()) {
                CardSectionContainer(title = "Milk Options / Spreads") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item.customizations.milkOptions.forEach { milkOption ->
                            val isSelected = milkOption.id == selectedMilk?.id
                            MilkOptionRow(
                                milkOption = milkOption,
                                isSelected = isSelected,
                                onClick = { viewModel.selectMilk(milkOption) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Sugar Levels / Serving Section Card
            if (item.customizations.sugarLevels.isNotEmpty()) {
                CardSectionContainer(title = "Sugar Levels / Serving") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item.customizations.sugarLevels.forEach { sugarLevel ->
                            val isSelected = sugarLevel == selectedSugarLevel
                            SugarLevelChip(
                                label = sugarLevel,
                                isSelected = isSelected,
                                onClick = { viewModel.selectSugarLevel(sugarLevel) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Bottom Action Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardSurface,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .background(CardSurface)
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.decrementQuantity() },
                        enabled = quantity > 1,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease Quantity",
                            tint = if (quantity > 1) TextPrimary else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "$quantity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { viewModel.incrementQuantity() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase Quantity",
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Add to Cart Button
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Add to Cart • $${String.format(Locale.US, "%.2f", totalPrice)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CardSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun CardSectionContainer(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SizeOptionButton(
    sizeOption: SizeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) SelectedOptionBg else CardSurface
    val borderColor = if (isSelected) TerracottaPrimary else CardBorder
    val textColor = if (isSelected) TerracottaPrimary else TextPrimary

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            borderColor
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = sizeOption.label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "+$${String.format(Locale.US, "%.2f", sizeOption.extraPrice)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) TerracottaPrimary else TextSecondary
            )
        }
    }
}

@Composable
private fun MilkOptionRow(
    milkOption: MilkOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) SelectedOptionBg else CardSurface
    val borderColor = if (isSelected) TerracottaPrimary else CardBorder
    val textColor = if (isSelected) TerracottaPrimary else TextPrimary

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            borderColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = milkOption.name,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )

            Text(
                text = "+$${String.format(Locale.US, "%.2f", milkOption.extraPrice)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) TerracottaPrimary else TextSecondary
            )
        }
    }
}

@Composable
private fun SugarLevelChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) EspressoDark else CardSurface
    val borderColor = if (isSelected) EspressoDark else CardBorder
    val textColor = if (isSelected) CardSurface else TextPrimary

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}