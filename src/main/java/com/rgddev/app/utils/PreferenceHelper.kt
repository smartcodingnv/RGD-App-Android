package com.greenspot.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(ctx: Context, FileName: String) {

    private var editor: SharedPreferences.Editor? = null
    private val prefs: SharedPreferences

    init {
        prefs = ctx.getSharedPreferences(FileName, Context.MODE_PRIVATE)
    }

    fun clearAllPrefs() {
        prefs.edit().clear().apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun initPref() {
        editor = prefs.edit()
    }

    fun ApplyPref() {
        editor!!.apply()
    }

    fun SaveStringPref(key: String, value: String) {
        editor!!.putString(key, value)
    }

    fun LoadStringPref(key: String, DefaultValue: String): String? {
        return prefs.getString(key, DefaultValue)
    }

    fun SaveIntPref(key: String, value: Int) {
        editor!!.putInt(key, value)
    }

    fun LoadIntPref(key: String, DefaultValue: Int): Int {
        return prefs.getInt(key, DefaultValue)
    }

    fun SaveBooleanPref(key: String, value: Boolean) {
        editor!!.putBoolean(key, value)
    }

    fun LoadBooleanPref(key: String, DefaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, DefaultValue)
    }

    fun SaveSetPref(key: String, value: Set<String>) {
        editor!!.putStringSet(key, value)
    }
    /*
    public Set<String> LoadSetPref(String key, Set<String> value) {
        return prefs.getStringSet(key, value);
    }

    public <LsDeviceInfo> void setList(String key, com.lifesense.ble.bean.LsDeviceInfo list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        editor.putString(key, json);
    }*/


}