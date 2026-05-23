package com.nure.vmpf.catalog.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nure.vmpf.catalog.data.ProductRepository
import com.nure.vmpf.catalog.data.model.Category
import com.nure.vmpf.catalog.data.model.Product
import com.nure.vmpf.catalog.data.remote.parseApiError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Варіанти сортування (значення збігаються з параметром ?sort= сервера ЛР2). */
enum class SortOption(val apiValue: String?, val label: String) {
    DEFAULT(null, "За замовчуванням"),
    PRICE_ASC("price_asc", "Ціна: спочатку дешевші"),
    PRICE_DESC("price_desc", "Ціна: спочатку дорожчі"),
    RATING("rating", "За рейтингом"),
}

/** Незмінний стан екрана каталогу. */
data class CatalogUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: String = "all",
    val search: String = "",
    val sort: SortOption = SortOption.DEFAULT,
)

class CatalogViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _state = MutableStateFlow(CatalogUiState())
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCategories()
        loadProducts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().onSuccess { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }
    }

    fun loadProducts() {
        val s = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getProducts(
                category = s.selectedCategory,
                search = s.search,
                sort = s.sort.apiValue,
            ).onSuccess { products ->
                _state.update { it.copy(isLoading = false, products = products, error = null) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = parseApiError(e)) }
            }
        }
    }

    fun onCategorySelected(categoryId: String) {
        _state.update { it.copy(selectedCategory = categoryId) }
        loadProducts()
    }

    fun onSortSelected(sort: SortOption) {
        _state.update { it.copy(sort = sort) }
        loadProducts()
    }

    /** Пошук із невеликою затримкою (debounce), щоб не слати запит на кожну літеру. */
    fun onSearchChanged(query: String) {
        _state.update { it.copy(search = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            loadProducts()
        }
    }
}
