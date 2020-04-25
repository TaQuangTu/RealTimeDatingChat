package com.taquangtu.forus.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taquangtu.forus.R
import com.taquangtu.forus.fragments.LoginFragment

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
        }
    }
}
