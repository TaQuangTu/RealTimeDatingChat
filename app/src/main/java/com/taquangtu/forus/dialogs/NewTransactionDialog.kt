package com.taquangtu.forus.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.taquangtu.forus.R
import com.taquangtu.forus.contexts.AppContext
import com.taquangtu.forus.models.Transaction

class NewTransactionDialog : DialogFragment() {
    var mListen: Listener? = null
    var mType: Int = Transaction.TYPE_ADD_MONEY
    lateinit var mEdtTransactionName: EditText
    lateinit var mEdtAmount: EditText
    lateinit var mBtnAdd: Button
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(context)
        val v = LayoutInflater.from(context).inflate(R.layout.dialog_new_transaction, null)
        builder.setTitle("Add new")
        val dialog = builder.create()
        dialog.setView(v)
        return dialog
    }

    override fun onResume() {
        super.onResume()
        presentData()
    }

    override fun onStart() {
        super.onStart()
        mEdtTransactionName = dialog!!.findViewById(R.id.edtTransactionName)
        mEdtAmount = dialog!!.findViewById(R.id.edtAmount)
        mBtnAdd = dialog!!.findViewById(R.id.btnAdd)
        mBtnAdd.setOnClickListener {
            val name = mEdtTransactionName.text.toString().trim()
            val amount = mEdtAmount.text.toString().trim()
            val time = System.currentTimeMillis()
            val userId = AppContext.userId
            val trans = Transaction(time, name, Integer.parseInt(amount), userId, mType)
            mListen?.onAddNewTransaction(trans)
            dismiss()
        }
    }

    fun setType(typeTransaction: Int) {
        mType = typeTransaction
    }

    fun presentData() {
        if (mType == Transaction.TYPE_ADD_MONEY) {
            mEdtTransactionName.visibility = GONE
            dialog?.setTitle("Add money")
        } else {
            dialog?.setTitle("Add transaction")
            mEdtTransactionName.visibility = VISIBLE
        }
    }

    interface Listener {
        fun onAddNewTransaction(t: Transaction);
    }
}