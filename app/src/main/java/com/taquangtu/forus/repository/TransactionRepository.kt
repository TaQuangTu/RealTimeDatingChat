package com.taquangtu.forus.repository

import com.google.firebase.database.*
import com.taquangtu.forus.contexts.AppContext
import com.taquangtu.forus.interfaces.FinanceInterface
import com.taquangtu.forus.interfaces.FinanceInterface.Model
import com.taquangtu.forus.models.Transaction

class TransactionRepository(val mViewModel: FinanceInterface.ViewModel) : Model {
    companion object {
        val TRANSACTIONS = "transactions"
    }

    init {
        registNewTransactionListener()
    }

    override fun reloadTransactions() {
        FirebaseDatabase.getInstance().reference.child(AppContext.roomId).child(TRANSACTIONS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    mViewModel.onError(p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val transactions = ArrayList<Transaction>()
                    for (child in p0.children) {
                        val trans = child.getValue(Transaction::class.java)
                        transactions.add(trans!!)
                    }
                    mViewModel.onTransactionsResult(transactions)
                }
            })
    }

    private fun registNewTransactionListener() {
        FirebaseDatabase.getInstance().reference.child(AppContext.roomId).child(TRANSACTIONS)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    mViewModel.onError(p0.message)
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val trans = p0.getValue(Transaction::class.java)
                    mViewModel.onNewTransaction(trans!!)
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    val trans = p0.getValue(Transaction::class.java)
                    mViewModel.onTransactionRemove(trans!!)
                }
            })
    }

    override fun addNewTransaction(t: Transaction) {
        val ref =
            FirebaseDatabase.getInstance().reference.child(AppContext.roomId).child(TRANSACTIONS)
        val key = ref.push().key
        if (key == null) {
            mViewModel.onError("Can not add, check your internet")
        } else {
            t.id = key!!
            ref.child(key!!).setValue(t).addOnCanceledListener {
                mViewModel.onError("Can not add, check your internet")
            }.addOnFailureListener {
                mViewModel.onError(it.localizedMessage)
            }
        }
    }
}