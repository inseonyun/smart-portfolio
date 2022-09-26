package com.douzone.smart.portfolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    // back button event
    var backTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - backTime > 3000) {
            Toast.makeText(applicationContext, R.string.back_toast_message, Toast.LENGTH_SHORT).show()
            backTime = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }
}