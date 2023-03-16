package com.example.abduraximovqobilmytaxitask.core.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.abduraximovqobilmytaxitask.core.cache.AppCache

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        AppCache.init(this)
        if (AppCache.appCache!!.getTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    companion object {
        var instance: Application? = null
            private set
    }
}