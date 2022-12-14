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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.douzone.smart.portfolio.adapter.CardPortfolioAdapter
import com.douzone.smart.portfolio.adapter.MessengerPortfolioAdapter
import com.douzone.smart.portfolio.adapter.TimelinePortfolioAdapter
import com.douzone.smart.portfolio.controller.RotateImageController
import com.douzone.smart.portfolio.data.*
import com.douzone.smart.portfolio.databinding.ActivityAddPortfolioBinding
import com.douzone.smart.portfolio.databinding.DialogAddPortfolioCardBinding
import com.douzone.smart.portfolio.databinding.DialogAddPortfolioMessengerBinding
import com.douzone.smart.portfolio.databinding.DialogAddPortfolioTimelineBinding
import com.douzone.smart.portfolio.databinding.DialogEditUserDescriptionBinding
import com.douzone.smart.portfolio.db.MessengerPortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.CardPortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.TimelinePortfolioDatabaseHelper
import com.douzone.smart.portfolio.db.UserDatabaseHelper
import java.io.ByteArrayOutputStream
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

    private val cardPortfolio = ArrayList<Card>()
    private val timelinePortfolio = ArrayList<Timeline>()
    private val messengerPortfolio = ArrayList<Messenger>()

    private val cardPortfolioAdapter = CardPortfolioAdapter(this@AddPortfolioActivity, cardPortfolio)
    private val timelinePortfolioAdapter = TimelinePortfolioAdapter(this@AddPortfolioActivity, timelinePortfolio)
    private val messengerPortfolioAdapter = MessengerPortfolioAdapter(this@AddPortfolioActivity, messengerPortfolio)

    private var checkUser = false
    private var checkUserDescription = false

    private var changedUserImage: ByteArray = byteArrayOf()
    private var postImage: ByteArray = byteArrayOf()

    private val getUserProfileImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if(activityResult.resultCode == RESULT_OK && activityResult.data != null) {
            setBitmapImage(activityResult.data!!.data!!)
        }
    }

    private val getPostImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if(activityResult.resultCode == RESULT_OK && activityResult.data != null) {
            val bitmap = RotateImageController(this@AddPortfolioActivity).rotateImage(activityResult.data!!.data!!)
            if(bitmap != null) {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                postImage = outputStream.toByteArray()
                outputStream.close()

                when(intent.getIntExtra("viewType", 1)) {
                    ViewType.CARDVIEW-> {
                        cardDialogBinding.tvImage.text = getString(R.string.dialog_add_portfolio_added_image)
                        cardDialogBinding.chkImage.visibility = View.GONE
                    }
                    ViewType.TIMELINE-> {
                        timelineDialogBinding.tvImage.text = getString(R.string.dialog_add_portfolio_added_image)
                        timelineDialogBinding.chkImage.visibility = View.GONE
                    }
                    ViewType.MESSENGER-> {
                        messengerDialogBinding.tvImage.text = getString(R.string.dialog_add_portfolio_added_image)
                        messengerDialogBinding.chkImage.visibility = View.GONE
                    }
                }
            }
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
        title = getString(R.string.activityMain_tab_item_add)
    }

    fun initUser() {
        binding.tvUserName.text = intent.getStringExtra("name")
        if(intent.getStringExtra("userTitle").isNullOrEmpty())
            binding.tvUserDescription.text = getString(R.string.listView_home_userList_title)
        else {
            binding.tvUserDescription.text = intent.getStringExtra("userTitle")
            checkUserDescription = true
        }
    }

    fun initData() {
        val userName = intent.getStringExtra("name")
        val viewType = intent.getIntExtra("viewType", 1)
        
        // ?????? ????????? DB??? ????????? ??????
        val dbHelper = UserDatabaseHelper(this@AddPortfolioActivity)
        checkUser = dbHelper.checkUser(userName!!)
        if(checkUser) {
            // ????????? ?????? ????????? ??????????????? ?????? ?????????
            when(viewType) {
                ViewType.CARDVIEW -> {
                    val cardDB = CardPortfolioDatabaseHelper(this@AddPortfolioActivity)
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
            // ?????? ????????? ????????? ????????? ????????? ????????????, ????????? ?????????
            val userData = dbHelper.selecetUser(userName)
            if(userData != null) {
                if(userData.profileImage != null && userData.profileImage!!.isNotEmpty()) {
                    changedUserImage = userData.profileImage!!

                    try {
                        val bitmap = BitmapFactory.decodeByteArray(changedUserImage, 0, changedUserImage!!.size)
                        bitmap?.let {
                            binding.ivUserImage.setImageBitmap(bitmap)
                        }
                    }catch (e: Exception) {

                    }
                }
                // ?????? ?????? ?????? ????????? ???????????? ????????? ??????
                if(userData.userTitle.isNotEmpty()) {
                    binding.tvUserDescription.text = userData.userTitle
                    checkUserDescription = true
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
                setTitle(getString(R.string.activityMain_tab_item_add))

                when(intent.getIntExtra("viewType", 1)) {
                    ViewType.CARDVIEW -> {
                        setView(cardDialogBinding.root)
                        setPositiveButton(getString(R.string.dialog_button_add), DialogInterface.OnClickListener { dialogInterface, _ ->
                            if(cardDialogBinding.etTitle.text.isNullOrEmpty() || cardDialogBinding.etContents.text.isNullOrEmpty())
                                Toast.makeText(this@AddPortfolioActivity, getString(R.string.toast_empty_input_value), Toast.LENGTH_SHORT).show()
                            else {
                                val rowCardPortfolio = Card(0,
                                    intent.getStringExtra("name").toString(),
                                    cardDialogBinding.etTitle.text.toString(),
                                    cardDialogBinding.etContents.text.toString(),
                                    postImage,
                                    if(cardDialogBinding.chkImage.isChecked) DefaultImage.VISIBLE else DefaultImage.GONE,
                                    cardDialogBinding.etUrl.text.toString().trim()
                                )

                                cardPortfolio.add(rowCardPortfolio)
                                cardPortfolioAdapter.notifyDataSetChanged()
                            }
                            // ??? ?????? ??? ?????? ?????????
                            if(cardDialogBinding.root.parent != null)
                                (cardDialogBinding.root.parent as ViewGroup).removeView(cardDialogBinding.root)

                            cardDialogBinding = DialogAddPortfolioCardBinding.inflate(layoutInflater)
                            postImage = byteArrayOf()
                            initEvent()

                            dialogInterface.dismiss()
                        })
                    }
                    ViewType.TIMELINE -> {
                        setView(timelineDialogBinding.root)
                        setPositiveButton(getString(R.string.dialog_button_add), DialogInterface.OnClickListener { dialogInterface, _ ->
                            if(timelineDialogBinding.etTitle.text.isNullOrEmpty() || timelineDialogBinding.etContents.text.isNullOrEmpty() || timelineDialogBinding.tvDate.text == getString(R.string.dialog_add_portfolio_text_date))
                                Toast.makeText(this@AddPortfolioActivity, getString(R.string.toast_empty_input_value), Toast.LENGTH_SHORT).show()
                            else {
                                // ????????? ????????? ??????????????? circleColor ??????
                                val circleColor: Int = when(timelineDialogBinding.rbtCircleColor.checkedRadioButtonId) {
                                    timelineDialogBinding.rbtGreen.id -> CircleType.GREEN
                                    timelineDialogBinding.rbtOrange.id -> CircleType.ORANGE
                                    else -> CircleType.BLUE
                                }

                                val rowTimelinePortfolio = Timeline(0,
                                    intent.getStringExtra("name").toString(),
                                    timelineDialogBinding.etTitle.text.toString(),
                                    timelineDialogBinding.etContents.text.toString(),
                                    timelineDialogBinding.tvDate.text.toString(),
                                    postImage,
                                    if(timelineDialogBinding.chkImage.isChecked) DefaultImage.VISIBLE else DefaultImage.GONE,
                                    timelineDialogBinding.etUrl.text.toString().trim(),
                                    circleColor
                                )
                                timelinePortfolio.add(rowTimelinePortfolio)
                                timelinePortfolioAdapter.notifyDataSetChanged()
                            }
                            // ??? ?????? ??? ?????? ?????????
                            if(timelineDialogBinding.root.parent != null)
                                (timelineDialogBinding.root.parent as ViewGroup).removeView(timelineDialogBinding.root)
                            timelineDialogBinding = DialogAddPortfolioTimelineBinding.inflate(layoutInflater)
                            postImage = byteArrayOf()
                            initEvent()

                            dialogInterface.dismiss()
                        })
                    }
                    ViewType.MESSENGER -> {
                        setView(messengerDialogBinding.root)
                        setPositiveButton(getString(R.string.dialog_button_add), DialogInterface.OnClickListener { dialogInterface, _ ->
                            if(messengerDialogBinding.etTitle.text.isNullOrEmpty() || messengerDialogBinding.etContents.text.isNullOrEmpty())
                                Toast.makeText(this@AddPortfolioActivity, getString(R.string.toast_empty_input_value), Toast.LENGTH_SHORT).show()
                            else {
                                val rowMessengerPortfolio = Messenger(0,
                                    intent.getStringExtra("name").toString(),
                                    messengerDialogBinding.etTitle.text.toString(),
                                    messengerDialogBinding.etContents.text.toString(),
                                    postImage,
                                    if(messengerDialogBinding.chkImage.isChecked) DefaultImage.VISIBLE else DefaultImage.GONE,
                                    messengerDialogBinding.etUrl.text.toString().trim()
                                )
                                messengerPortfolio.add(rowMessengerPortfolio)
                                messengerPortfolioAdapter.notifyDataSetChanged()
                            }
                            // ??? ?????? ??? ?????? ?????????
                            if(messengerDialogBinding.root.parent != null)
                                (messengerDialogBinding.root.parent as ViewGroup).removeView(messengerDialogBinding.root)
                            messengerDialogBinding = DialogAddPortfolioMessengerBinding.inflate(layoutInflater)
                            postImage = byteArrayOf()
                            initEvent()

                            dialogInterface.dismiss()
                        })
                    }
                }

                setNegativeButton(getString(R.string.dialog_button_cancel), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()

                    when(intent.getIntExtra("viewType", 1)) {
                        ViewType.CARDVIEW -> {
                            // ??? ?????? ??? ?????? ?????????
                            if(cardDialogBinding.root.parent != null)
                                (cardDialogBinding.root.parent as ViewGroup).removeView(cardDialogBinding.root)

                            cardDialogBinding = DialogAddPortfolioCardBinding.inflate(layoutInflater)
                        }
                        ViewType.TIMELINE -> {
                            // ??? ?????? ??? ?????? ?????????
                            if(timelineDialogBinding.root.parent != null)
                                (timelineDialogBinding.root.parent as ViewGroup).removeView(timelineDialogBinding.root)
                            timelineDialogBinding = DialogAddPortfolioTimelineBinding.inflate(layoutInflater)
                        }
                        ViewType.MESSENGER -> {
                            // ??? ?????? ??? ?????? ?????????
                            if(messengerDialogBinding.root.parent != null)
                                (messengerDialogBinding.root.parent as ViewGroup).removeView(messengerDialogBinding.root)
                            messengerDialogBinding = DialogAddPortfolioMessengerBinding.inflate(layoutInflater)
                        }
                    }
                    postImage = byteArrayOf()
                    initEvent()
                })
                setCancelable(false)
                show()
            }
        }

        binding.cardViewUser.setOnClickListener {
            // ????????? ????????? ??? ?????? ???
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            getUserProfileImage.launch(intent)
        }

        // ??? ?????? ????????? ?????? ????????? ?????? ?????????
        cardDialogBinding.btnImage.setOnClickListener {
            // ????????? ????????? ??? ?????? ???
            getPostImage()
        }
        timelineDialogBinding.btnImage.setOnClickListener {
            // ????????? ????????? ??? ?????? ???
            getPostImage()
        }
        messengerDialogBinding.btnImage.setOnClickListener {
            // ????????? ????????? ??? ?????? ???
            getPostImage()
        }

        // ??? ??? ?????? ?????? ?????????
        binding.ivEditDescription.setOnClickListener {
            val dialogBinding = DialogEditUserDescriptionBinding.inflate(layoutInflater)
            AlertDialog.Builder(this).run {
                setTitle(getString(R.string.dialog_edit_description_title))
                setView(dialogBinding.root)
                setPositiveButton(getString(R.string.dialog_button_save)) { dialogInterface, _ ->
                    dialogInterface.dismiss()

                    val description = dialogBinding.etDescription.text.toString()

                    if(description.isNullOrEmpty())
                        Toast.makeText(this@AddPortfolioActivity, getString(R.string.toast_empty_input_value), Toast.LENGTH_SHORT).show()
                    else {
                        checkUserDescription = true
                        binding.tvUserDescription.text = description
                    }
                }
                setNegativeButton(getString(R.string.dialog_button_cancel)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                show()
            }
        }
    }

    fun getPostImage() {
        // ????????? ????????? ??? ?????? ???
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        getPostImage.launch(intent)
    }

    fun insertPortfolio() {
        val userName = intent.getStringExtra("name")
        val userViewType = intent.getIntExtra("viewType", 1)
        val userTitle: String = if(checkUserDescription)
            binding.tvUserDescription.text.toString()
        else
            ""

        // add user
        val user = User(userName!!, userTitle!!, changedUserImage, userViewType)
        val userDB = UserDatabaseHelper(this@AddPortfolioActivity)
        userDB.insertData(user)

        // add portfolio
        when(userViewType) {
            ViewType.CARDVIEW -> {
                val portfolioDB = CardPortfolioDatabaseHelper(this@AddPortfolioActivity)
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
        val userTitle: String = if(checkUserDescription)
            binding.tvUserDescription.text.toString()
        else
            ""

        // update userProfileImage
        val user = User(userName!!, userTitle, changedUserImage, userViewType)
        val userDB = UserDatabaseHelper(this@AddPortfolioActivity)
        userDB.updateData(user)

        // update portfolio
        when(userViewType) {
            ViewType.CARDVIEW -> {
                val portfolioDB = CardPortfolioDatabaseHelper(this@AddPortfolioActivity)
                
                // ?????? ?????? ???????????? ??? ??????????????? ?????? ??? ??????
                portfolioDB.deleteData(userName!!)
                
                cardPortfolio.forEach {
                    portfolioDB.insertData(it)
                }
            }
            ViewType.TIMELINE -> {
                val portfolioDB = TimelinePortfolioDatabaseHelper(this@AddPortfolioActivity)

                // ?????? ?????? ???????????? ??? ??????????????? ?????? ??? ??????
                portfolioDB.deleteData(userName!!)

                timelinePortfolio.forEach {
                    portfolioDB.insertData(it)
                }
            }
            ViewType.MESSENGER -> {
                val portfolioDB = MessengerPortfolioDatabaseHelper(this@AddPortfolioActivity)

                // ?????? ?????? ???????????? ??? ??????????????? ?????? ??? ??????
                portfolioDB.deleteData(userName!!)

                messengerPortfolio.forEach {
                    portfolioDB.insertData(it)
                }
            }
        }
    }

    fun setBitmapImage(uri: Uri) {
        val bitmap = RotateImageController(this@AddPortfolioActivity).rotateImage(uri)
        if(bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            changedUserImage = outputStream.toByteArray()
            outputStream.close()
            binding.ivUserImage.setImageBitmap(bitmap)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.menu_save -> {
            if(checkUser) {
                // ?????? ?????? ????????? ????????????
                updatePortfolio()
            } else {
                // ?????? ????????? ??????
                insertPortfolio()
            }
            Toast.makeText(this@AddPortfolioActivity, getString(R.string.toast_saved), Toast.LENGTH_SHORT).show()
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
            setTitle(getString(R.string.dialog_title_cancel_add_portfolio))
            setMessage(getString(R.string.dialog_message_cancel_add_portfolio))
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