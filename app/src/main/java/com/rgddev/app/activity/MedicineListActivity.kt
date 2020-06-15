package com.rgddev.app.activity

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rgddev.app.R
import com.rgddev.app.adapter.MedicineListAdapter
import com.rgddev.app.network.ApiClient
import com.rgddev.app.network.ApiInterface
import com.rgddev.app.responce.insurancedetails.DataItem
import com.rgddev.app.responce.insurancedetails.ResponceMedicnList
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.PreferenceHelper
import com.rgddev.app.utils.Progress
import com.rgddev.app.utils.Utils
import kotlinx.android.synthetic.main.activity_medecin_list.*
import kotlinx.android.synthetic.main.content_medecin_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MedicineListActivity : AppCompatActivity() {

    private val activity = this
    private var idList = ArrayList<String>()

    private var insuranceId: Int = 0
    private var insuranceName: String = ""

    private var progress: Progress? = null
    private var utils: Utils? = null
    private var helper: PreferenceHelper? = null
    private val gson = Gson()

    private var medicineListAdapter: MedicineListAdapter? = null
    private var listAllData = ArrayList<DataItem>()
    private var hashMap = HashMap<String, String>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medecin_list)
        setSupportActionBar(toolbar)

        progress = Progress(this)
        utils = Utils(this)
        helper = PreferenceHelper(this, AppConfig.PREFERENCE.PREF_FILE)

        insuranceId = intent.getIntExtra(AppConfig.EXTRA.INSUSRANCEID, 0)
        insuranceName = intent.getStringExtra(AppConfig.EXTRA.INSUSRANCETITLE) ?: ""

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        txt_title.text = insuranceName

        et_search.addTextChangedListener {
            if (listAllData.isNotEmpty()) {
                val text = it.toString().toLowerCase(Locale.ENGLISH)
                val listData: ArrayList<DataItem> = ArrayList()
                for (data in listAllData) {
                    if (data.name.toLowerCase(Locale.ENGLISH).contains(text)) {
                        listData.add(data)
                    }
                }
                setupMedicineList(listData)
            } else {
                setupMedicineList(listAllData)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        getMedicineOfflineData()
        getMedicineList()
    }

    private fun getMedicineOfflineData() {
        val insurancePlan = helper!!.LoadStringPref(AppConfig.PREFERENCE.MEDICINLIST, "")

        var offlineData = ""

        with(insurancePlan!!) {
            if (isNotEmpty()) {
                hashMap = gson.fromJson(
                    this,
                    object : TypeToken<HashMap<String, String>>() {}.type
                )
                offlineData = hashMap[insuranceId.toString()] ?: ""
            }
        }

        with(offlineData) {
            if (isNotEmpty()) {
                if (contains("{") && contains("}")) {
                    if (isNotEmpty()) {
                        listAllData = gson.fromJson(
                            this,
                            object : TypeToken<ArrayList<DataItem>>() {}.type
                        )
                    }
                } else {
                    val value = replace(Regex("[ \\[\\]']"), "")
                    if (contains(",")) {
                        for (v in value.split(",")) {
                            listAllData.add(DataItem(name = v))
                        }
                    } else {
                        listAllData.add(DataItem(name = value))
                    }
                }
            }
        }

        setupMedicineList(listAllData)
    }

    private fun getMedicineList() {

        progress!!.createDialog(false)
        progress!!.DialogMessage(getString(R.string.please_wait))
        progress!!.showDialog()
        utils!!.hideKeyboard()

        ApiClient.client.create(ApiInterface::class.java).CALL_INSURANCEPLANDETAILS(
            token = AppConfig.URL.TOKEN,
            syncDate = "",
            id = insuranceId.toString()
        ).enqueue(object : Callback<ResponceMedicnList> {
            override fun onResponse(
                @NonNull call: Call<ResponceMedicnList>,
                @NonNull response: Response<ResponceMedicnList>
            ) {
                progress!!.hideDialog()

                if (response.isSuccessful) {
                    if (response.body()?.apiStatus == 1 && !response.body()?.data.isNullOrEmpty()) {
                        listAllData.clear()
                        listAllData.addAll(response.body()?.data!!)

                        hashMap[insuranceId.toString()] = gson.toJson(listAllData)

                        with(helper!!) {
                            initPref()
                            SaveStringPref(
                                AppConfig.PREFERENCE.MEDICINLIST,
                                gson.toJson(hashMap)
                            )
                            ApplyPref()
                        }

                        setupMedicineList(listAllData)
                    } else {
                        Toast.makeText(
                            this@MedicineListActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MedicineListActivity,
                        getString(R.string.msg_unexpected_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(@NonNull call: Call<ResponceMedicnList>, @NonNull t: Throwable) {
                progress!!.hideDialog()
            }
        })
    }

    private fun setupMedicineList(listData: ArrayList<DataItem>) {
        rv_medicin.apply {
            layoutManager = LinearLayoutManager(context)
        }
        if (medicineListAdapter == null) {
            medicineListAdapter = MedicineListAdapter(activity)
            rv_medicin.adapter = medicineListAdapter
            medicineListAdapter?.swapData(listData)
        } else {
            medicineListAdapter?.swapData(listData)
        }
    }
}
