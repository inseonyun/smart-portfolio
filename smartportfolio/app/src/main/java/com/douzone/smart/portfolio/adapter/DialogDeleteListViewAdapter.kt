package com.douzone.smart.portfolio.adapter

import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import com.douzone.smart.portfolio.MainActivity
import com.douzone.smart.portfolio.R
import com.douzone.smart.portfolio.`interface`.DialogUserOnItemClick
import com.douzone.smart.portfolio.data.AddDelete
import com.douzone.smart.portfolio.data.User
import com.douzone.smart.portfolio.databinding.ListviewDialogUserBinding
import java.lang.Exception

class DialogDeleteListViewAdapter(val context: Context, var items: ArrayList<User>, private val listener: DialogUserOnItemClick) : BaseAdapter() {

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding = ListviewDialogUserBinding.inflate(LayoutInflater.from(context))

        binding.tvUserName.text = items[p0].name

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
            (context as MainActivity)
            // 삭제 액티비티로 이동
            AlertDialog.Builder(context).run {
                setTitle(context.getString(R.string.dialog_title_delete_portfolio))
                setMessage(items[p0].name + context.getString(R.string.dialog_message_delete_user_portfolio))
                setPositiveButton(context.getString(R.string.dialog_button_yes), DialogInterface.OnClickListener { dialogInterface, _ ->
                    listener.onClick(items[p0].name, items[p0].viewType, AddDelete.DELETE)
                    dialogInterface.dismiss()
                })
                setNegativeButton(context.getString(R.string.dialog_button_no), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })
                show()
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