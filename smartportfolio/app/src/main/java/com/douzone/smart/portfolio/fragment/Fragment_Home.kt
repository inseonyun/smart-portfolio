package com.douzone.smart.portfolio.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.douzone.smart.portfolio.databinding.FragmentHomeBinding

class Fragment_Home : Fragment() {
    // ViewBinding
    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root



        return view
    }

    fun changeViewPagerPage(idx: Int) {
        binding.viewpager.currentItem = idx
    }
}