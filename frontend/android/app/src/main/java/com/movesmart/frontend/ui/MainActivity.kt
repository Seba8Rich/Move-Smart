package com.movesmart.frontend.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.movesmart.frontend.R
import com.movesmart.frontend.utils.TokenManagerSingleton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize TokenManager and RetrofitClient
        TokenManagerSingleton.init(applicationContext)
        com.movesmart.frontend.data.api.RetrofitClient.initTokenManager(applicationContext)

        // Check if user is logged in
        if (TokenManagerSingleton.getInstance().isLoggedIn()) {
            startActivity(Intent(this, DashboardActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}

