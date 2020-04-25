package com.taquangtu.forus.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.taquangtu.forus.contexts.AppContext
import com.taquangtu.forus.contexts.ForUsApplication
import com.taquangtu.forus.base.BaseViewModel
import kotlin.random.Random


class LoginViewModel : BaseViewModel() {
    companion object {
        fun logout() {
            val checkpoint =
                ForUsApplication.context.getSharedPreferences("user", Context.MODE_PRIVATE)
            checkpoint.edit().remove("pass").apply()
        }
    }

    var liveData = MutableLiveData<String>()
    fun checkLogin() {
        val checkpoint = ForUsApplication.context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = checkpoint.edit()
        var pass = checkpoint.getString("pass", null)
        if (pass != null) {
            liveData.value = pass
        }
    }

    fun writeToCache(pass: String) {
        val checkpoint = ForUsApplication.context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = checkpoint.edit()
        AppContext.userId = pass
        if (pass.equals("tunhi") || pass.equals("nhitu")) {
            AppContext.roomId = "999999"
        } else if (pass.equals("test1") || pass.equals("test2")) {
            AppContext.roomId = "0"
        } else {
            AppContext.roomId = Random.nextInt(1, 50).toString()
        }
        editor.putString("pass", pass).apply()
    }

    fun login(pass: String) {
        val rootRef = FirebaseDatabase.getInstance().reference.child("users")
        val query: Query = rootRef.orderByChild("token").equalTo(pass)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    liveError.value = "Invalid user"
                } else {
                    liveData.value = pass
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                liveError.value = databaseError.message
            }
        }
        query.addValueEventListener(eventListener)
    }
}
