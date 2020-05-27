package com.taquangtu.forus.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.taquangtu.forus.R
import com.taquangtu.forus.activities.LandingPageActivity
import com.taquangtu.forus.base.BaseFragment
import com.taquangtu.forus.viewmodels.LoginViewModel

//use MVVM architecture
class LoginFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBtnLogin: Button
    lateinit var mEdtPassword: EditText
    private lateinit var mViewModel: LoginViewModel

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViews()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        mViewModel.mLiveUserId.observe(viewLifecycleOwner, Observer<String> { t ->
            if (t == null) {
                toast("Fail when login, try again!")
            } else {
                mViewModel.writeToCache(t)
                goToLandingPage(t)
            }
        })
        mViewModel.mLiveMessage.observe(viewLifecycleOwner,
            Observer<String> { t -> toast(t) })
        mViewModel.checkLogin() //auto login if there was a logged in account
    }

    fun goToLandingPage(userId: String) {
        val intent = Intent(this.activity, LandingPageActivity::class.java)
        startActivity(intent)
        activity!!.finish()
    }

    fun mapViews() {
        mBtnLogin = view!!.findViewById(R.id.btnLogin)
        mEdtPassword = view!!.findViewById(R.id.edtPassword)
        mBtnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == mBtnLogin) {
            val pass = mEdtPassword.text.toString().trim()
            if (pass == null || pass.isEmpty()) {
                toast("Password must not be empty, try again!")
            } else {
                mViewModel.login(pass)
            }
        }
    }
}
