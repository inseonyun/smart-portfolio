package com.douzone.smart.portfolio

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.airbnb.lottie.LottieAnimationView
import com.douzone.smart.portfolio.`interface`.DialogUserOnItemClick
import com.douzone.smart.portfolio.adapter.DialogAddPortfolioListViewAdapter
import com.douzone.smart.portfolio.adapter.DialogDeleteListViewAdapter
import com.douzone.smart.portfolio.adapter.MenuUserListViewAdapter
import com.douzone.smart.portfolio.data.*
import com.douzone.smart.portfolio.databinding.ActivityMainBinding
import com.douzone.smart.portfolio.databinding.DialogAddUserBinding
import com.douzone.smart.portfolio.databinding.DialogDeletePortfolioBinding
import com.douzone.smart.portfolio.databinding.TabDialogAddPortfolioBinding
import com.douzone.smart.portfolio.db.UserDatabaseHelper
import com.douzone.smart.portfolio.fragment.Fragment_Home
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlin.random.Random

class MainActivity : AppCompatActivity(), DialogUserOnItemClick
{
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // back button event
    private var backTime = 0L

    // menubar
    private lateinit var navigationView : NavigationView
    private lateinit var drawerLayout : DrawerLayout

    //duplicate
    private lateinit var fragmentHome: Fragment_Home

    private lateinit var bindingDialogUserDelete: DialogDeletePortfolioBinding
    private lateinit var dialogDeleteUserAdapter: DialogDeleteListViewAdapter

    private lateinit var bindingDialogAddPortfolio: TabDialogAddPortfolioBinding
    private lateinit var dialogAddPortfolioAdapter: DialogAddPortfolioListViewAdapter

    private lateinit var progressDialog: AppCompatDialog

    private var loadingTime = Random.nextLong(1000, 3000)

    override fun onClick(userName: String, userTitle: String, viewType: Int, addDelete: Int) {
        when(addDelete) {
            AddDelete.ADD -> {
                // ??????????????? ??????
                val intent = Intent(this@MainActivity, AddPortfolioActivity::class.java)
                intent.putExtra("name", userName)
                intent.putExtra("viewType", viewType)
                intent.putExtra("userTitle", userTitle)
                addPortfolioRequestLauncher.launch(intent)
            }
            AddDelete.DELETE -> {
                // ??????????????? ?????? -> activityResultLauncher
                val intent = Intent(this@MainActivity, DeletePortfolioActivity::class.java)
                intent.putExtra("name", userName)
                intent.putExtra("viewType", viewType)
                deletePortfolioRequestLauncher.launch(intent)
            }
        }
    }


