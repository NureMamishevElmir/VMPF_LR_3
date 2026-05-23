package com.nure.vmpf.catalog.data.remote

import com.nure.vmpf.catalog.data.model.Category
import com.nure.vmpf.catalog.data.model.Order
import com.nure.vmpf.catalog.data.model.OrderRequest
import com.nure.vmpf.catalog.data.model.Product
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Опис маршрутів REST API сервера ЛР2. Retrofit генерує реалізацію цього
 * інтерфейсу. Усі методи — suspend, тож викликаються з корутін без блокування.
 */
interface ApiService {

    /** GET /api/products?category=&search=&sort= — список товарів з фільтрами. */
    @GET("api/products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("sort") sort: String? = null,
    ): List<Product>

    /** GET /api/products/categories — список категорій. */
    @GET("api/products/categories")
    suspend fun getCategories(): List<Category>

    /** GET /api/products/{id} — деталі товару. */
    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Product

    /** POST /api/orders — оформлення замовлення. */
    @POST("api/orders")
    suspend fun createOrder(@Body body: OrderRequest): Order

    /** GET /api/orders/{id} — деталі замовлення. */
    @GET("api/orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): Order
}
