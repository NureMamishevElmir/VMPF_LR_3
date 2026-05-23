package com.nure.vmpf.catalog.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nure.vmpf.catalog.data.ProductRepository
import com.nure.vmpf.catalog.data.model.Product
import com.nure.vmpf.catalog.data.remote.parseApiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val product: Product? = null,
)

class ProductDetailViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _state = MutableStateFlow(ProductDetailUiState())
    val state: StateFlow<ProductDetailUiState> = _state.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getProduct(id)
                .onSuccess { product ->
                    _state.update { it.copy(isLoading = false, product = product) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = parseApiError(e)) }
                }
        }
    }
}
