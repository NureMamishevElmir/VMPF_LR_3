package com.nure.vmpf.catalog.ui.ordersuccess

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nure.vmpf.catalog.data.ProductRepository
import com.nure.vmpf.catalog.data.model.Order
import com.nure.vmpf.catalog.data.remote.parseApiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderSuccessUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val order: Order? = null,
)

class OrderSuccessViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _state = MutableStateFlow(OrderSuccessUiState())
    val state: StateFlow<OrderSuccessUiState> = _state.asStateFlow()

    fun load(orderId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getOrder(orderId)
                .onSuccess { order -> _state.update { it.copy(isLoading = false, order = order) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = parseApiError(e)) } }
        }
    }
}
