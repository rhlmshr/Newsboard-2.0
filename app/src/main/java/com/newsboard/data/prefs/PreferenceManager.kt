package com.newsboard.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.lang.reflect.Type

class PreferenceManager private constructor(context: Context) {
    private val sharedPref: SharedPreferences =
        android.preference.PreferenceManager.getDefaultSharedPreferences(context)

    companion object {

        private var mAppSharedPreferenceInstance: PreferenceManager? = null

        fun getInstance(context: Context): PreferenceManager? {
            if (mAppSharedPreferenceInstance == null) {
                synchronized(PreferenceManager::class.java) {
                    if (mAppSharedPreferenceInstance == null)
                        mAppSharedPreferenceInstance = PreferenceManager(context)
                }
            }
            return mAppSharedPreferenceInstance
        }
    }

    fun getInt(key: String): Int {
        return sharedPref.getInt(key, 0)
    }

    fun putLong(key: String, value: Int) {
        val editor = sharedPref.edit()
        editor.putLong(key, value.toLong())
        editor.apply()
    }

    fun putString(key: String, value: String?) {
        val editor = sharedPref.edit()
        editor.putString(key, value)         // Commit the edits!
        editor.apply()
    }

    fun getString(key: String): String? {
        return sharedPref.getString(key, null)
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun clearAllPrefs() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun putInt(key: String, value: Int) {
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun putObject(key: String, o: Any) {
        val editor = sharedPref.edit()
        editor.putString(key, Gson().toJson(o))
        editor.apply()
    }

    fun getObject(key: String, clazz: Type): Any? {
        return Gson().fromJson<Any>(sharedPref.getString(key, ""), clazz)
    }

    fun hasKey(key: String): Boolean {
        return sharedPref.contains(key)
    }
}
