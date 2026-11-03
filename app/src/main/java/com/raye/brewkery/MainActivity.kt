package com.raye.brewkery

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.raye.brewkery.data.model.convertJsonToOrderDetails
import com.raye.brewkery.data.model.convertOrderDetailsToJson
import com.raye.brewkery.ui.cart.CartScreen
import com.raye.brewkery.ui.detail.DetailScreen
import com.raye.brewkery.ui.menu.MenuScreen
import com.raye.brewkery.ui.order.OrderStatusScreen
import com.raye.brewkery.ui.theme.BrewkeryTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class Screen(
    val route: String
) {
    data object Menu : Screen("home")
    data object Detail : Screen("detail/{itemId}") {
        fun createRoute(itemId: Int): String = "detail/$itemId"
    }

    data object Cart : Screen("cart")
    data object OrderStatus : Screen("orderStatus/{orderDetailsJson}") {
        fun createRoute(json: String) = "orderStatus/$json"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrewkeryTheme {
                BrewkeryApp()
            }
        }
    }
}

@Composable
fun BrewkeryApp() {
    val navController = rememberNavController()

    val onBackClick: () -> Unit = { navController.popBackStack() }
    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route
    ) {
        composable(
            route = Screen.Menu.route
        ) {
            MenuScreen(
                onItemClick = { item -> navController.navigate(Screen.Detail.createRoute(item.id)) },
                onCartClick = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("itemId") { type = NavType.IntType }
            )
        ) {
            val itemId = it.arguments?.getInt("itemId") ?: 0

            DetailScreen(
                itemId = itemId,
                onBackClick = onBackClick,
            )
        }

        composable(
            route = Screen.Cart.route
        ) {
            CartScreen(
                onBackClick = onBackClick,
                onOrderPlaced = { orderDetails ->
                    val jsonString = convertOrderDetailsToJson(orderDetails)
                    val sanitizedJson = Uri.encode(jsonString)
                    navController.navigate(Screen.OrderStatus.createRoute(sanitizedJson))
                },
            )
        }

        composable(
            route = Screen.OrderStatus.route,
            arguments = listOf(
                navArgument("orderDetailsJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // 1. Fetch the raw argument string
            val argumentPayload = backStackEntry.arguments?.getString("orderDetailsJson") ?: ""

            // 2. Decode it back using Android's native Uri tool to restore full URLs
            val rawJson = Uri.decode(argumentPayload)

            // 3. De-serialize back to your complex object tree
            val orderDetails = convertJsonToOrderDetails(rawJson)

            OrderStatusScreen(
                orderDetails = orderDetails,
                onBackToMenu = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                }
            )
        }
    }

//    when (val screen = currentScreen) {
//        is Screen.Menu -> {
//            MenuScreen(
//                viewModel = menuViewModel,
//                onItemClick = { item ->
//                    currentScreen = Screen.Detail(item.id)
//                },
//                onCartClick = {
//                    currentScreen = Screen.Cart
//                }
//            )
//        }
//
//        is Screen.Detail -> {
//            DetailScreen(
//                itemId = screen.itemId,
//                viewModel = itemDetailViewModel,
//                onBackClick = {
//                    currentScreen = Screen.Menu
//                }
//            )
//        }
//
//        is Screen.Cart -> {
//            CartScreen(
//                viewModel = cartViewModel,
//                onBackClick = {
//                    currentScreen = Screen.Menu
//                },
//                onOrderPlaced = { orderDetails ->
//                    currentScreen = Screen.OrderStatus(orderDetails)
//                }
//            )
//        }
//
//        is Screen.OrderStatus -> {
//            OrderStatusScreen(
//                orderDetails = screen.orderDetails,
//                onBackToMenu = {
//                    currentScreen = Screen.Menu
//                }
//            )
//        }
//    }
}