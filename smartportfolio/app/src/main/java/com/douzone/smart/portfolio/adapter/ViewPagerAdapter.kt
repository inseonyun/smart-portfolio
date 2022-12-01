package com.douzone.smart.portfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.douzone.smart.portfolio.data.ViewType
import com.douzone.smart.portfolio.data.User
import com.douzone.smart.portfolio.databinding.ViewPagerCardBinding
import com.douzone.smart.portfolio.databinding.ViewPagerMainBinding
import com.douzone.smart.portfolio.databinding.ViewPagerTimelineBinding
import com.douzone.smart.portfolio.viewholder.PortfolioTimelineViewHolder
import com.douzone.smart.portfolio.viewholder.homeMainViewHolder
import com.douzone.smart.portfolio.viewholder.portfolioCardViewHolder

class ViewPagerAdapter(var items: ArrayList<User>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            ViewType.CARDVIEW -> {
                val binding =
                    ViewPagerCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return portfolioCardViewHolder(binding)
            }
            ViewType.TIMELINE -> {
                val binding =
                    ViewPagerTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return PortfolioTimelineViewHolder(binding)
            }
            ViewType.MESSENGER -> {
                TODO()
            }
            else -> {
                val binding =
                    ViewPagerMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return homeMainViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(items[position].viewType) {
            ViewType.CARDVIEW -> {
                (holder as portfolioCardViewHolder).onBind(items[position])
            }
            ViewType.TIMELINE -> {
                (holder as PortfolioTimelineViewHolder).onBind(items[position])
            }
            ViewType.MESSENGER -> {
                TODO()
            }
            else -> {
                (holder as homeMainViewHolder).onBind()
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }
}