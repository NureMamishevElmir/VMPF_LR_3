package com.nure.vmpf.catalog.data

import com.nure.vmpf.catalog.data.model.Category
import com.nure.vmpf.catalog.data.model.Order
import com.nure.vmpf.catalog.data.model.OrderRequest
import com.nure.vmpf.catalog.data.model.Product
import com.nure.vmpf.catalog.data.remote.RetrofitClient

/**
 * Репозиторій товарів і замовлень — єдина точка доступу до мережевого API.
 * ViewModel-и працюють лише з цим класом і не знають про деталі Retrofit.
 * Методи повертають [Result], тож виклична сторона зручно обробляє успіх/помилку.
 */
class ProductRepository {

    private val api = RetrofitClient.api

    suspend fun getProducts(
        category: String? = null,
        search: String? = null,
        sort: String? = null,
    ): Result<List<Product>> = runCatching {
        api.getProducts(
            category = category?.takeIf { it.isNotBlank() && it != "all" },
            search = search?.takeIf { it.isNotBlank() },
            sort = sort?.takeIf { it.isNotBlank() },
        )
    }

    suspend fun getCategories(): Result<List<Category>> = runCatching {
        api.getCategories()
    }

    suspend fun getProduct(id: Int): Result<Product> = runCatching {
        api.getProduct(id)
    }

    suspend fun createOrder(request: OrderRequest): Result<Order> = runCatching {
        api.createOrder(request)
    }

    suspend fun getOrder(id: Int): Result<Order> = runCatching {
        api.getOrder(id)
    }
}