    private val addPortfolioRequestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if(activityResult.resultCode == RESULT_OK) {
            // ????????? ??????
            fragmentHome.initUserData()
            fragmentHome.initMenuList()
            fragmentHome.initPages()

            if(!dialogAddPortfolioAdapter.isEmpty && ::bindingDialogAddPortfolio.isInitialized) {
                dialogAddPortfolioAdapter.notifyDataSetChanged()
                bindingDialogAddPortfolio.lvUser.adapter = dialogAddPortfolioAdapter
            }
        }
    }

    private val deletePortfolioRequestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        // ????????? ??????
        fragmentHome.initUserData()
        fragmentHome.initMenuList()
        fragmentHome.initPages()

        dialogDeleteUserAdapter.notifyDataSetChanged()
        bindingDialogUserDelete.lvUser.adapter = dialogDeleteUserAdapter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fragmentHome = Fragment_Home()
        initFrameLayout()

        initToolBar()
        initMenuButton()

        startLoading()

        initBottomNavigation()
        initSlidingDrawer()
    }

    fun startLoading() {
        progressDialog = AppCompatDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setContentView(R.layout.loading_login)
        progressDialog.show()
        val loadingLottie = progressDialog.findViewById<LottieAnimationView>(R.id.loading_lottie)
        loadingLottie?.playAnimation()
        loadingLottie?.repeatCount = ValueAnimator.INFINITE

        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()
        }, loadingTime)
    }

    fun changeViewPagerPage(idx: Int) {
        if(fragmentHome.binding.viewpager.currentItem != idx)
            fragmentHome.binding.viewpager.currentItem = idx
    }

    private fun setListViewHeightBasedOnChildren() {
        val menuListAdapter = binding.lvUser.adapter ?: return

        var totalHeight = 0

        for(i in 0 until menuListAdapter.count) {
            val listItem = menuListAdapter.getView(i, null, binding.lvUser)
            val px = 500 * binding.lvUser.resources.displayMetrics.density
            listItem.measure(View.MeasureSpec.makeMeasureSpec( px.toInt(), View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            totalHeight += listItem.measuredHeight
        }

        val totalDividersHeight = binding.lvUser.dividerHeight * (menuListAdapter.count - 1)
        val totalPadding = binding.lvUser.paddingTop + binding.lvUser.paddingBottom + 50
        val params = binding.lvUser.layoutParams
        params.height = totalHeight + totalDividersHeight + totalPadding
        binding.lvUser.layoutParams = params
        binding.lvUser.requestLayout()
    }

    fun initMenuListUserData(userList: ArrayList<User>) {
        binding.lvUser.adapter = MenuUserListViewAdapter(this, userList)
        dialogDeleteUserAdapter = DialogDeleteListViewAdapter(this, userList, this)
        dialogAddPortfolioAdapter = DialogAddPortfolioListViewAdapter(this, userList, this)
        setListViewHeightBasedOnChildren()
    }

    fun initBottomNavigation() {
        binding.bottomTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            // ??? ?????? ?????? ??? ?????????
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.text) {
                    getString(R.string.activityMain_tab_item_home) -> {
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.frame_main, fragment_home)
//                            .commit()
                    }
                    getString(R.string.activityMain_tab_item_add)-> {
                        bindingDialogAddPortfolio = TabDialogAddPortfolioBinding.inflate(layoutInflater)

                        // ??? ??????
                        binding.bottomTabLayout.selectTab(binding.bottomTabLayout.getTabAt(0))

                        // ?????? ?????????????????? ???????????? ?????????, ????????? ?????????????????? ?????????
                        if(dialogAddPortfolioAdapter.count == 0) {
                            binding.tvPortfolioAdd.callOnClick()
                        } else {
                            // ??????????????? ???????????? ??????
                            bindingDialogAddPortfolio.lvUser.adapter = dialogAddPortfolioAdapter

                            AlertDialog.Builder(this@MainActivity).run {
                                setTitle(getString(R.string.activityMain_tab_item_add))
                                setView(bindingDialogAddPortfolio.root)
                                setNegativeButton(getString(R.string.dialog_button_cancel)) { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                }
                                show()
                            }
                        }
                    }
                    getString(R.string.activityMain_tab_item_delete) -> {
                        // ??? ??????
                        binding.bottomTabLayout.selectTab(binding.bottomTabLayout.getTabAt(0))

                        binding.tvPortfolioDelete.callOnClick()
                    }
                }
            }

            // ????????? ?????? ?????? ?????? ?????? ?????? ?????? ??? ?????????
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            // ????????? ?????? ?????? ?????? ?????? ??? ?????????
            override fun onTabReselected(tab: TabLayout.Tab?) {
                when(tab?.text) {
                    getString(R.string.activityMain_tab_item_home) -> changeViewPagerPage(0)
                }
            }
        })
    }

    fun initSlidingDrawer() {
        binding.tvHome.setOnClickListener {
            changeViewPagerPage(0)
        }
    }

    fun initToolBar() {
        val toolbar = binding.toolbar

        // menubar
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigation_view)

        setSupportActionBar(toolbar)// ????????? ??????????????? ????????? ??????
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // ???????????? ?????? ??? ?????? ?????????
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_button_menu) // ????????? ????????? ??????
        supportActionBar?.setDisplayShowTitleEnabled(false) // ????????? ????????? ????????????
    }

    fun setToolbarUserImage(userImage: Bitmap) {
        binding.toolbarUserImage.setImageBitmap(userImage)
    }

    fun setToolbarUserImageResources(resources: Int) {
        binding.toolbarUserImage.setImageResource(resources)
    }

    fun setToolbarUserName(userName: String ) {
        binding.toolbarUserName.text = userName
    }

    fun setVisibleToolbarLayout(visible: Int) {
        binding.toolbarLayout.visibility = visible
    }

