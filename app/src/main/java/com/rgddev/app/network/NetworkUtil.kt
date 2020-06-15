package com.rgddev.app.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager


object NetworkUtil {

    var TYPE_WIFI = 1
    var TYPE_MOBILE = 2
    var TYPE_NOT_CONNECTED = 0


    @SuppressLint("MissingPermission")
    fun getConnectivityStatus(context: Context): Int {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI

            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    fun isConnectivityEnable(context: Context): Boolean {
        val conn = NetworkUtil.getConnectivityStatus(context)
        var status = false
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = true
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = true
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = false
        }
        return status
    }
}