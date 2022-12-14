package com.douzone.smart.portfolio.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.douzone.smart.portfolio.MainActivity
import com.douzone.smart.portfolio.data.User
import com.douzone.smart.portfolio.databinding.ListviewMenuUserBinding

class MenuUserListViewAdapter (val context: Context, var items: ArrayList<User>) : BaseAdapter() {

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding = ListviewMenuUserBinding.inflate(LayoutInflater.from(context))

        binding.tvUserName.text = items[p0].name

        binding.tvUserName.setOnClickListener {
            (context as MainActivity).changeViewPagerPage(p0 + 1)
        }

        return binding.root
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }
}