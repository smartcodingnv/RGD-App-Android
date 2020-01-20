package com.rgddev.app.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.greenspot.app.utils.PreferenceHelper
import com.rgddev.app.R
import com.rgddev.app.adapter.GuardImageAdapter
import com.rgddev.app.network.ApiClient
import com.rgddev.app.network.ApiInterface
import com.rgddev.app.responce.garddetails.ResponceGardDetails
import com.rgddev.app.responce.gardtimetable.DataItem
import com.rgddev.app.responce.gardtimetable.ResponceGardTimeTable
import com.rgddev.app.responce.notificationlist.ResponceNotification
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.Progress
import com.rgddev.app.utils.Utils
import kotlinx.android.synthetic.main.activity_guard_detail.*
import kotlinx.android.synthetic.main.content_guard_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GuardDetailActivity : AppCompatActivity() {
    private var id: Int = 0

    private var progress: Progress? = null
    private var utils: Utils? = null
    private var helper: PreferenceHelper? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private lateinit var guardImageAdapter: GuardImageAdapter
    private var gardListdata: List<DataItem>? = null
    private var gardListNotificationdata: List<com.rgddev.app.responce.notificationlist.DataItem>? =
        null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guard_detail)
        setSupportActionBar(toolbar)



        progress = Progress(this)
        utils = Utils(this)
        helper = PreferenceHelper(this, AppConfig.PREFERENCE.PREF_FILE)

        val intenttt = getIntent()

        id = intenttt.getIntExtra(AppConfig.EXTRA.GUARDID, 0)
        val checknoti = intenttt.getIntExtra(AppConfig.EXTRA.CHECKNOTIFICAION, 0)

        val img_notification: ImageView = findViewById(R.id.img_notification)

        img_notification.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, NotificationListActivity::class.java);
            startActivity(intent)

        })

        if (Utils.isInternetAvailable(this)) {
            rv_guardimg.visibility = View.VISIBLE
        } else {
            rv_guardimg.visibility = View.GONE
        }

        Log.e("garddetailssss", " " + id)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            if (checknoti == 1) {
                onBackPressed()

            } else if (checknoti == 2) {
                val intent = Intent(this, MainActivity::class.java);
                startActivity(intent)
                finish()
            } else if (checknoti == 3) {
                onBackPressed()
            }

        })
        if (checknoti == 1) {

            val gson = Gson()
            val gardresponce =
                gson.fromJson(
                    helper!!.LoadStringPref(AppConfig.PREFERENCE.GARDRESPONCE, ""),
                    ResponceGardTimeTable::class.java
                )

            gardListdata = gardresponce.data!!
            gardListdata!!.mapIndexed { index, dataItem ->

                if (id.equals(dataItem.id)) {
                    txt_gardtitle.text = gardresponce.data[index].title
                    txt_startdate.text = gardresponce.data[index].startDateTime
                    txt_enddate.text = gardresponce.data[index].endDateTime

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        txt_description.text = Html.fromHtml(
                            gardresponce.data[index].description,
                            Html.FROM_HTML_MODE_COMPACT
                        )
                    } else {
                        txt_description.text = Html.fromHtml(gardresponce.data[index].description)
                    }


                    if (gardresponce.data[index].image.isNullOrEmpty()) {
                        rv_guardimg.visibility = View.GONE
                    } else {

                        if (Utils.isInternetAvailable(this)) {
                            rv_guardimg.visibility = View.VISIBLE
                            mLayoutManager = LinearLayoutManager(this@GuardDetailActivity)
                            rv_guardimg.setLayoutManager(mLayoutManager)
                            guardImageAdapter = GuardImageAdapter(this@GuardDetailActivity)
                            guardImageAdapter.swapData(gardresponce.data[index].image!!)
                            rv_guardimg.adapter = guardImageAdapter
                        }


                    }

                }
            }
        } else if (checknoti == 3) {
            val gson = Gson()
            val gardresponce =
                gson.fromJson(
                    helper!!.LoadStringPref(AppConfig.PREFERENCE.NOTIFICATIONRESPONCE, ""),
                    ResponceNotification::class.java
                )

            gardListNotificationdata = gardresponce.data!!
            gardListNotificationdata!!.mapIndexed { index, dataItem ->

                if (id.equals(dataItem.typeId)) {

                    Log.e("garddetails", " " + id)

                    txt_gardtitle.text = gardresponce.data[index].guardTimetable.title
                    txt_startdate.text = gardresponce.data[index].guardTimetable.startDateTime
                    txt_enddate.text = gardresponce.data[index].guardTimetable.endDateTime

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        txt_description.text = Html.fromHtml(
                            gardresponce.data[index].guardTimetable.description,
                            Html.FROM_HTML_MODE_COMPACT
                        )
                    } else {
                        txt_description.text =
                            Html.fromHtml(gardresponce.data[index].guardTimetable.description)
                    }


                    if (gardresponce.data[index].guardTimetable.image.isNullOrEmpty()) {
                        rv_guardimg.visibility = View.GONE
                    } else {

                        if (Utils.isInternetAvailable(this)) {
                            rv_guardimg.visibility = View.VISIBLE
                            mLayoutManager = LinearLayoutManager(this@GuardDetailActivity)
                            rv_guardimg.setLayoutManager(mLayoutManager)
                            guardImageAdapter = GuardImageAdapter(this@GuardDetailActivity)
                            guardImageAdapter.swapData(gardresponce.data[index].guardTimetable.image!!)
                            rv_guardimg.adapter = guardImageAdapter
                        }


                    }

                }


            }
        } else if (checknoti == 2) {

            initview()

        }


