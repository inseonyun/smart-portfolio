package com.douzone.smart.portfolio.adapter

import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import com.douzone.smart.portfolio.R
import com.douzone.smart.portfolio.data.CircleType
import com.douzone.smart.portfolio.data.Timeline
import com.douzone.smart.portfolio.databinding.ListviewPortfolioTimelineBinding
import java.time.LocalDate

class TimelinePortfolioAdapter(val context: Context, var items: ArrayList<Timeline>): BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding = ListviewPortfolioTimelineBinding.inflate(LayoutInflater.from(context))

        val splitDate = items[p0].date.split("-")
        val date = LocalDate.of(splitDate[0].toInt(), splitDate[1].toInt(), splitDate[2].toInt())

        // 월 풀네임을 가져오기 때문에 앞 3글자만 파싱하고, 첫글짜만 대문자로 함
        val dateMonth = "${date.month}".substring(0, 3).lowercase()

        binding.tvDay.text = "${date.dayOfMonth}"
        binding.tvMonth.text = dateMonth.substring(0, 1).uppercase() + dateMonth.substring(1, 3)
        binding.tvYear.text = "${date.year}"

        binding.tvTitle.text = items[p0].title
        binding.tvContents.text = items[p0].contents

        // 이미지 넣음
        if(items[p0].image != null && items[p0].image!!.isNotEmpty()) {
            try {
                val bitmap = BitmapFactory.decodeByteArray(items[p0].image, 0, items[p0].image!!.size)
                bitmap?.let {
                    binding.ivMain.setImageBitmap(bitmap)
                }
            }catch (e: Exception) {

            }
        }

        // url이 없다면 표시하지 않음
        if(items[p0].url.isNullOrEmpty()) {
            binding.tvLink.visibility = View.GONE
        } else {
            binding.tvLink.text = items[p0].url
        }

        // 타임라인 서클 default : blue
        when(items[p0].circleColor) {
            CircleType.GREEN -> binding.ivTimelineCircle.setImageResource(R.drawable.img_green_timeline_circle)
            CircleType.ORANGE -> binding.ivTimelineCircle.setImageResource(R.drawable.img_orange_timeline_circle)
            else -> binding.ivTimelineCircle.setImageResource(R.drawable.img_blue_timeline_circle)
        }

        // 불려온 context가 delete라면 삭제 이벤트 처리
        if(context.toString().contains("DeletePortfolio")) {
            binding.cardView.setOnClickListener {
                AlertDialog.Builder(context).run {
                    setTitle("포트폴리오 삭제")
                    setMessage("해당 포트폴리오를 삭제할까요?")
                    setPositiveButton("예", DialogInterface.OnClickListener { dialogInterface, _ ->
                        // 사용자가 삭제 취소를 할 수 있도록 DB에 바로 제거하지는 않음
                        this@TimelinePortfolioAdapter.items.removeAt(p0)
                        this@TimelinePortfolioAdapter.notifyDataSetChanged()
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