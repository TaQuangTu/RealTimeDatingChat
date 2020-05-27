package com.taquangtu.forus.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.taquangtu.forus.R
import com.taquangtu.forus.activities.FinanceManagementActivity
import com.taquangtu.forus.activities.MainActivity
import com.taquangtu.forus.base.BaseFragment
import kotlinx.android.synthetic.main.landing_page_fragment.*

class LandingPageFragment : BaseFragment(), View.OnClickListener {
    lateinit var mImvChat: ImageView
    lateinit var mImvFinance: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.landing_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViews()
    }

    fun mapViews() {
        mImvChat = findView(R.id.imvChat)
        mImvFinance = findView(R.id.imvFinance)
        mImvChat.setOnClickListener(this)
        mImvFinance.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == imvChat) {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
        if (v == imvFinance) {
            val intent = Intent(context, FinanceManagementActivity::class.java)
            startActivity(intent)
        }
    }
}