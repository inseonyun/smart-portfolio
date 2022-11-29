package com.douzone.smart.portfolio.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.douzone.smart.portfolio.data.User
import com.douzone.smart.portfolio.databinding.ViewPagerCardBinding

class portfolioCardViewHolder(private val binding: ViewPagerCardBinding): RecyclerView.ViewHolder(binding.root) {
    var data: User? = null

    fun onBind(data: User) {
        this.data = data

        binding.tvTitle.setText(data.name)
        //binding.tvContents.setText(data.contents)

//        if(!data.image.isNullOrEmpty()) {
//            // 이미지 설정
//        }
//        if(!data.url.isNullOrEmpty()) {
//            // 하이퍼링크 설정
//        }
    }
}