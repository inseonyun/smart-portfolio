package com.douzone.smart.portfolio.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import com.douzone.smart.portfolio.BrowserActivity
import com.douzone.smart.portfolio.R
import com.douzone.smart.portfolio.controller.BrowserController
import com.douzone.smart.portfolio.data.Card
import com.douzone.smart.portfolio.data.DefaultImage
import com.douzone.smart.portfolio.databinding.ListviewPortfolioCardBinding

class CardPortfolioAdapter(val context: Context, var items: ArrayList<Card>): BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding = ListviewPortfolioCardBinding.inflate(LayoutInflater.from(context))

        binding.tvTitle.text = items[p0].title
        binding.tvContents.text = items[p0].contents

        // defaultImage 여부에 따라 기본 이미지 보이게 할지 말지 정함
        // 이미지 넣음
        if(items[p0].image != null && items[p0].image!!.isNotEmpty()) {
            try {
                val bitmap = BitmapFactory.decodeByteArray(items[p0].image, 0, items[p0].image!!.size)
                bitmap?.let {
                    binding.ivMain.setImageBitmap(bitmap)
                }
            }catch (e: Exception) {

            }
        } else {
            if(items[p0].defaultImage == DefaultImage.GONE) {
                binding.cvImage.visibility = View.GONE
            }
        }

        if(items[p0].url.isNullOrEmpty()) {
            binding.tvLink.visibility = View.GONE
        } else {
            binding.tvLink.visibility = View.VISIBLE
            binding.tvLink.setOnClickListener {
                // 크롬이 깔려있다면 다음 다이얼로그 띄움
                if(BrowserController.checkToInstallChrome(context, context.getString(R.string.package_chrome))) {
                    // url 내부 브라우저 혹은 외부 Chrome 띄움
                    AlertDialog.Builder(context).run {
                        setTitle(context.getString(R.string.dialog_title_open_link))
                        setMessage(context.getString(R.string.dialog_message_open_link))
                        setPositiveButton(context.getString(R.string.dialog_button_chrome), DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            
                            // 크롬으로 띄움
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(items[p0].url))
//                            val intent = Intent(context, BrowserActivity::class.java)
//                            intent.putExtra("url", items[p0].url)
                            intent.setPackage(context.getString(R.string.package_chrome))
                            context.startActivity(intent)
                        })
                        setNegativeButton(context.getString(R.string.dialog_button_internal_browser), DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()

                            val intent = Intent(context, BrowserActivity::class.java)
                            intent.putExtra("url", items[p0].url)
                            context.startActivity(intent)
                        })
                        show()
                    }
                }else {
                    // 안 깔려있다면 바로 내부 브라우저로 링크 띄움
                    val intent = Intent(context, BrowserActivity::class.java)
                    intent.putExtra("url", items[p0].url)
                    context.startActivity(intent)
                }
            }
        }

        // 불려온 context가 delete라면 삭제 이벤트 처리
        if(context.toString().contains("DeletePortfolio")) {
            binding.cardView.setOnClickListener {
                AlertDialog.Builder(context).run {
                    setTitle(context.getString(R.string.dialog_title_delete_portfolio))
                    setMessage(context.getString(R.string.dialog_message_delete_portfolio))
                    setPositiveButton(context.getString(R.string.dialog_button_yes), DialogInterface.OnClickListener { dialogInterface, _ ->
                        // 사용자가 삭제 취소를 할 수 있도록 DB에 바로 제거하지는 않음
                        this@CardPortfolioAdapter.items.removeAt(p0)
                        this@CardPortfolioAdapter.notifyDataSetChanged()
                        dialogInterface.dismiss()
                    })
                    setNegativeButton(context.getString(R.string.dialog_button_no), DialogInterface.OnClickListener { dialogInterface, _ ->
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