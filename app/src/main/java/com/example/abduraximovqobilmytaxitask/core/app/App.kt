package com.example.abduraximovqobilmytaxitask.core.app

import android.app.Application
import com.example.abduraximovqobilmytaxitask.core.cache.AppCache

class App : Application() {
    //    private val localeAppDelegate = LocaleHelperApplicationDelegate()
    override fun onCreate() {
        super.onCreate()
        instance = this
        AppCache.init(this)
    }

    companion object {
        var instance: Application? = null
            private set
    }
}