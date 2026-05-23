package com.nure.vmpf.catalog.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nure.vmpf.catalog.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Позиція кошика. Зберігаємо мінімум полів, потрібних для відображення
 * та оформлення замовлення.
 */
data class CartItem(
    val productId: Int,
    val title: String,
    val price: Int,
    val image: String,
    val stock: Int,
    val quantity: Int,
) {
    val subtotal: Int get() = price * quantity
}

/**
 * Глобальний стан кошика (аналог CartContext із React-версії ЛР2).
 *
 * Реалізовано як singleton-об'єкт із [StateFlow], за яким реактивно
 * оновлюється інтерфейс. Вміст кошика зберігається у [SharedPreferences]
 * у форматі JSON, тож не зникає після перезапуску застосунку
 * (аналог localStorage у вебверсії).
 */
object CartRepository {

    private const val PREFS = "cart_prefs"
    private const val KEY_ITEMS = "items"

    private val gson = Gson()
    private lateinit var prefs: SharedPreferences

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    /** Викликається один раз із CatalogApplication.onCreate(). */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        _items.value = load()
    }

    // --- Похідні значення ---

    fun totalQuantity(): Int = _items.value.sumOf { it.quantity }

    fun totalPrice(): Int = _items.value.sumOf { it.subtotal }

    // --- Операції над кошиком ---

    /** Додати товар (або збільшити кількість, якщо він уже в кошику). */
    fun add(product: Product, quantity: Int = 1) {
        val current = _items.value.toMutableList()
        val index = current.indexOfFirst { it.productId == product.id }
        if (index >= 0) {
            val existing = current[index]
            val newQty = (existing.quantity + quantity).coerceAtMost(product.stock)
            current[index] = existing.copy(quantity = newQty)
        } else {
            current.add(
                CartItem(
                    productId = product.id,
                    title = product.title,
                    price = product.price,
                    image = product.image,
                    stock = product.stock,
                    quantity = quantity.coerceIn(1, product.stock),
                ),
            )
        }
        commit(current)
    }

    /** Встановити конкретну кількість (у межах залишку на складі). */
    fun setQuantity(productId: Int, quantity: Int) {
        if (quantity < 1) {
            remove(productId)
            return
        }
        val current = _items.value.map {
            if (it.productId == productId) {
                it.copy(quantity = quantity.coerceAtMost(it.stock))
            } else {
                it
            }
        }
        commit(current)
    }

    fun remove(productId: Int) {
        commit(_items.value.filterNot { it.productId == productId })
    }

    fun clear() {
        commit(emptyList())
    }

    // --- Збереження ---

    private fun commit(items: List<CartItem>) {
        _items.value = items
        prefs.edit().putString(KEY_ITEMS, gson.toJson(items)).apply()
    }

    private fun load(): List<CartItem> {
        val json = prefs.getString(KEY_ITEMS, null) ?: return emptyList()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return runCatching { gson.fromJson<List<CartItem>>(json, type) }
            .getOrNull() ?: emptyList()
    }
}
