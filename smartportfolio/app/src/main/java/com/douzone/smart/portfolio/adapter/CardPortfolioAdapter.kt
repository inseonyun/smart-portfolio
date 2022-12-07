package com.douzone.smart.portfolio.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import com.douzone.smart.portfolio.data.Portfolio
import com.douzone.smart.portfolio.databinding.ListviewPortfolioCardBinding
import com.douzone.smart.portfolio.db.PortfolioDatabaseHelper

class CardPortfolioAdapter(val context: Context, var items: ArrayList<Portfolio>): BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding = ListviewPortfolioCardBinding.inflate(LayoutInflater.from(context))

        binding.tvTitle.text = items[p0].title
        binding.tvContents.text = items[p0].contents
        binding.tvLink.text = items[p0].url

        // 이미지 넣음
        //if(!items[p0].image.isNullOrEmpty())

        if(items[p0].url.isNullOrEmpty()) {
            binding.tvLink.visibility = View.GONE
        } else {
            binding.tvLink.text = items[p0].url
        }

        // 불려온 context가 delete라면 삭제 이벤트 처리
        if(context.toString().contains("DeletePortfolio")) {
            binding.cardView.setOnClickListener {
                AlertDialog.Builder(context).run {
                    setTitle("포트폴리오 삭제")
                    setMessage("해당 포트폴리오를 삭제할까요?")
                    setPositiveButton("예", DialogInterface.OnClickListener { dialogInterface, _ ->
                        val dbHelper = PortfolioDatabaseHelper(context)
                        dbHelper.deletePortfolio(items[p0].id)
                        this@CardPortfolioAdapter.items.removeAt(p0)
                        this@CardPortfolioAdapter.notifyDataSetChanged()
                        dialogInterface.dismiss()
                    })
                    setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    })
                    show()
                }
            }
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