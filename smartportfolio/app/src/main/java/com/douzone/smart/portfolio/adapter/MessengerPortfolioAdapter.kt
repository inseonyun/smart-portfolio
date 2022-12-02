package com.douzone.smart.portfolio.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.douzone.smart.portfolio.data.Messenger
import com.douzone.smart.portfolio.databinding.ListviewPortfolioMessengerBinding

class MessengerPortfolioAdapter(val context: Context, var items: ArrayList<Messenger>): BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding = ListviewPortfolioMessengerBinding.inflate(LayoutInflater.from(context))

        binding.tvTitle.text = items[p0].title
        binding.tvContents.text = items[p0].contents

        if(items[p0].url.isNullOrEmpty()) {
            binding.tvLink.visibility = View.GONE
        } else {
            binding.tvLink.text = items[p0].url
        }

        // 이미지 넣음
        //if(!items[p0].image.isNullOrEmpty())

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