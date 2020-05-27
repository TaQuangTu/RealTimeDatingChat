package com.taquangtu.forus.interfaces

class LoginInterface {
    interface Model {
        fun login(userId: String)
    }

    interface ViewModel {
        fun onLoginError(message: String)
        fun onLoginSuccess(userId: String)
        fun onLogoutSuccess()
    }
}