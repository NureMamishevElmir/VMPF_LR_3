package com.nure.vmpf.catalog.data.model

/**
 * Моделі даних застосунку. Структура повторює відповіді REST API сервера ЛР2
 * (Node.js + Express), тож Gson десеріалізує JSON безпосередньо в ці класи.
 */

/** Товар каталогу. */
data class Product(
    val id: Int,
    val title: String,
    val category: String,
    val price: Int,
    val rating: Double,
    val stock: Int,
    val image: String,
    val description: String,
)

/** Категорія товарів. */
data class Category(
    val id: String,
    val name: String,
)

/** Позиція в оформленому замовленні (відповідь сервера). */
data class OrderItem(
    val id: Int,
    val title: String,
    val price: Int,
    val quantity: Int,
    val subtotal: Int,
)

/** Дані покупця. */
data class Customer(
    val name: String,
    val email: String,
    val address: String,
)

/** Оформлене замовлення (відповідь сервера на POST /api/orders). */
data class Order(
    val id: Int,
    val customer: Customer,
    val items: List<OrderItem>,
    val total: Int,
    val status: String,
    val createdAt: String,
)

/** Тіло запиту на створення замовлення: { customer, items: [{ id, quantity }] }. */
data class OrderRequest(
    val customer: Customer,
    val items: List<OrderItemRequest>,
)

data class OrderItemRequest(
    val id: Int,
    val quantity: Int,
)

/** Формат тіла помилки сервера: { error } або { errors: [...] }. */
data class ApiError(
    val error: String? = null,
    val errors: List<String>? = null,
)
