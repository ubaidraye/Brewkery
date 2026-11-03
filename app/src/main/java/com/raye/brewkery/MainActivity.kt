package com.raye.brewkery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.raye.brewkery.data.model.OrderDetails
import com.raye.brewkery.ui.cart.CartScreen
import com.raye.brewkery.ui.detail.DetailScreen
import com.raye.brewkery.ui.menu.MenuScreen
import com.raye.brewkery.ui.order.OrderStatusScreen
import com.raye.brewkery.ui.theme.BrewkeryTheme
import com.raye.brewkery.ui.viewmodel.BrewkeryViewModel
import com.raye.brewkery.ui.viewmodel.CartViewModel
import com.raye.brewkery.ui.viewmodel.ItemDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

sealed interface Screen {
    data object Menu : Screen
    data class Detail(val itemId: Int) : Screen
    data object Cart : Screen
    data class OrderStatus(val orderDetails: OrderDetails) : Screen
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val menuViewModel: BrewkeryViewModel by viewModels()
    private val itemDetailViewModel: ItemDetailViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrewkeryTheme {
                BrewkeryApp(
                    menuViewModel = menuViewModel,
                    itemDetailViewModel = itemDetailViewModel,
                    cartViewModel = cartViewModel
                )
            }
        }
    }
}

@Composable
fun BrewkeryApp(
    menuViewModel: BrewkeryViewModel,
    itemDetailViewModel: ItemDetailViewModel,
    cartViewModel: CartViewModel
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Menu) }

    when (val screen = currentScreen) {
        is Screen.Menu -> {
            MenuScreen(
                viewModel = menuViewModel,
                onItemClick = { item ->
                    currentScreen = Screen.Detail(item.id)
                },
                onCartClick = {
                    currentScreen = Screen.Cart
                }
            )
        }

        is Screen.Detail -> {
            DetailScreen(
                itemId = screen.itemId,
                viewModel = itemDetailViewModel,
                onBackClick = {
                    currentScreen = Screen.Menu
                }
            )
        }

        is Screen.Cart -> {
            CartScreen(
                viewModel = cartViewModel,
                onBackClick = {
                    currentScreen = Screen.Menu
                },
                onOrderPlaced = { orderDetails ->
                    currentScreen = Screen.OrderStatus(orderDetails)
                }
            )
        }

        is Screen.OrderStatus -> {
            OrderStatusScreen(
                orderDetails = screen.orderDetails,
                onBackToMenu = {
                    currentScreen = Screen.Menu
                }
            )
        }
    }
}