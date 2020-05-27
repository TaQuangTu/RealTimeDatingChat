package com.taquangtu.forus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.taquangtu.forus.R
import com.taquangtu.forus.contexts.ForUsApplication
import com.taquangtu.forus.models.Transaction
import java.text.SimpleDateFormat

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    var mData: List<Transaction>? = null
        get() = field
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class TransactionViewHolder : RecyclerView.ViewHolder {
        val tvTransactionName: TextView
        val tvUserName: TextView
        val tvTime: TextView
        val tvAmount: TextView

        constructor(itemView: View) : super(itemView) {
            tvTransactionName = itemView.findViewById(R.id.tvTransactionName)
            tvUserName = itemView.findViewById(R.id.tvUserName)
            tvTime = itemView.findViewById(R.id.tvTime)
            tvAmount = itemView.findViewById(R.id.tvAmount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_transaction,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        val trans = mData!![position]
        return trans.type
    }

    override fun onBindViewHolder(holderTransaction: TransactionViewHolder, position: Int) {
        val trans = mData!![position]
        if (trans.type == Transaction.TYPE_ADD_MONEY) {
            holderTransaction.tvTransactionName.setTextColor(
                ContextCompat.getColor(
                    ForUsApplication.context,
                    R.color.green
                )
            )
            holderTransaction.tvTransactionName.text = trans.userId + " added " + trans.money
        } else {
            holderTransaction.tvTransactionName.setTextColor(
                ContextCompat.getColor(
                    ForUsApplication.context,
                    R.color.black
                )
            )
            holderTransaction.tvTransactionName.text = trans.name
        }
        holderTransaction.tvAmount.text = "Amount: " + trans.money + "K"
        holderTransaction.tvUserName.text = "By " + trans.userId
        val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
        val time = formatter.format(trans.date)
        holderTransaction.tvTime.text = time
    }

    fun addNewTransaction(it: Transaction?) {
        if (mData == null) {
            mData = ArrayList()
        }
        (mData as ArrayList).add(it!!)
        notifyItemInserted(mData!!.size - 1)
    }
}