//        initview()
    }

    private fun initview() {

        gardDetails(AppConfig.URL.TOKEN, "", id.toString())

    }

    private fun gardDetails(header: String, sync_date: String, id: String) {

        progress!!.createDialog(false)
        progress!!.DialogMessage(getString(R.string.please_wait))
        progress!!.showDialog()
        utils!!.hideKeyboard()

        val apiService = ApiClient.client?.create(ApiInterface::class.java)
        val responceGardDetails = apiService?.CALL_GAURDDETAILS(
            token = header,
            syncDate = "",
            id = id

        )

        responceGardDetails?.enqueue(object : Callback<ResponceGardDetails> {
            override fun onResponse(@NonNull call: Call<ResponceGardDetails>, @NonNull response: Response<ResponceGardDetails>) {

                progress!!.hideDialog()
                val gardDetails = response.body()
                Log.e("codee", " " + response.code())
                if (response.code() == AppConfig.URL.SUCCESS) {
                    if (gardDetails!!.apiStatus == 1) {

                        txt_gardtitle.text = gardDetails.data.title
                        txt_startdate.text = gardDetails.data.startDateTime
                        txt_enddate.text = gardDetails.data.endDateTime

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txt_description.text = Html.fromHtml(
                                gardDetails.data.description,
                                Html.FROM_HTML_MODE_COMPACT
                            )
                        } else {
                            txt_description.text = Html.fromHtml(gardDetails.data.description)
                        }


                        if (gardDetails.data.image.isNullOrEmpty()) {
                            rv_guardimg.visibility = View.GONE
                        } else {
                            rv_guardimg.visibility = View.VISIBLE

                            mLayoutManager = LinearLayoutManager(this@GuardDetailActivity)
                            rv_guardimg.setLayoutManager(mLayoutManager)
                            guardImageAdapter = GuardImageAdapter(this@GuardDetailActivity)
                            guardImageAdapter.swapData(gardDetails.data.image!!)
                            rv_guardimg.adapter = guardImageAdapter

                        }


                    } else {


                        Toast.makeText(
                            this@GuardDetailActivity,
                            gardDetails.message,
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                } else {
                    Toast.makeText(
                        this@GuardDetailActivity,
                        getString(R.string.msg_unexpected_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(@NonNull call: Call<ResponceGardDetails>, @NonNull t: Throwable) {
                progress!!.hideDialog()
               /* Toast.makeText(
                    this@GuardDetailActivity,
                    getString(R.string.msg_internet_conn),
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        })

    }


}
