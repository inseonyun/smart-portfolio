package com.douzone.smart.portfolio

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    override fun onClick(userName: String, viewType: Int, addDelete: Int) {
        when(addDelete) {
            AddDelete.ADD -> {
                // 포트폴리오 추가
                val intent = Intent(this@MainActivity, AddPortfolioActivity::class.java)
                intent.putExtra("name", userName)
                intent.putExtra("viewType", viewType)
                addPortfolioRequestLauncher.launch(intent)
            }
            AddDelete.DELETE -> {
                // 포트폴리오 삭제 -> activityResultLauncher
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
            // 데이터 갱신
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
        // 데이터 갱신
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
            // 탭 선택 됐을 때 이벤트
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.text) {
                    getString(R.string.activityMain_tab_item_home) -> {
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.frame_main, fragment_home)
//                            .commit()
                    }
                    getString(R.string.activityMain_tab_item_add)-> {
                        bindingDialogAddPortfolio = TabDialogAddPortfolioBinding.inflate(layoutInflater)

                        // 탭 변경
                        binding.bottomTabLayout.selectTab(binding.bottomTabLayout.getTabAt(0))

                        // 유저 포트폴리오가 존재하지 않으면, 메뉴바 다이얼로그를 띄워줌
                        if(dialogAddPortfolioAdapter.count == 0) {
                            binding.tvPortfolioAdd.callOnClick()
                        } else {
                            // 다이얼로그 리스트뷰 연결
                            bindingDialogAddPortfolio.lvUser.adapter = dialogAddPortfolioAdapter

                            AlertDialog.Builder(this@MainActivity).run {
                                setTitle(getString(R.string.activityMain_tab_item_add))
                                setView(bindingDialogAddPortfolio.root)
                                setNegativeButton("취소") { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                }
                                show()
                            }
                        }
                    }
                    getString(R.string.activityMain_tab_item_delete) -> {
                        // 탭 변경
                        binding.bottomTabLayout.selectTab(binding.bottomTabLayout.getTabAt(0))

                        binding.tvPortfolioDelete.callOnClick()
                    }
                }
            }

            // 선택된 탭이 아닌 다른 탭을 선택 했을 때 이벤트
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            // 선택된 탭을 다시 선택 했을 때 이벤트
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

        setSupportActionBar(toolbar)// 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_button_menu) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게
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
            // 포트폴리오 추가 기능 구현
            val bindingDialogUserAdd = DialogAddUserBinding.inflate(layoutInflater)
            AlertDialog.Builder(this).run {
                setTitle(getString(R.string.drawer_menu_add))
                setView(bindingDialogUserAdd.root)
                setPositiveButton("추가") { dialogInterface, _ ->
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
                            addPortfolioRequestLauncher.launch(intent)
                        }
                    }
                    dialogInterface.dismiss()
                }
                setNegativeButton("취소") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                show()
            }
        }

        binding.tvPortfolioDelete.setOnClickListener {
            //포트폴리오 삭제 기능 구현
            bindingDialogUserDelete = DialogDeletePortfolioBinding.inflate(layoutInflater)

            // 유저 포트폴리오가 존재해야 띄워주도록 함
            if(dialogDeleteUserAdapter.count == 0) {
                Toast.makeText(this@MainActivity, getString(R.string.toast_notExist_portfolio), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 다이얼로그 리스트뷰 연결
            bindingDialogUserDelete.lvUser.adapter = dialogDeleteUserAdapter

            AlertDialog.Builder(this).run {
                setTitle(getString(R.string.drawer_menu_delete))
                setView(bindingDialogUserDelete.root)
                setNegativeButton("취소") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{ // 메뉴 버튼
                drawerLayout.openDrawer(GravityCompat.START)    // 네비게이션 드로어 열기
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