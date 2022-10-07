package com.douzone.smart.portfolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.douzone.smart.portfolio.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // back button event
    var backTime = 0L

    // menubar
    private lateinit var navigationView : NavigationView
    private lateinit var drawer_layout : DrawerLayout

    private lateinit var fragment_home: Fragment_Home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(getLayoutInflater())
        val view = binding.root
        setContentView(view)

        fragment_home = Fragment_Home()

        initToolBar()
        initMenuButton()
        initFrameLayout()
    }
    fun initToolBar() {
        val toolbar = binding.toolbar

        // menubar
        drawer_layout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigation_view)

        setSupportActionBar(toolbar)// 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_button_menu) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게
        navigationView.setNavigationItemSelectedListener(this) //navigation 리스너
    }

    fun initFrameLayout() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_main, fragment_home!!)
            .commit()
    }

    fun initMenuButton() {
        binding.imgbtnBack.setOnClickListener {
            if(drawer_layout.isDrawerOpen(GravityCompat.START)){
                drawer_layout.closeDrawers()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home->{ // 메뉴 버튼
                drawer_layout.openDrawer(GravityCompat.START)    // 네비게이션 드로어 열기
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

        }
        return false
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawers()
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