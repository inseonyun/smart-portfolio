package com.douzone.smart.portfolio

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.douzone.smart.portfolio.adapter.CardPortfolioAdapter
import com.douzone.smart.portfolio.adapter.MessengerPortfolioAdapter
import com.douzone.smart.portfolio.adapter.TimelinePortfolioAdapter
import com.douzone.smart.portfolio.data.*
import com.douzone.smart.portfolio.databinding.ActivityAddPortfolioBinding
import com.douzone.smart.portfolio.databinding.DialogAddPortfolioCardBinding
import com.douzone.smart.portfolio.databinding.DialogAddPortfolioMessengerBinding
import com.douzone.smart.portfolio.databinding.DialogAddPortfolioTimelineBinding
import com.douzone.smart.portfolio.db.MessengerPortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.PortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.TimelinePortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.UserDatabaseHelper
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.Exception
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

    private val cardPortfolio = ArrayList<Portfolio>()
    private val timelinePortfolio = ArrayList<Timeline>()
    private val messengerPortfolio = ArrayList<Messenger>()

    private val cardPortfolioAdapter = CardPortfolioAdapter(this@AddPortfolioActivity, cardPortfolio)
    private val timelinePortfolioAdapter = TimelinePortfolioAdapter(this@AddPortfolioActivity, timelinePortfolio)
    private val messengerPortfolioAdapter = MessengerPortfolioAdapter(this@AddPortfolioActivity, messengerPortfolio)

    private var checkUser = false

    private var changedUserImage: ByteArray ?= null

    private val getUserProfileImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if(activityResult.resultCode == RESULT_OK && activityResult.data != null) {
            setBitmapImage(activityResult.data!!.data!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initView()
        initToolbar()
        initUser()
        initData()
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

        when(intent.getIntExtra("viewType", 1)) {
            ViewType.CARDVIEW -> binding.lvPortfolio.adapter = cardPortfolioAdapter
            ViewType.TIMELINE -> binding.lvPortfolio.adapter = timelinePortfolioAdapter
            ViewType.MESSENGER -> binding.lvPortfolio.adapter = messengerPortfolioAdapter
        }
    }

    fun initToolbar() {
        title = "포트폴리오 추가"
    }

    fun initUser() {
        binding.tvUserName.text = intent.getStringExtra("name")
    }

    fun initData() {
        val userName = intent.getStringExtra("name")
        val viewType = intent.getIntExtra("viewType", 1)
        
        // 해당 유저가 DB에 있는지 확인
        val dbHelper = UserDatabaseHelper(this@AddPortfolioActivity)
        checkUser = dbHelper.checkUser(userName!!)
        if(checkUser) {
            // 있으면 해당 유저의 포트폴리오 정보 가져옴
            when(viewType) {
                ViewType.CARDVIEW -> {
                    val cardDB = PortfolioDatabaseHelper(this@AddPortfolioActivity)
                    cardDB.selectData(userName).forEach { cardPortfolio.add(it) }
                }
                ViewType.TIMELINE -> {
                    val timelineDB = TimelinePortfolioDatabaseHelper(this@AddPortfolioActivity)
                    timelineDB.selectData(userName).forEach { timelinePortfolio.add(it) }
                }
                ViewType.MESSENGER -> {
                    val messengerDB = MessengerPortfolioDatabaseHelper(this@AddPortfolioActivity)
                    messengerDB.selectData(userName).forEach { messengerPortfolio.add(it) }
                }
            }
            // 있는 유저면 프로필 사진이 있는지 확인하고, 있다면 가져옴
            val userData = dbHelper.selecetUser(userName)
            if(userData != null) {
                if(userData.profileImage != null && userData.profileImage!!.isNotEmpty()) {
                    changedUserImage = userData.profileImage

                    try {
                        val bitmap = BitmapFactory.decodeByteArray(changedUserImage, 0, changedUserImage!!.size)
                        bitmap?.let {
                            binding.ivUserImage.setImageBitmap(bitmap)
                        }
                    }catch (e: Exception) {

                    }
                }
            }
        }
    }

    fun initDialogEvent() {
        timelineDialogBinding.tvDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this@AddPortfolioActivity, {_, year, month, day ->
                timelineDialogBinding.tvDate.text = "$year-${month + 1}-$day"
            }, year, month, day)
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }

    fun initEvent() {
        binding.btnAdd.setOnClickListener {
            initDialogEvent()
            AlertDialog.Builder(this).run {
                setTitle("포트폴리오 추가")

                when(intent.getIntExtra("viewType", 1)) {
                    ViewType.CARDVIEW -> {
                        setView(cardDialogBinding.root)
                        setPositiveButton("추가", DialogInterface.OnClickListener { dialogInterface, _ ->
                            if(cardDialogBinding.etTitle.text.isNullOrEmpty() || cardDialogBinding.etContents.text.isNullOrEmpty())
                                Toast.makeText(this@AddPortfolioActivity, "입력 값이 비었습니다.", Toast.LENGTH_SHORT).show()
                            else {
                                // 이미지 구현 해야함
                                val rowCardPortfolio = Portfolio(0,
                                    intent.getStringExtra("name").toString(),
                                    cardDialogBinding.etTitle.text.toString(),
                                    cardDialogBinding.etContents.text.toString(),
                                    "",
                                    cardDialogBinding.etUrl.text.toString().trim()
                                )

                                cardPortfolio.add(rowCardPortfolio)
                                cardPortfolioAdapter.notifyDataSetChanged()

                                // 뷰 제거 후 다시 바인딩
                                if(cardDialogBinding.root.parent != null)
                                    (cardDialogBinding.root.parent as ViewGroup).removeView(cardDialogBinding.root)

                                cardDialogBinding = DialogAddPortfolioCardBinding.inflate(layoutInflater)
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
                                // 라디오 선택된 컬러값으로 circleColor 조정
                                val circleColor: Int = when(timelineDialogBinding.rbtCircleColor.checkedRadioButtonId) {
                                    timelineDialogBinding.rbtGreen.id -> CircleType.GREEN
                                    timelineDialogBinding.rbtOrange.id -> CircleType.ORANGE
                                    else -> CircleType.BLUE
                                }

                                // 이미지 구현 해야함
                                val rowTimelinePortfolio = Timeline(0,
                                    intent.getStringExtra("name").toString(),
                                    timelineDialogBinding.etTitle.text.toString(),
                                    timelineDialogBinding.etContents.text.toString(),
                                    timelineDialogBinding.tvDate.text.toString(),
                                    "",
                                    timelineDialogBinding.etUrl.text.toString().trim(),
                                    circleColor
                                )
                                timelinePortfolio.add(rowTimelinePortfolio)
                                timelinePortfolioAdapter.notifyDataSetChanged()

                                // 뷰 제거 후 다시 바인딩
                                if(timelineDialogBinding.root.parent != null)
                                    (timelineDialogBinding.root.parent as ViewGroup).removeView(timelineDialogBinding.root)
                                timelineDialogBinding = DialogAddPortfolioTimelineBinding.inflate(layoutInflater)
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
                                // 이미지 구현 해야함
                                val rowMessengerPortfolio = Messenger(0,
                                    intent.getStringExtra("name").toString(),
                                    messengerDialogBinding.etTitle.text.toString(),
                                    messengerDialogBinding.etContents.text.toString(),
                                    "",
                                    messengerDialogBinding.etUrl.text.toString().trim()
                                )
                                messengerPortfolio.add(rowMessengerPortfolio)
                                messengerPortfolioAdapter.notifyDataSetChanged()

                                // 뷰 제거 후 다시 바인딩
                                if(messengerDialogBinding.root.parent != null)
                                    (messengerDialogBinding.root.parent as ViewGroup).removeView(messengerDialogBinding.root)
                                messengerDialogBinding = DialogAddPortfolioMessengerBinding.inflate(layoutInflater)
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

        binding.cardViewUser.setOnClickListener {
            // 이미지 선택할 수 있게 함
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            getUserProfileImage.launch(intent)
        }
    }

    fun insertPortfolio() {
        val userName = intent.getStringExtra("name")
        val userViewType = intent.getIntExtra("viewType", 1)

        // add user
        val user = User(userName!!, changedUserImage, userViewType)
        val userDB = UserDatabaseHelper(this@AddPortfolioActivity)
        userDB.insertData(user)

        // add portfolio
        when(userViewType) {
            ViewType.CARDVIEW -> {
                val portfolioDB = PortfolioDatabaseHelper(this@AddPortfolioActivity)
                cardPortfolio.forEach {
                    portfolioDB.insertData(it)
                }
            }
            ViewType.TIMELINE -> {
                val portfolioDB = TimelinePortfolioDatabaseHelper(this@AddPortfolioActivity)
                timelinePortfolio.forEach {
                    portfolioDB.insertData(it)
                }
            }
            ViewType.MESSENGER -> {
                val portfolioDB = MessengerPortfolioDatabaseHelper(this@AddPortfolioActivity)
                messengerPortfolio.forEach {
                    portfolioDB.insertData(it)
                }
            }
        }
    }

    fun updatePortfolio() {
        val userName = intent.getStringExtra("name")
        val userViewType = intent.getIntExtra("viewType", 1)

        // update userProfileImage
        val user = User(userName!!, changedUserImage, userViewType)
        val userDB = UserDatabaseHelper(this@AddPortfolioActivity)
        userDB.updateData(user)

        // update portfolio
        when(userViewType) {
            ViewType.CARDVIEW -> {
                val portfolioDB = PortfolioDatabaseHelper(this@AddPortfolioActivity)
                
                // 이미 있는 데이터가 안 들어가도록 삭제 후 넣음
                portfolioDB.deleteData(userName!!)
                
                cardPortfolio.forEach {
                    portfolioDB.insertData(it)
                }
            }
            ViewType.TIMELINE -> {
                val portfolioDB = TimelinePortfolioDatabaseHelper(this@AddPortfolioActivity)

                // 이미 있는 데이터가 안 들어가도록 삭제 후 넣음
                portfolioDB.deleteData(userName!!)

                timelinePortfolio.forEach {
                    portfolioDB.insertData(it)
                }
            }
            ViewType.MESSENGER -> {
                val portfolioDB = MessengerPortfolioDatabaseHelper(this@AddPortfolioActivity)

                // 이미 있는 데이터가 안 들어가도록 삭제 후 넣음
                portfolioDB.deleteData(userName!!)

                messengerPortfolio.forEach {
                    portfolioDB.insertData(it)
                }
            }
        }
    }

    fun setBitmapImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream, null, null)
            val outputStream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            changedUserImage = outputStream.toByteArray()
            inputStream!!.close()
            outputStream.close()

            bitmap?.let {
                binding.ivUserImage.setImageBitmap(bitmap)
            }
        } catch(e: FileNotFoundException) {
            Log.e("UserImage ERROR", "${e.printStackTrace()}")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.menu_save -> {
            if(checkUser) {
                // 이미 있는 유저면 업데이트
                updatePortfolio()
            } else {
                // 없는 유저면 삽입
                insertPortfolio()
            }
            Toast.makeText(this@AddPortfolioActivity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK, intent)
            finish()
            true
        }
        else -> true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_portfolio_save, menu)
        return super.onCreateOptionsMenu(menu)
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