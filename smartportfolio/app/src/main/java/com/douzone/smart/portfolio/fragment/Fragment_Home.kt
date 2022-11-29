package com.douzone.smart.portfolio.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.douzone.smart.portfolio.MainActivity
import com.douzone.smart.portfolio.adapter.ViewPagerAdapter
import com.douzone.smart.portfolio.data.User
import com.douzone.smart.portfolio.databinding.FragmentHomeBinding
import com.douzone.smart.portfolio.db.UserDatabaseHelper

class Fragment_Home : Fragment() {
    // ViewBinding
    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!

    // page를 나타내는 리스트
    private var pageList = ArrayList<User>()

    // 유저를 나타내는 리스트
    private var userList = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        getSQLData()

        (activity as MainActivity).initMenuListUserData(userList)

        return binding.root
    }

    fun getSQLData() {
        val dbHelper = UserDatabaseHelper(requireContext() as MainActivity)
        userList = dbHelper.selectData()

        pageList.add(0, User("home", -1))
        userList.forEach { pageList.add(it) }

        // 어댑터 생성
        val pagerAdapter = ViewPagerAdapter(pageList)

//        deprecated
//        fragment_home.binding.viewpager.adapter = pagerAdapter
//        binding.indicatorMain.setViewPager2(fragment_home.binding.viewpager)

        // 어댑터와 뷰페이지 연결
        binding.viewpager.adapter = pagerAdapter
        binding.indicatorMain.setViewPager2(binding.viewpager)

    }
}