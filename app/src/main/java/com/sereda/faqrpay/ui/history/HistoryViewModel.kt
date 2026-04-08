package com.sereda.faqrpay.ui.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sereda.faqrpay.data.local.entity.TransactionEntity
import com.sereda.faqrpay.domain.TransactionRepository
import com.sereda.faqrpay.util.ServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(private val repository: TransactionRepository) : ViewModel() {

    /**
     * Converts the Flow from Room into a StateFlow.
     * stateIn ensures the database is only queried when the UI is actually visible.
     */
    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

/**
 * Factory to provide the TransactionRepository to the ViewModel.
 */
class HistoryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // We use the ServiceLocator to get the repository instance
        val repo = ServiceLocator.provideTransactionRepository(context)
        return HistoryViewModel(repo) as T
    }
}