package com.taquangtu.forus.viewmodels

import androidx.lifecycle.MutableLiveData
import com.taquangtu.forus.base.BaseViewModel
import com.taquangtu.forus.interfaces.FinanceInterface
import com.taquangtu.forus.models.Transaction
import com.taquangtu.forus.repository.TransactionRepository

class FinanceViewModel : BaseViewModel(), FinanceInterface.ViewModel {
    val mTransactionRepository = TransactionRepository(this)
    val mLiveTransList = MutableLiveData<List<Transaction>>()
    val mLiveNewTransaction = MutableLiveData<Transaction>()
    val mLiveBalance =
        MutableLiveData<ArrayList<Int>>()  //array list with 3 values = (total, used, remain)

    fun reloadTransactions() {
        mTransactionRepository.reloadTransactions()
    }

    override fun onTransactionsResult(t: List<Transaction>) {
        mLiveTransList.value = t
        recalculateBalance()
    }

    fun recalculateBalance() {
        var total = 0
        var used = 0
        var remain = 0
        mLiveTransList.value?.forEach {
            if (it.type == Transaction.TYPE_TRANSACTION) {
                used += it.money
            } else if (it.type == Transaction.TYPE_ADD_MONEY) {
                total += it.money
            }
        }.also {
            remain = total - used
            val balance = ArrayList<Int>()
            balance.add(total)
            balance.add(used)
            balance.add(remain)
            mLiveBalance.value = balance
        }

    }

    override fun onNewTransaction(t: Transaction) {
        if (mLiveTransList.value == null) return

        (mLiveTransList.value as ArrayList)?.add(t).also {
            mLiveNewTransaction.value = t
            recalculateBalance()
        }
    }

    override fun onTransactionRemove(t: Transaction) {

    }

    override fun onError(message: String) {
        mLiveMessage.value = message
    }

    fun addNewTransaction(t: Transaction) {
        mTransactionRepository.addNewTransaction(t)
    }
}