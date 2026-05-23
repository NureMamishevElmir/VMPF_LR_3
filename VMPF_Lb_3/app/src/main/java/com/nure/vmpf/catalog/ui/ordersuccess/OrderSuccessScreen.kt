package com.nure.vmpf.catalog.ui.ordersuccess

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nure.vmpf.catalog.data.model.Order
import com.nure.vmpf.catalog.ui.components.ErrorState
import com.nure.vmpf.catalog.ui.components.LoadingState
import com.nure.vmpf.catalog.ui.util.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSuccessScreen(
    orderId: Int,
    onBackToCatalog: () -> Unit,
    viewModel: OrderSuccessViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(orderId) { viewModel.load(orderId) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Замовлення прийнято") }) },
    ) { padding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(padding))
            state.error != null -> ErrorState(
                message = state.error!!,
                onRetry = { viewModel.load(orderId) },
                modifier = Modifier.padding(padding),
            )

            state.order != null -> OrderContent(
                order = state.order!!,
                modifier = Modifier.padding(padding),
                onBackToCatalog = onBackToCatalog,
            )
        }
    }
}

@Composable
private fun OrderContent(
    order: Order,
    modifier: Modifier = Modifier,
    onBackToCatalog: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp),
        )
        Text(
            text = "Дякуємо за замовлення!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Замовлення №${order.id} • статус: ${order.status}",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Отримувач",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(order.customer.name)
                Text(order.customer.email)
                Text(order.customer.address)

                Spacer(Modifier.size(12.dp))
                Divider()
                Spacer(Modifier.size(12.dp))

                Text(
                    "Склад замовлення",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.size(4.dp))
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "${item.title} × ${item.quantity}",
                            modifier = Modifier.weight(1f),
                        )
                        Text(formatPrice(item.subtotal))
                    }
                }

                Spacer(Modifier.size(8.dp))
                Divider()
                Spacer(Modifier.size(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Разом:", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = formatPrice(order.total),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        Button(onClick = onBackToCatalog, modifier = Modifier.fillMaxWidth()) {
            Text("Повернутися до каталогу")
        }
    }
}
