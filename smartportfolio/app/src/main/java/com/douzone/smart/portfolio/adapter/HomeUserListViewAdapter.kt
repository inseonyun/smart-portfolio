package com.douzone.smart.portfolio.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.douzone.smart.portfolio.MainActivity
import com.douzone.smart.portfolio.data.user
import com.douzone.smart.portfolio.databinding.ListviewHomeUserBinding

class HomeUserListViewAdapter(val context: Context, var items: ArrayList<user>) : BaseAdapter() {

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding = ListviewHomeUserBinding.inflate(LayoutInflater.from(context))

        binding.tvUserName.setText(items[p0].name)
        binding.tvUserTitle.setText(items[p0].title)

        binding.cvUser.setOnClickListener {
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