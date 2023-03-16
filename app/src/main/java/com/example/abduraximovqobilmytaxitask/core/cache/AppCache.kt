package com.example.abduraximovqobilmytaxitask.core.cache

import android.content.Context
import android.content.SharedPreferences

class AppCache private constructor(context: Context) {
    private val USER_PHOTO = "userPhoto"
    private val APP_THEME = "night"

    private val preferences: SharedPreferences =
        context.getSharedPreferences("MODE", Context.MODE_PRIVATE)

    private var editor: SharedPreferences.Editor? = null

    companion object {
        var appCache: AppCache? = null
            private set

        fun init(context: Context) {
            if (appCache == null) {
                appCache = AppCache(context)
            }
        }
    }

    fun getUserPhoto(): String = preferences.getString(USER_PHOTO, "null")!!

    fun setUserPhoto(userPhone: String) {
        editor = preferences.edit()
        editor?.apply {
            putString(USER_PHOTO, userPhone)
            apply()
        }
    }

    fun getTheme(): Boolean = preferences.getBoolean(APP_THEME, false)

    fun setTheme(theme: Boolean) {
        editor = preferences.edit()
        editor?.apply {
            putBoolean(APP_THEME, theme)
            apply()
        }
    }

}