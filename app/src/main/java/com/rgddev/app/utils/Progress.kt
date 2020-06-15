package com.rgddev.app.utils

import android.app.ProgressDialog
import android.content.Context
import com.rgddev.app.R

class Progress(private val activity: Context) {

    private var pDialog: ProgressDialog? = null


    fun createDialog(cancelable: Boolean) {
        pDialog = ProgressDialog(activity, R.style.AppCompatAlertDialogStyle)


        pDialog!!.setCancelable(cancelable)


    }

    fun DialogMessage(message: String) {
        pDialog!!.setMessage(message)
    }

    fun showDialog() {
        if (!pDialog!!.isShowing)
            pDialog!!.show()
    }

    fun hideDialog() {
        if (pDialog!!.isShowing)
            pDialog!!.dismiss()
    }
}