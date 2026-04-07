package com.example.faqrpay.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.faqrpay.data.local.entity.TransactionEntity

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(factory = HistoryViewModelFactory(LocalContext.current))
) {
    // Observe the StateFlow from the ViewModel
    // Using collectAsState() ensures the UI recomposes whenever the DB changes
    val transactions by viewModel.transactions.collectAsState()

    val groupedTransactions = transactions.groupBy { it.date.toLocalDate() }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Historie plateb",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (transactions.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Zatím žádné transakce",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // 2. Iterate through each group (date)
                groupedTransactions.forEach { (date, dayTransactions) ->

                    // 3. Calculate sum of PAID transactions for this specific day
                    val dailySum = dayTransactions
                        .filter { it.isPaid }
                        .sumOf { it.amount }

                    // 4. Add a Header for the day
                    item {
                        DailyHeader(date = date, totalAmount = dailySum)
                    }

                    // 5. Add the individual transactions for that day
                    items(dayTransactions) { transaction ->
                        TransactionItem(transaction)
                    }

                    // Add space between days
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            // Subtle green background for paid items
            containerColor = if (transaction.isPaid)
                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${transaction.amount} ${transaction.currency}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (transaction.isPaid) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant

                    )
                }

                val formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                Text(
                    text = transaction.date.format(formatter), // Displays the ISO date string
                    style = MaterialTheme.typography.bodySmall,
                    color = if (transaction.isPaid) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "ID: ${transaction.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (transaction.isPaid) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status Icon
            if (transaction.isPaid) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Zaplaceno",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = Color.Gray.copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "❌", // UTF-8 character
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyHeader(date: java.time.LocalDate, totalAmount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE d. MMMM", java.util.Locale("cs", "CZ"))

        Text(
            text = date.format(formatter).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold
        )

        if (totalAmount > 0) {
            Text(
                text = "Celkem: ${"%.2f".format(totalAmount)} CZK",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}