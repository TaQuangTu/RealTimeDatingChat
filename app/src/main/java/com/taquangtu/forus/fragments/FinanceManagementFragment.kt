package com.taquangtu.forus.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taquangtu.forus.R
import com.taquangtu.forus.adapters.TransactionAdapter
import com.taquangtu.forus.base.BaseFragment
import com.taquangtu.forus.contexts.ForUsApplication
import com.taquangtu.forus.dialogs.NewTransactionDialog
import com.taquangtu.forus.models.Transaction
import com.taquangtu.forus.viewmodels.FinanceViewModel

//use MVVM architecture
class FinanceManagementFragment : BaseFragment(), NewTransactionDialog.Listener {
    private var mAddNewDialog: NewTransactionDialog? = null
    private lateinit var mRcvMonthList: RecyclerView
    private lateinit var mViewModel: FinanceViewModel
    private lateinit var mTvTotalAdded: TextView
    private lateinit var mTvTotalUsed: TextView
    private lateinit var mTvRemaining: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_finance, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemMenuAddTransaction) {
            if (mAddNewDialog == null) {
                mAddNewDialog = NewTransactionDialog()
                mAddNewDialog!!.mListen = this
            }
            mAddNewDialog!!.setType(Transaction.TYPE_TRANSACTION)
            mAddNewDialog!!.show(childFragmentManager, "dialog")
        }
        if (item.itemId == R.id.itemMenuAddMoney) {
            if (mAddNewDialog == null) {
                mAddNewDialog = NewTransactionDialog()
                mAddNewDialog!!.mListen = this
            }
            mAddNewDialog!!.setType(Transaction.TYPE_ADD_MONEY)
            mAddNewDialog!!.show(childFragmentManager, "dialog")
        }
        if (item.itemId == R.id.itemMenuReload) {
            mViewModel.reloadTransactions()
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.finace_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViews()
        setupList()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.reloadTransactions()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(activity!!).get(FinanceViewModel::class.java)
        observeDataChanges()
    }

    private fun mapViews() {
        mRcvMonthList = findView(R.id.rcvMonthList)
        mTvTotalAdded = findView(R.id.tvTotal)
        mTvTotalUsed = findView(R.id.tvUsed)
        mTvRemaining = findView(R.id.tvRemaining)
    }

    private fun observeDataChanges() {
        mViewModel.mLiveNewTransaction.observe(viewLifecycleOwner,
            Observer<Transaction> {
                (mRcvMonthList.adapter as TransactionAdapter).addNewTransaction(it); updateBalance()
            })
        mViewModel.mLiveTransList.observe(viewLifecycleOwner, Observer<List<Transaction>> {
            updateScreenUi(it)
        })
        mViewModel.mLiveMessage.observe(viewLifecycleOwner, Observer<String> {
            toast(it)
        })
        mViewModel.mLiveBalance.observe(viewLifecycleOwner, Observer {
            updateBalance()
        })
    }

    private fun updateScreenUi(transactions: List<Transaction>) {
        (mRcvMonthList.adapter as TransactionAdapter).mData =
            ((transactions as ArrayList).clone() as List<Transaction>)
        if (transactions != null && transactions.isNotEmpty()) {
            mRcvMonthList.smoothScrollToPosition((mRcvMonthList.adapter as TransactionAdapter).mData!!.size - 1)
        }
        updateBalance()
    }

    private fun updateBalance() {
        mTvTotalAdded.text = "Total: " + mViewModel.mLiveBalance.value?.get(0)
        mTvTotalUsed.text = "Used: " + mViewModel.mLiveBalance.value?.get(1)
        mTvRemaining.text = "Remaining: " + mViewModel.mLiveBalance.value?.get(2)
    }

    private fun setupList() {
        mRcvMonthList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val decorator =
            DividerItemDecoration(ForUsApplication.context, LinearLayoutManager.VERTICAL)
        mRcvMonthList.addItemDecoration(decorator)
        mRcvMonthList.adapter = TransactionAdapter()
    }

    override fun onAddNewTransaction(t: Transaction) {
        mViewModel.addNewTransaction(t)
    }
}