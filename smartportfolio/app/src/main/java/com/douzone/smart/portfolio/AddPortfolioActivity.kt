package com.douzone.smart.portfolio

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import com.douzone.smart.portfolio.data.*
import com.douzone.smart.portfolio.databinding.ActivityAddPortfolioBinding
import com.douzone.smart.portfolio.databinding.DialogAddPortfolioCardBinding
import com.douzone.smart.portfolio.databinding.DialogAddPortfolioMessengerBinding
import com.douzone.smart.portfolio.databinding.DialogAddPortfolioTimelineBinding
import com.douzone.smart.portfolio.db.MessengerPortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.PortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.TimelinePortfolioDatabaseHelper
import java.sql.Time
import java.util.*

class AddPortfolioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPortfolioBinding
    private lateinit var cardDialogBinding : DialogAddPortfolioCardBinding
    private lateinit var timelineDialogBinding : DialogAddPortfolioTimelineBinding
    private lateinit var messengerDialogBinding : DialogAddPortfolioMessengerBinding

    private val calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH)
    private var day = calendar.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initView()
        initToolbar()
        initUser()
        initEvent()
    }

    fun initBinding() {
        binding = ActivityAddPortfolioBinding.inflate(layoutInflater)

        cardDialogBinding = DialogAddPortfolioCardBinding.inflate(layoutInflater)
        timelineDialogBinding = DialogAddPortfolioTimelineBinding.inflate(layoutInflater)
        messengerDialogBinding = DialogAddPortfolioMessengerBinding.inflate(layoutInflater)
    }

    fun initView() {
        setContentView(binding.root)
    }

    fun initToolbar() {
        title = "포트폴리오 추가"
    }

    fun initUser() {
        binding.tvUserName.text = intent.getStringExtra("name")
    }

    fun initEvent() {
        timelineDialogBinding.tvDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this@AddPortfolioActivity, {_, year, month, day ->
                timelineDialogBinding.tvDate.text = "$year-${month + 1}-$day"
            }, year, month, day)
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        binding.btnAdd.setOnClickListener {
            AlertDialog.Builder(this).run {
                setTitle("포트폴리오 추가")

                when(intent.getIntExtra("viewType", 1)) {
                    ViewType.CARDVIEW -> {
                        setView(cardDialogBinding.root)
                        setPositiveButton("추가", DialogInterface.OnClickListener { dialogInterface, _ ->
                            if(cardDialogBinding.etTitle.text.isNullOrEmpty() || cardDialogBinding.etContents.text.isNullOrEmpty())
                                Toast.makeText(this@AddPortfolioActivity, "입력 값이 비었습니다.", Toast.LENGTH_SHORT).show()
                            else {
                                intent.putExtra("name", intent.getStringExtra("name"))
                                intent.putExtra("viewType", intent.getIntExtra("viewType", 1))

                                // 이미지 구현 해야함
                                val cardPortfolio = Portfolio(0,
                                    intent.getStringExtra("name").toString(),
                                    cardDialogBinding.etTitle.text.toString(),
                                    cardDialogBinding.etContents.text.toString(),
                                    "",
                                    cardDialogBinding.etUrl.text.toString().trim()
                                )
                                intent.putExtra("portfolio", cardPortfolio)

                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }

                            dialogInterface.dismiss()
                        })
                    }
                    ViewType.TIMELINE -> {
                        setView(timelineDialogBinding.root)
                        setPositiveButton("추가", DialogInterface.OnClickListener { dialogInterface, _ ->
                            if(timelineDialogBinding.etTitle.text.isNullOrEmpty() || timelineDialogBinding.etContents.text.isNullOrEmpty())
                                Toast.makeText(this@AddPortfolioActivity, "입력 값이 비었습니다.", Toast.LENGTH_SHORT).show()
                            else {
                                intent.putExtra("name", intent.getStringExtra("name"))
                                intent.putExtra("viewType", intent.getIntExtra("viewType", 1))

                                // 라디오 선택된 컬러값으로 circleColor 조정
                                val circleColor: Int = when(timelineDialogBinding.rbtCircleColor.checkedRadioButtonId) {
                                    timelineDialogBinding.rbtGreen.id -> CircleType.GREEN
                                    timelineDialogBinding.rbtOrange.id -> CircleType.ORANGE
                                    else -> CircleType.BLUE
                                }

                                // 이미지 구현 해야함
                                val timelinePortfolio = Timeline(0,
                                    intent.getStringExtra("name").toString(),
                                    timelineDialogBinding.etTitle.text.toString(),
                                    timelineDialogBinding.etContents.text.toString(),
                                    timelineDialogBinding.tvDate.text.toString(),
                                    "",
                                    timelineDialogBinding.etUrl.text.toString().trim(),
                                    circleColor
                                )
                                intent.putExtra("portfolio", timelinePortfolio)

                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }

                            dialogInterface.dismiss()
                        })
                    }
                    ViewType.MESSENGER -> {
                        setView(messengerDialogBinding.root)
                        setPositiveButton("추가", DialogInterface.OnClickListener { dialogInterface, _ ->
                            if(messengerDialogBinding.etTitle.text.isNullOrEmpty() || messengerDialogBinding.etContents.text.isNullOrEmpty())
                                Toast.makeText(this@AddPortfolioActivity, "입력 값이 비었습니다.", Toast.LENGTH_SHORT).show()
                            else {
                                intent.putExtra("name", intent.getStringExtra("name"))
                                intent.putExtra("viewType", intent.getIntExtra("viewType", 1))

                                // 이미지 구현 해야함
                                val messengerPortfolio = Messenger(0,
                                    intent.getStringExtra("name").toString(),
                                    messengerDialogBinding.etTitle.text.toString(),
                                    messengerDialogBinding.etContents.text.toString(),
                                    "",
                                    messengerDialogBinding.etUrl.text.toString().trim()
                                )
                                intent.putExtra("portfolio", messengerPortfolio)

                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }

                            dialogInterface.dismiss()
                        })
                    }
                }

                setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })
                show()
            }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).run {
            setTitle("종료")
            setMessage("포트폴리오 추가를 취소하시겠습니까?")
            setPositiveButton("예", DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
                setResult(Activity.RESULT_CANCELED, intent)
                super.onBackPressed()
            })
            setNegativeButton("아니오", DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
            })
            show()
        }
    }
}