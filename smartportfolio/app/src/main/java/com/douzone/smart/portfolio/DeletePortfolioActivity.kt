package com.douzone.smart.portfolio

import android.animation.ValueAnimator
import android.app.Activity
import android.content.DialogInterface
import android.graphics.BitmapFactory
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
import com.douzone.smart.portfolio.data.*
import com.douzone.smart.portfolio.databinding.ActivityDeleteBinding
import com.douzone.smart.portfolio.db.MessengerPortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.CardPortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.TimelinePortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.UserDatabaseHelper
import java.lang.Exception
import java.util.ArrayList
import kotlin.random.Random

class DeletePortfolioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteBinding

    private var cardPortfolio = ArrayList<Card>()
    private var timelinePortfolio = ArrayList<Timeline>()
    private var messengerPortfolio = ArrayList<Messenger>()

    private val cardPortfolioAdapter = CardPortfolioAdapter(this@DeletePortfolioActivity, cardPortfolio)
    private val timelinePortfolioAdapter = TimelinePortfolioAdapter(this@DeletePortfolioActivity, timelinePortfolio)
    private val messengerPortfolioAdapter = MessengerPortfolioAdapter(this@DeletePortfolioActivity, messengerPortfolio)

    private lateinit var progressDialog: AppCompatDialog

    private var loadingTime = Random.nextLong(1000, 3000)

    private var userImageArray = ByteArray(1)
    var noImage = true

    var noDescription = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()

        startLoading()

        initView()
        initToolbar()

        // 유저 이름에 따른 데이터 가져옴
        initUserData()

        initEvent()
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
                val dbHelper = CardPortfolioDatabaseHelper(this@DeletePortfolioActivity)
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

        val dbHelper = UserDatabaseHelper(this@DeletePortfolioActivity)
        val userData = dbHelper.selecetUser(userName!!)
        if(userData != null) {
            if(userData.profileImage != null && userData.profileImage!!.isNotEmpty()) {
                try {
                    val bitmap = BitmapFactory.decodeByteArray(userData.profileImage, 0, userData.profileImage!!.size)
                    bitmap?.let {
                        binding.ivUserImage.setImageBitmap(bitmap)
                    }
                    userImageArray = userData.profileImage!!
                    noImage = false
                }catch (e: Exception) {

                }
            }
            // 유저 한 줄 소개 있으면 가져옴
            if(userData.userTitle.isNotEmpty()) {
                binding.tvUserDescription.text = userData.userTitle
                noDescription = false
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
        title = getString(R.string.activityMain_tab_item_delete)
    }

    fun initEvent() {
        binding.cardViewUser.setOnClickListener {
            // 유저 이미지 제거 할 수 있게 함, 만약 없으면 띄우지 않음
            if(noImage) {
                Toast.makeText(this@DeletePortfolioActivity, getString(R.string.toast_empty_user_image), Toast.LENGTH_SHORT).show()
            }
            else {
                AlertDialog.Builder(this).run {
                    setTitle(getString(R.string.dialog_title_delete_user_image))
                    setMessage(getString(R.string.dialog_message_delete_user_image))
                    setPositiveButton(getString(R.string.dialog_button_yes)) {dialogInterface, _ ->
                        dialogInterface.dismiss()
                        binding.ivUserImage.setImageResource(R.drawable.ic_listview_user)
                        userImageArray = ByteArray(1)
                        noImage = true
                    }
                    setNegativeButton(getString(R.string.dialog_button_cancel)) {dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    show()
                }
            }
        }

        binding.ivDeleteDescription.setOnClickListener {
            // 한줄 소개 제거 할 수 있게 함, 만약 없으면 띄우지 않음
            if(noDescription) {
                Toast.makeText(this@DeletePortfolioActivity, getString(R.string.toast_empty_user_description), Toast.LENGTH_SHORT).show()
            }else {
                AlertDialog.Builder(this).run {
                    setTitle(getString(R.string.dialog_title_delete_user_description))
                    setMessage(getString(R.string.dialog_message_delete_user_description))
                    setPositiveButton(getString(R.string.dialog_button_yes)) {dialogInterface, _ ->
                        dialogInterface.dismiss()
                        noDescription = true
                        binding.tvUserDescription.text = getString(R.string.addDelete_userDescription)
                    }
                    setNegativeButton(getString(R.string.dialog_button_cancel)) {dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    show()
                }
            }
        }
    }

    fun deleteUser() {
        val userName = intent.getStringExtra("name")

        when(intent.getIntExtra("viewType", 1)) {
            ViewType.CARDVIEW -> {
                val dbHelper = CardPortfolioDatabaseHelper(this@DeletePortfolioActivity)
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

        // 유저 업데이트도 진행
        val userDB = UserDatabaseHelper(this@DeletePortfolioActivity)
        val userData = userDB.selecetUser(userName!!)
        val userDescription = if(noDescription) "" else binding.tvUserDescription.text.toString()
        userDB.updateData(User(userData!!.name, userDescription, userImageArray, intent.getIntExtra("viewType", 1)))

        when(intent.getIntExtra("viewType", 1)) {
            ViewType.CARDVIEW -> {
                val dbHelper = CardPortfolioDatabaseHelper(this@DeletePortfolioActivity)
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
                setTitle(getString(R.string.dialog_title_delete_user))
                setMessage(intent.getStringExtra("name") + getString(R.string.dialog_message_delete_user))
                setPositiveButton(getString(R.string.dialog_button_yes), DialogInterface.OnClickListener { dialogInterface, _ ->
                    deleteUser()
                    setResult(Activity.RESULT_OK, intent)
                    Toast.makeText(this@DeletePortfolioActivity, getString(R.string.toast_deleted_user), Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                    finish()
                    true
                })
                setNegativeButton(getString(R.string.dialog_button_no), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })
                show()
            }
            true
        }
        R.id.menu_return -> {
            AlertDialog.Builder(this).run {
                setTitle(getString(R.string.dialog_title_save))
                setMessage(getString(R.string.dialog_message_save_changed))
                setPositiveButton(getString(R.string.dialog_button_yes), DialogInterface.OnClickListener { dialogInterface, _ ->
                    deletePortfolio()
                    setResult(Activity.RESULT_OK, intent)
                    Toast.makeText(this@DeletePortfolioActivity, getString(R.string.toast_saved_changing), Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                    finish()
                    true
                })
                setNegativeButton(getString(R.string.dialog_button_no), DialogInterface.OnClickListener { dialogInterface, _ ->
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
            setTitle(getString(R.string.dialog_title_cancel_delete_portfolio))
            setMessage(getString(R.string.dialog_message_cancel_delete_portfolio))
            setPositiveButton(getString(R.string.dialog_button_yes), DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
                setResult(Activity.RESULT_CANCELED, intent)
                super.onBackPressed()
            })
            setNegativeButton(getString(R.string.dialog_button_no), DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
            })
            show()
        }
    }
}