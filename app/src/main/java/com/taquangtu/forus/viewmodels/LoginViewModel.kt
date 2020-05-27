package com.taquangtu.forus.viewmodels

import androidx.lifecycle.MutableLiveData
import com.taquangtu.forus.base.BaseViewModel
import com.taquangtu.forus.interfaces.LoginInterface
import com.taquangtu.forus.repository.LoginRepository

class LoginViewModel : BaseViewModel(), LoginInterface.ViewModel {
    companion object { //make it as company object to call it every where when need
        fun logout() {
            LoginRepository.logout()
        }
    }

    private val mLoginRepository = LoginRepository(this)
    var mLiveUserId = MutableLiveData<String>()

    fun checkLogin() {
        mLoginRepository.checkLogin()
    }

    fun writeToCache(pass: String) {
        mLoginRepository.writeToCache(pass)
    }

    fun login(pass: String) {
        mLoginRepository.login(pass)
    }

    override fun onLoginError(message: String) {
        mLiveMessage.value = message
    }

    override fun onLoginSuccess(userId: String) {
        mLiveUserId.value = userId
    }

    override fun onLogoutSuccess() {
        mLiveMessage.value = "Logout success"
    }
}
