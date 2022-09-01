package com.douzone.smart.portfolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.douzone.smart.portfolio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(getLayoutInflater())
        val view = binding.root
        setContentView(view)

        binding.tvTf.text = "test"
    }
}