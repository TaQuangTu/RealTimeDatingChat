package com.taquangtu.forus.repository

import android.content.Context
import com.google.firebase.database.*
import com.taquangtu.forus.contexts.AppContext
import com.taquangtu.forus.contexts.ForUsApplication
import com.taquangtu.forus.interfaces.LoginInterface
import kotlin.random.Random

class LoginRepository(val mViewModel: LoginInterface.ViewModel) : LoginInterface.Model {
    companion object {
        fun logout() {
            val checkpoint =
                ForUsApplication.context.getSharedPreferences("user", Context.MODE_PRIVATE)
            checkpoint.edit().remove("pass").apply()
        }
    }

    fun checkLogin() {
        val checkpoint = ForUsApplication.context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = checkpoint.edit()
        var pass = checkpoint.getString("pass", null)
        if (pass != null) {
            mViewModel.onLoginSuccess(pass)
        }
    }

    override fun login(userId: String) {
        val rootRef = FirebaseDatabase.getInstance().reference.child("users")
        val query: Query = rootRef.orderByChild("token").equalTo(userId)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mViewModel.onLoginError("invalid user")
                } else {
                    mViewModel.onLoginSuccess(userId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                mViewModel.onLoginError(databaseError.message)
            }
        }
        query.addValueEventListener(eventListener)
    }

    fun writeToCache(pass: String) {
        val checkpoint = ForUsApplication.context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = checkpoint.edit()
        AppContext.userId = pass

        if (pass.equals("test1") || pass.equals("test2")) {
            AppContext.roomId = "0"
        } else {
            AppContext.roomId = Random.nextInt(1, 50).toString()
        }
        editor.putString("pass", pass).apply()
    }
}