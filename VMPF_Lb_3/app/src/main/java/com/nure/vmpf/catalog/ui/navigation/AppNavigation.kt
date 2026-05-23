package com.nure.vmpf.catalog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nure.vmpf.catalog.ui.cart.CartScreen
import com.nure.vmpf.catalog.ui.catalog.CatalogScreen
import com.nure.vmpf.catalog.ui.checkout.CheckoutScreen
import com.nure.vmpf.catalog.ui.detail.ProductDetailScreen
import com.nure.vmpf.catalog.ui.ordersuccess.OrderSuccessScreen

/**
 * Маршрути застосунку. Аналог конфігурації React Router у вебверсії ЛР2:
 *   "/"            -> catalog
 *   "/product/:id" -> product/{id}
 *   "/cart"        -> cart
 *   "/checkout"    -> checkout
 *   "/order/:id"   -> order/{id}
 */
object Routes {
    const val CATALOG = "catalog"
    const val PRODUCT = "product/{id}"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val ORDER = "order/{id}"

    fun product(id: Int) = "product/$id"
    fun order(id: Int) = "order/$id"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.CATALOG) {

        // Рівень 1 — каталог товарів
        composable(Routes.CATALOG) {
            CatalogScreen(
                onProductClick = { id -> navController.navigate(Routes.product(id)) },
                onCartClick = { navController.navigate(Routes.CART) },
            )
        }

        // Рівень 1 — деталі товару
        composable(
            route = Routes.PRODUCT,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            ProductDetailScreen(
                productId = id,
                onBack = { navController.popBackStack() },
                onCartClick = { navController.navigate(Routes.CART) },
            )
        }

        // Рівень 2 — кошик
        composable(Routes.CART) {
            CartScreen(
                onBack = { navController.popBackStack() },
                onCheckout = { navController.navigate(Routes.CHECKOUT) },
            )
        }

        // Рівень 2 — оформлення замовлення
        composable(Routes.CHECKOUT) {
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onOrderPlaced = { orderId ->
                    navController.navigate(Routes.order(orderId)) {
                        // прибираємо кошик та оформлення зі стеку повернення
                        popUpTo(Routes.CATALOG)
                    }
                },
            )
        }

        // Рівень 2 — підтвердження замовлення
        composable(
            route = Routes.ORDER,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            OrderSuccessScreen(
                orderId = id,
                onBackToCatalog = {
                    navController.navigate(Routes.CATALOG) {
                        popUpTo(Routes.CATALOG) { inclusive = true }
                    }
                },
            )
        }
    }
}
