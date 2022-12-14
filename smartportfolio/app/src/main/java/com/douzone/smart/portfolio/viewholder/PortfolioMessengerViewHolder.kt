package com.douzone.smart.portfolio.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.douzone.smart.portfolio.MainActivity
import com.douzone.smart.portfolio.adapter.MessengerPortfolioAdapter
import com.douzone.smart.portfolio.data.User
import com.douzone.smart.portfolio.databinding.ViewPagerMessengerBinding
import com.douzone.smart.portfolio.db.MessengerPortfolioDatabaseHelper

class PortfolioMessengerViewHolder(private val binding: ViewPagerMessengerBinding): RecyclerView.ViewHolder(binding.root) {
    var data: User? = null

    fun onBind(data: User) {
        this.data = data

        // 여기서 해당 유저 이름을 갖고 db 탐색함
        val portfolioDbHelper = MessengerPortfolioDatabaseHelper(binding.root.context as MainActivity)
        val nowUserPortfolio = portfolioDbHelper.selectData(this.data!!.name)

        binding.lvPortfolio.adapter = MessengerPortfolioAdapter(binding.root.context as MainActivity, nowUserPortfolio)

        //binding.tvTitle.setText(data.name)
        //binding.tvContents.setText(data.contents)

//        if(!data.image.isNullOrEmpty()) {
//            // 이미지 설정
//        }
//        if(!data.url.isNullOrEmpty()) {
//            // 하이퍼링크 설정
//        }
    }
}