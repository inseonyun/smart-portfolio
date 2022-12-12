package com.douzone.smart.portfolio.controller

import android.content.Context
object BrowserController {

    fun checkToInstallChrome(context: Context, packageName: String): Boolean =
        context.packageManager.getLaunchIntentForPackage(packageName) != null

}