// deprecated
    fun initFrameLayout() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_main, fragmentHome)
            .commit()
    }

    fun initMenuButton() {
        binding.imgbtnBack.setOnClickListener {
            if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawers()
            }
        }

        binding.tvPortfolioAdd.setOnClickListener {
            // ??????????????? ?????? ?????? ??????
            val bindingDialogUserAdd = DialogAddUserBinding.inflate(layoutInflater)
            AlertDialog.Builder(this).run {
                setTitle(getString(R.string.drawer_menu_add))
                setView(bindingDialogUserAdd.root)
                setPositiveButton(getString(R.string.dialog_button_add)) { dialogInterface, _ ->
                    if (bindingDialogUserAdd.etName.text.isNullOrEmpty())
                        Toast.makeText(context, getString(R.string.toast_input_name), Toast.LENGTH_SHORT).show()
                    else {
                        val userName = bindingDialogUserAdd.etName.text.toString().trim()
                        val checkDB = UserDatabaseHelper(this@MainActivity)

                        if (checkDB.checkUser(userName))
                            Toast.makeText(context, getString(R.string.toast_exist_name), Toast.LENGTH_SHORT).show()
                        else {
                            val userViewType =
                                when (bindingDialogUserAdd.radioGroup.checkedRadioButtonId) {
                                    bindingDialogUserAdd.rbtTimeline.id -> ViewType.TIMELINE
                                    bindingDialogUserAdd.rbtMessenger.id -> ViewType.MESSENGER
                                    else -> ViewType.CARDVIEW
                                }
                            val intent = Intent(this@MainActivity, AddPortfolioActivity::class.java)
                            intent.putExtra(
                                "name",
                                bindingDialogUserAdd.etName.text.toString().trim()
                            )
                            intent.putExtra("viewType", userViewType)
                            val userTitle = if(bindingDialogUserAdd.etUserTitle.text.toString().isNullOrEmpty()) "" else bindingDialogUserAdd.etUserTitle.text.toString()
                            intent.putExtra("userTitle", userTitle)
                            addPortfolioRequestLauncher.launch(intent)
                        }
                    }
                    dialogInterface.dismiss()
                }
                setNegativeButton(getString(R.string.dialog_button_cancel)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                show()
            }
        }

        binding.tvPortfolioDelete.setOnClickListener {
            //??????????????? ?????? ?????? ??????
            bindingDialogUserDelete = DialogDeletePortfolioBinding.inflate(layoutInflater)

            // ?????? ?????????????????? ???????????? ??????????????? ???
            if(dialogDeleteUserAdapter.count == 0) {
                Toast.makeText(this@MainActivity, getString(R.string.toast_notExist_portfolio), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ??????????????? ???????????? ??????
            bindingDialogUserDelete.lvUser.adapter = dialogDeleteUserAdapter

            AlertDialog.Builder(this).run {
                setTitle(getString(R.string.drawer_menu_delete))
                setView(bindingDialogUserDelete.root)
                setNegativeButton(getString(R.string.dialog_button_cancel)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                show()
            }
        }

        binding.tvLogout.setOnClickListener {
            // ???????????? ??????
            logoutEvent()
        }
    }

    private fun logoutEvent() {
        AlertDialog.Builder(this).run {
            setTitle(getString(R.string.dialog_title_logout))
            setMessage(R.string.dialog_message_logout)
            setPositiveButton(getString(R.string.dialog_button_yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            setNegativeButton(getString(R.string.dialog_button_cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> { // ?????? ??????
                drawerLayout.openDrawer(GravityCompat.START)    // ??????????????? ????????? ??????
            }
            R.id.menu_logout -> {
                // ???????????? ??????
                logoutEvent()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers()
        }else {
            if(System.currentTimeMillis() - backTime > 3000) {
                Toast.makeText(applicationContext, R.string.back_toast_message, Toast.LENGTH_SHORT).show()
                backTime = System.currentTimeMillis()
            } else {
                super.onBackPressed()
            }
        }
    }
}