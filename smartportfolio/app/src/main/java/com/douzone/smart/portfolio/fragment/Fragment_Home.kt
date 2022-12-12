package com.douzone.smart.portfolio.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.viewpager2.widget.ViewPager2
import com.douzone.smart.portfolio.MainActivity
import com.douzone.smart.portfolio.R
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

    private var pagerAdapter = ViewPagerAdapter(pageList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 유저 데이터 가져옴
        getSQLData()

        // 어댑터와 뷰페이지 연결
        initPages()

        // 드로어 메뉴에 유저 리스트 갱신
        initMenuList()

        // 뷰페이저 이벤트 리스너
        initEvent()

        return binding.root
    }

    fun getSQLData() {
        val dbHelper = UserDatabaseHelper(requireContext() as MainActivity)
        userList = dbHelper.selectData()
    }

    fun initUserData() {
        val dbHelper = UserDatabaseHelper(requireContext() as MainActivity)
        userList.clear()
        userList = dbHelper.selectData()
    }

    fun initMenuList() {
        (activity as MainActivity).initMenuListUserData(userList)
    }

    fun initPages() {
        pageList.clear()
        pageList.add(0, User("home", "", null,-1))
        userList.forEach { pageList.add(it) }
        pagerAdapter.notifyDataSetChanged()
        binding.viewpager.adapter = pagerAdapter
        binding.indicatorMain.setViewPager2(binding.viewpager)
    }

    fun initEvent() {
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if(position == 0) {
                    // 툴바 레이아웃 안 보이게 함
                    (context as MainActivity).setVisibleToolbarLayout(View.GONE)
                }else {
                    // 해당 포지션의 유저 이름, 이미지 보이게 함
                    // 이미지 넣음
                    if(pageList[position].profileImage != null && pageList[position].profileImage!!.isNotEmpty()) {
                        try {
                            val bitmap = BitmapFactory.decodeByteArray(pageList[position].profileImage, 0, pageList[position].profileImage!!.size)
                            bitmap?.let {
                                (context as MainActivity).setToolbarUserImage(bitmap)
                            }
                        }catch (e: Exception) {

                        }
                    } else {
                        (context as MainActivity).setToolbarUserImage(ContextCompat.getDrawable((context as MainActivity), R.drawable.ic_listview_user)!!.toBitmap())
                    }

                    (context as MainActivity).setToolbarUserName(pageList[position].name)
                    (context as MainActivity).setVisibleToolbarLayout(View.VISIBLE)
                }
            }
        })
    }
}