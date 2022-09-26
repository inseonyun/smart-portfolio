package com.douzone.smart.portfolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.douzone.smart.portfolio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // back button event
    var backTime = 0L

    var fragment_home: Fragment_Home ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(getLayoutInflater())
        val view = binding.root
        setContentView(view)

        fragment_home = Fragment_Home()

        // frame layout
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_main, fragment_home!!)
            .commit()

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