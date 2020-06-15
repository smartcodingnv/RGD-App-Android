package com.rgddev.app.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.rgddev.app.utils.PreferenceHelper
import com.rgddev.app.R
import com.rgddev.app.adapter.NotificationListAdapter
import com.rgddev.app.network.ApiClient
import com.rgddev.app.network.ApiInterface
import com.rgddev.app.responce.notificationlist.DataItem
import com.rgddev.app.responce.notificationlist.ResponceNotification
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.Progress
import com.rgddev.app.utils.Utils
import kotlinx.android.synthetic.main.activity_notification_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationListActivity : AppCompatActivity() {

    private var notificationResponce: String = ""
    private var notificationListdata: List<DataItem>? = null
    // TODO: Rename and change types of parameters
    private lateinit var rv_notification: RecyclerView

    private var progress: Progress? = null
    private var utils: Utils? = null
    private var helper: PreferenceHelper? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private lateinit var notificationListAdapter: NotificationListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)
        setSupportActionBar(toolbar)

        progress = Progress(this)
        utils = Utils(this)
        helper = PreferenceHelper(this, AppConfig.PREFERENCE.PREF_FILE)

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            onBackPressed()
        })
        rv_notification = findViewById(R.id.rv_notificationlist)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        val gson = Gson()
        val notification =
            gson.fromJson(
                helper!!.LoadStringPref(AppConfig.PREFERENCE.NOTIFICATIONRESPONCE, ""),
                ResponceNotification::class.java
            )

        if (notification == null) {
            notificationlistdata(AppConfig.URL.TOKEN, "", 1)
        } else {
            notificationListdata = notification.data!!
            setnotificationData()
            notificationlistdata(AppConfig.URL.TOKEN, "", 2)
        }

        swipeRefreshLayout.setOnRefreshListener {

            if (notification == null) {
                notificationlistdata(AppConfig.URL.TOKEN, "", 1)
            } else {
                notificationListdata = notification.data!!
                setnotificationData()
                notificationlistdata(AppConfig.URL.TOKEN, "", 2)
            }

        }
    }

    private fun notificationlistdata(header: String, sync_date: String, flag: Int) {

        progress!!.createDialog(false)
        progress!!.DialogMessage(getString(R.string.please_wait))
        if (flag == 1) {
            progress!!.showDialog()
        }

        utils!!.hideKeyboard()

        val apiService = ApiClient.client?.create(ApiInterface::class.java)
        val checkBookingResponce = apiService?.CALL_NOTIFICATION(
            token = header,
            syncDate = ""
        )

        checkBookingResponce?.enqueue(object : Callback<ResponceNotification> {
            override fun onResponse(@NonNull call: Call<ResponceNotification>, @NonNull response: Response<ResponceNotification>) {
                swipeRefreshLayout.isRefreshing = false

                progress!!.hideDialog()
                val newsResponse = response.body()
                Log.e("codee", " " + response.code())
                if (response.code() == AppConfig.URL.SUCCESS) {
                    if (newsResponse!!.apiStatus == 1) {

                        val gson = Gson()
                        notificationResponce = gson.toJson(newsResponse)
                        helper!!.initPref()
                        helper!!.SaveStringPref(
                            AppConfig.PREFERENCE.NOTIFICATIONRESPONCE,
                            notificationResponce
                        )
                        helper!!.ApplyPref()
                        notificationListdata = newsResponse.data!!
                        setnotificationData()


                    } else {

                        if (newsResponse.data == null || newsResponse.data.isEmpty()) {
                            Toast.makeText(
                                this@NotificationListActivity,
                                newsResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }
                } else {
//                    Toast.makeText(
//                        this@NotificationListActivity,
//                        getString(R.string.msg_unexpected_error),
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }

            override fun onFailure(@NonNull call: Call<ResponceNotification>, @NonNull t: Throwable) {
                swipeRefreshLayout.isRefreshing = false

                progress!!.hideDialog()
//                Toast.makeText(
//                    activity,
//                    getString(R.string.msg_internet_conn),
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        })

    }


    private fun setnotificationData() {

        mLayoutManager = LinearLayoutManager(this)
        rv_notification.setLayoutManager(mLayoutManager)
        notificationListAdapter = NotificationListAdapter(this)
        notificationListAdapter.swapData(notificationListdata!!)
        rv_notification.adapter = notificationListAdapter
    }

}
