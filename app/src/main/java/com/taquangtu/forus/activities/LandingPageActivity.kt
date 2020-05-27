package com.taquangtu.forus.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taquangtu.forus.R
import com.taquangtu.forus.fragments.LandingPageFragment

class LandingPageActivity : AppCompatActivity() {
    private val mFragment = LandingPageFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_page_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, mFragment).commitNow()
        }
    }
}