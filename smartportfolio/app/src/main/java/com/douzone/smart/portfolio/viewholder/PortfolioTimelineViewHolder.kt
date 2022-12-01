package com.douzone.smart.portfolio.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.douzone.smart.portfolio.MainActivity
import com.douzone.smart.portfolio.adapter.TimelinePortfolioAdapter
import com.douzone.smart.portfolio.data.User
import com.douzone.smart.portfolio.databinding.ViewPagerTimelineBinding
import com.douzone.smart.portfolio.db.TimelinePortfolioDatabaseHelper

class PortfolioTimelineViewHolder(private val binding: ViewPagerTimelineBinding): RecyclerView.ViewHolder(binding.root) {
    var data: User? = null

    fun onBind(data: User) {
        this.data = data

        // 여기서 해당 유저 이름을 갖고 db 탐색함
        val portfolioDbHelper = TimelinePortfolioDatabaseHelper(binding.root.context as MainActivity)
        val nowUserPortfolio = portfolioDbHelper.selectData(this.data!!.name)

        binding.lvPortfolio.adapter = TimelinePortfolioAdapter(binding.root.context as MainActivity, nowUserPortfolio)
    }
}