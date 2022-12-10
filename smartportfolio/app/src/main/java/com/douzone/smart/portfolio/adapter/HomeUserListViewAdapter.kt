package com.douzone.smart.portfolio.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.douzone.smart.portfolio.MainActivity
import com.douzone.smart.portfolio.data.User
import com.douzone.smart.portfolio.databinding.ListviewHomeUserBinding
import java.lang.Exception

class HomeUserListViewAdapter(val context: Context, var items: ArrayList<User>) : BaseAdapter() {

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding = ListviewHomeUserBinding.inflate(LayoutInflater.from(context))

        binding.tvUserName.setText(items[p0].name)
        //binding.tvUserTitle.setText(items[p0].title)

        if(items[p0].profileImage != null && items[p0].profileImage!!.isNotEmpty()) {
            try {
                val bitmap = BitmapFactory.decodeByteArray(items[p0].profileImage, 0, items[p0].profileImage!!.size)
                bitmap?.let {
                    binding.ivUserImage.setImageBitmap(bitmap)
                }
            }catch (e: Exception) {

            }
        }

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