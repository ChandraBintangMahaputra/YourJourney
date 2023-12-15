package com.example.yourjourney.manager

import android.content.Context
import android.content.SharedPreferences
import com.example.yourjourney.extention.Value.KEY_EMAIL
import com.example.yourjourney.extention.Value.KEY_IS_LOGIN
import com.example.yourjourney.extention.Value.KEY_TOKEN
import com.example.yourjourney.extention.Value.KEY_USER_ID
import com.example.yourjourney.extention.Value.KEY_USER_NAME
import com.example.yourjourney.extention.Value.PREFS_NAME

class SessionHandler(context: Context) {
    private var prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor = prefs.edit()

    fun setStringPreference(prefKey: String, value: String) {
        editor.putString(prefKey, value)
        editor.apply()
    }

    fun setIntPreference(prefKey: String, value: Int) {
        editor.putInt(prefKey, value)
        editor.apply()
    }

    fun setBooleanPreference(prefKey: String, value: Boolean) {
        editor.putBoolean(prefKey, value)
        editor.apply()
    }

    fun clearPreferenceByKey(prefKey: String) {
        editor.remove(prefKey)
        editor.apply()
    }

    fun clearPreferences() {
        editor.clear().apply()
    }

    val getToken = prefs.getString(KEY_TOKEN, "")
    val getUserId = prefs.getString(KEY_USER_ID, "")
    val isLogin = prefs.getBoolean(KEY_IS_LOGIN, false)
    val getUserName = prefs.getString(KEY_USER_NAME, "")
    val getEmail = prefs.getString(KEY_EMAIL, "")
}