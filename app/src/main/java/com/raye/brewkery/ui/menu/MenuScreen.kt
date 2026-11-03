package com.raye.brewkery.ui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raye.brewkery.data.model.BrewkeryItem
import com.raye.brewkery.ui.components.ErrorView
import com.raye.brewkery.ui.components.LoadingView
import com.raye.brewkery.ui.components.MenuContentView
import com.raye.brewkery.ui.components.ProceedToCheckoutCard
import com.raye.brewkery.ui.theme.CreamBackground
import com.raye.brewkery.ui.viewmodel.BrewkeryUiState
import com.raye.brewkery.ui.viewmodel.BrewkeryViewModel

@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    viewModel: BrewkeryViewModel = hiltViewModel(),
    onItemClick: (BrewkeryItem) -> Unit,
    onCartClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartItemCount.collectAsStateWithLifecycle()

    // Wire up the live state flow calculation mapping
    val cartTotalPrice by viewModel.cartTotalPrice.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CreamBackground,
        bottomBar = {
            AnimatedVisibility(
                visible = cartCount > 0,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                ProceedToCheckoutCard(
                    cartCount = cartCount,
                    totalPrice = cartTotalPrice, // Emits strings like "$9.40" live!
                    onCheckoutClick = onCartClick
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is BrewkeryUiState.Loading -> {
                    LoadingView(modifier = Modifier.align(Alignment.Center))
                }

                is BrewkeryUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.fetchBrewkeryData() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is BrewkeryUiState.Success -> {
                    MenuContentView(
                        meta = state.meta,
                        categories = state.categories,
                        items = state.items,
                        cartCount = cartCount,
                        onItemClick = onItemClick,
                        onCartClick = onCartClick
                    )
                }
            }
        }
    }
}
