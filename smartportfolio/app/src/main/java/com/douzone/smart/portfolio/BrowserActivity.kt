package com.douzone.smart.portfolio

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.douzone.smart.portfolio.controller.BrowserController
import com.douzone.smart.portfolio.databinding.ActivityBrowserBinding

class BrowserActivity : AppCompatActivity() {
    lateinit var binding: ActivityBrowserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initView()
        initToolBar()
        initBrowser()
    }
    fun initBinding() {
        binding = ActivityBrowserBinding.inflate(layoutInflater)
    }

    fun initView() {
        setContentView(binding.root)

        binding.webView.webViewClient = WebViewClient()                         // 새창 띄우지 않기
        binding.webView.webChromeClient = WebChromeClient()

        binding.webView.settings.setSupportZoom(false)                          // 줌 설정 여부
        binding.webView.settings.builtInZoomControls = false                    // 줌 확대/축소 버튼 여부
        binding.webView.settings.javaScriptEnabled = true                       // 자바 스크립트 사용여부
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true   // 자바스크립트가 window open()을 사용할 수 있도록 함
        binding.webView.settings.setSupportMultipleWindows(false)               // 멀티 윈도우 사용 여부
        binding.webView.settings.domStorageEnabled = true                       // 로컬 스토리지 사용 여부
    }

    fun initToolBar() {
        setSupportActionBar(binding.toolbar)// 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // x 버튼으로 사용
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_button_close) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "" // 툴바 타이틀 초기화
    }

    fun initBrowser() {
        val url = intent.getStringExtra("url")
        supportActionBar?.title = url.toString()
        binding.webView.loadUrl(url.toString())
    }

    fun closeWebViewDialog() {
        AlertDialog.Builder(this@BrowserActivity).run {
            setTitle(getString(R.string.dialog_title_close_browser))
            setMessage(getString(R.string.dialog_message_close_browser))
            setPositiveButton(getString(R.string.dialog_button_yes), DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
                finish()
            })
            setNegativeButton(getString(R.string.dialog_button_no), DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
            })
            show()
        }
    }

    fun openExternalBrowser() {
        // 크롬이 깔려있다면 다음 다이얼로그 띄움
        if(BrowserController.checkToInstallChrome(this@BrowserActivity, getString(R.string.package_chrome))) {
            AlertDialog.Builder(this@BrowserActivity).run {
                setTitle(getString(R.string.dialog_title_open_external_browser))
                setMessage(getString(R.string.dialog_message_open_external_browser))
                setPositiveButton(getString(R.string.dialog_button_yes), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra("url")))
                    intent.setPackage(getString(R.string.package_chrome))
                    startActivity(intent)
                    finish()
                })
                setNegativeButton(getString(R.string.dialog_button_no), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })
                show()
            }
        }
        else {
            Toast.makeText(this@BrowserActivity, getString(R.string.toast_Uninstalled_chrome), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_browser, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{ // 메뉴 버튼
                // 웹뷰 닫음
                closeWebViewDialog()
            }
            R.id.menu_external_web -> {
                // 크롬 브라우저로 열음
                openExternalBrowser()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        closeWebViewDialog()
    }
}