package com.douzone.smart.portfolio

import android.animation.ValueAnimator
import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import com.airbnb.lottie.LottieAnimationView
import com.douzone.smart.portfolio.adapter.CardPortfolioAdapter
import com.douzone.smart.portfolio.adapter.MessengerPortfolioAdapter
import com.douzone.smart.portfolio.adapter.TimelinePortfolioAdapter
import com.douzone.smart.portfolio.data.Messenger
import com.douzone.smart.portfolio.data.Portfolio
import com.douzone.smart.portfolio.data.Timeline
import com.douzone.smart.portfolio.data.ViewType
import com.douzone.smart.portfolio.databinding.ActivityDeleteBinding
import com.douzone.smart.portfolio.db.MessengerPortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.PortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.TimelinePortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.UserDatabaseHelper
import java.util.ArrayList
import kotlin.random.Random

class DeletePortfolioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteBinding

    private var cardPortfolio = ArrayList<Portfolio>()
    private var timelinePortfolio = ArrayList<Timeline>()
    private var messengerPortfolio = ArrayList<Messenger>()

    private val cardPortfolioAdapter = CardPortfolioAdapter(this@DeletePortfolioActivity, cardPortfolio)
    private val timelinePortfolioAdapter = TimelinePortfolioAdapter(this@DeletePortfolioActivity, timelinePortfolio)
    private val messengerPortfolioAdapter = MessengerPortfolioAdapter(this@DeletePortfolioActivity, messengerPortfolio)

    private lateinit var progressDialog: AppCompatDialog

    private var loadingTime = Random.nextLong(1000, 3000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()

        startLoading()

        initView()
        initToolbar()

        // 유저 이름에 따른 데이터 가져옴
        initUserData()

    }

    fun startLoading() {
        progressDialog = AppCompatDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setContentView(R.layout.loading_login)
        progressDialog.show()
        var loading_lottie = progressDialog.findViewById<LottieAnimationView>(R.id.loading_lottie)
        loading_lottie?.playAnimation()
        loading_lottie?.repeatCount = ValueAnimator.INFINITE

        Handler().postDelayed({
            progressDialog.dismiss()
        }, loadingTime)
    }

    fun initUserData() {
        val userName = intent.getStringExtra("name")

        when(intent.getIntExtra("viewType", 1)) {
            ViewType.CARDVIEW -> {
                val dbHelper = PortfolioDatabaseHelper(this@DeletePortfolioActivity)
                dbHelper.selectData(userName!!).forEach { cardPortfolio.add(it) }
                initAdapter()
            }
            ViewType.MESSENGER -> {
                val dbHelper = MessengerPortfolioDatabaseHelper(this@DeletePortfolioActivity)
                dbHelper.selectData(userName!!).forEach { messengerPortfolio.add(it) }
                initAdapter()
            }
            ViewType.TIMELINE -> {
                val dbHelper = TimelinePortfolioDatabaseHelper(this@DeletePortfolioActivity)
                dbHelper.selectData(userName!!).forEach { timelinePortfolio.add(it) }
                initAdapter()
            }
        }
    }

    fun initAdapter() {
        cardPortfolioAdapter.notifyDataSetChanged()
        messengerPortfolioAdapter.notifyDataSetChanged()
        timelinePortfolioAdapter.notifyDataSetChanged()
    }

    fun initBinding() {
        binding = ActivityDeleteBinding.inflate(layoutInflater)
    }

    fun initView() {
        setContentView(binding.root)

        binding.tvUserName.text = intent.getStringExtra("name")

        when(intent.getIntExtra("viewType", 1)) {
            ViewType.CARDVIEW -> binding.lvPortfolio.adapter = cardPortfolioAdapter
            ViewType.TIMELINE -> binding.lvPortfolio.adapter = timelinePortfolioAdapter
            ViewType.MESSENGER -> binding.lvPortfolio.adapter = messengerPortfolioAdapter
        }
    }

    fun initToolbar() {
        title = "포트폴리오 삭제"
    }

    fun deleteUser() {
        val userName = intent.getStringExtra("name")

        when(intent.getIntExtra("viewType", 1)) {
            ViewType.CARDVIEW -> {
                val dbHelper = PortfolioDatabaseHelper(this@DeletePortfolioActivity)
                dbHelper.deleteData(userName!!)
            }
            ViewType.MESSENGER -> {
                val dbHelper = MessengerPortfolioDatabaseHelper(this@DeletePortfolioActivity)
                dbHelper.deleteData(userName!!)
            }
            ViewType.TIMELINE -> {
                val dbHelper = TimelinePortfolioDatabaseHelper(this@DeletePortfolioActivity)
                dbHelper.deleteData(userName!!)
            }
        }
        val userDBHelper = UserDatabaseHelper(this@DeletePortfolioActivity)
        userDBHelper.deleteData(userName!!)
    }

    fun deletePortfolio() {
        val userName = intent.getStringExtra("name")

        when(intent.getIntExtra("viewType", 1)) {
            ViewType.CARDVIEW -> {
                val dbHelper = PortfolioDatabaseHelper(this@DeletePortfolioActivity)
                // 다 제거하고 현재 list의 내용 넣음
                dbHelper.deleteData(userName!!)
                cardPortfolioAdapter.items.forEach { dbHelper.insertData(it) }
            }
            ViewType.MESSENGER -> {
                val dbHelper = MessengerPortfolioDatabaseHelper(this@DeletePortfolioActivity)
                // 다 제거하고 현재 list의 내용 넣음
                dbHelper.deleteData(userName!!)
                messengerPortfolioAdapter.items.forEach { dbHelper.insertData(it) }
            }
            ViewType.TIMELINE -> {
                val dbHelper = TimelinePortfolioDatabaseHelper(this@DeletePortfolioActivity)
                // 다 제거하고 현재 list의 내용 넣음
                dbHelper.deleteData(userName!!)
                timelinePortfolioAdapter.items.forEach { dbHelper.insertData(it) }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.menu_delete -> {
            AlertDialog.Builder(this).run {
                setTitle("유저 삭제")
                setMessage("${intent.getStringExtra("name")}의 모든 데이터가 삭제됩니다. \r\n삭제하시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialogInterface, _ ->
                    deleteUser()
                    setResult(Activity.RESULT_OK, intent)
                    dialogInterface.dismiss()
                    finish()
                    true
                })
                setNegativeButton("아니오", DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })
                show()
            }
            true
        }
        R.id.menu_return -> {
            AlertDialog.Builder(this).run {
                setTitle("저장")
                setMessage("변경사항을 저장하시겠습니까?")
                setPositiveButton("예", DialogInterface.OnClickListener { dialogInterface, _ ->
                    deletePortfolio()
                    setResult(Activity.RESULT_OK, intent)
                    dialogInterface.dismiss()
                    finish()
                    true
                })
                setNegativeButton("아니오", DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })
                show()
            }
            true
        }
        else -> true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_user_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).run {
            setTitle("종료")
            setMessage("포트폴리오 수정을 취소하시겠습니까?")
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