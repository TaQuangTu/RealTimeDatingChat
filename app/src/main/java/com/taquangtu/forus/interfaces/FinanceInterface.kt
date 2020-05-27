package com.taquangtu.forus.interfaces

import com.taquangtu.forus.models.Transaction

interface FinanceInterface {
    interface Model {
        fun reloadTransactions();
        fun addNewTransaction(t: Transaction)
    }

    interface ViewModel {
        fun onTransactionsResult(t: List<Transaction>)
        fun onNewTransaction(t: Transaction)
        fun onTransactionRemove(t: Transaction)
        fun onError(message: String)
    }
}