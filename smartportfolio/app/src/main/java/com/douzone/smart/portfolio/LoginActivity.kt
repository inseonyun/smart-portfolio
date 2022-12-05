package com.douzone.smart.portfolio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
import com.douzone.smart.portfolio.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    // back button event
    var backTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        logoFadeIn()
        loginButtonEvent()
    }

    private fun View.hideAndShow(time : Long = 2000) {
        visibility = View.INVISIBLE
        startAnimation(
            AlphaAnimation(0.0f, 1.0f).apply {
                duration = time
                fillAfter = true
            }
        )
    }

    private fun logoFadeIn() {
        binding.ivAppName.hideAndShow()
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - backTime > 3000) {
            Toast.makeText(applicationContext, R.string.back_toast_message, Toast.LENGTH_SHORT).show()
            backTime = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }

    fun loginButtonEvent() {
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}