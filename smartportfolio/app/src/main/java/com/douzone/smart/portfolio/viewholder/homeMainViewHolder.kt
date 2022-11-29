package com.douzone.smart.portfolio.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.douzone.smart.portfolio.adapter.HomeUserListViewAdapter
import com.douzone.smart.portfolio.data.User
import com.douzone.smart.portfolio.databinding.ViewPagerMainBinding
import com.douzone.smart.portfolio.db.UserDatabaseHelper

class homeMainViewHolder(private val binding: ViewPagerMainBinding): RecyclerView.ViewHolder(binding.root) {

    fun onBind() {
        val db_helper = UserDatabaseHelper(binding.root.context)
        val user_list: ArrayList<User> = db_helper.selectData()
        val list_adapter = HomeUserListViewAdapter(binding.root.context, user_list)
        binding.listviewUser.adapter = list_adapter
    }

}