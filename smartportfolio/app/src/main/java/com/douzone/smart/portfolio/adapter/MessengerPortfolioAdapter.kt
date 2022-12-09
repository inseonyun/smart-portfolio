package com.douzone.smart.portfolio.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
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

        // 불려온 context가 delete라면 삭제 이벤트 처리
        if(context.toString().contains("DeletePortfolio")) {
            binding.layout.setOnClickListener {
                AlertDialog.Builder(context).run {
                    setTitle("포트폴리오 삭제")
                    setMessage("해당 포트폴리오를 삭제할까요?")
                    setPositiveButton("예", DialogInterface.OnClickListener { dialogInterface, _ ->
                        // 사용자가 삭제 취소를 할 수 있도록 DB에 바로 제거하지는 않음
                        this@MessengerPortfolioAdapter.items.removeAt(p0)
                        this@MessengerPortfolioAdapter.notifyDataSetChanged()
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