package com.nure.vmpf.catalog.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nure.vmpf.catalog.data.CartRepository
import com.nure.vmpf.catalog.data.ProductRepository
import com.nure.vmpf.catalog.data.model.Customer
import com.nure.vmpf.catalog.data.model.OrderItemRequest
import com.nure.vmpf.catalog.data.model.OrderRequest
import com.nure.vmpf.catalog.data.remote.parseApiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val name: String = "",
    val email: String = "",
    val address: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val addressError: String? = null,
    val isSubmitting: Boolean = false,
    val serverError: String? = null,
    val placedOrderId: Int? = null,
)

/**
 * ViewModel оформлення замовлення.
 *
 * Валідація відбувається на двох рівнях (як у вебверсії ЛР2):
 *  - клієнтська — у [validate] перед відправкою;
 *  - серверна — повторно на сервері ЛР2, помилки якого показуються користувачу.
 */
class CheckoutViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _state = MutableStateFlow(CheckoutUiState())
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = null) }
    fun onEmailChange(value: String) = _state.update { it.copy(email = value, emailError = null) }
    fun onAddressChange(value: String) = _state.update { it.copy(address = value, addressError = null) }

    private val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    private fun validate(): Boolean {
        val s = _state.value
        val nameError = if (s.name.trim().length < 2) "Ім'я має містити щонайменше 2 символи" else null
        val emailError = if (!emailRegex.matches(s.email.trim())) "Некоректна email-адреса" else null
        val addressError =
            if (s.address.trim().length < 5) "Вкажіть адресу доставки (мін. 5 символів)" else null

        _state.update {
            it.copy(nameError = nameError, emailError = emailError, addressError = addressError)
        }
        return nameError == null && emailError == null && addressError == null
    }

    fun submit() {
        if (_state.value.isSubmitting) return
        if (!validate()) return

        val cart = CartRepository.items.value
        if (cart.isEmpty()) {
            _state.update { it.copy(serverError = "Кошик порожній") }
            return
        }

        val s = _state.value
        val request = OrderRequest(
            customer = Customer(
                name = s.name.trim(),
                email = s.email.trim(),
                address = s.address.trim(),
            ),
            items = cart.map { OrderItemRequest(id = it.productId, quantity = it.quantity) },
        )

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, serverError = null) }
            repository.createOrder(request)
                .onSuccess { order ->
                    CartRepository.clear()
                    _state.update { it.copy(isSubmitting = false, placedOrderId = order.id) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isSubmitting = false, serverError = parseApiError(e)) }
                }
        }
    }
}
