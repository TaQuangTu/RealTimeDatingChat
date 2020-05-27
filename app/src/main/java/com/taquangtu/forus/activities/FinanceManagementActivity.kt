package com.taquangtu.forus.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taquangtu.forus.R
import com.taquangtu.forus.fragments.FinanceManagementFragment

class FinanceManagementActivity : AppCompatActivity() {
    private val mFragment = FinanceManagementFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, mFragment).commitNow()
        }
    }
}
