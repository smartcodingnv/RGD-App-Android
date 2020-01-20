package com.rgddev.app.activity

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.greenspot.app.utils.PreferenceHelper
import com.rgddev.app.R
import com.rgddev.app.adapter.MedicinListAdapter
import com.rgddev.app.network.ApiClient
import com.rgddev.app.network.ApiInterface
import com.rgddev.app.responce.insurancedetails.DataItem
import com.rgddev.app.responce.insurancedetails.ResponceMedicnList
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.Progress
import com.rgddev.app.utils.Utils
import kotlinx.android.synthetic.main.activity_medecin_list.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class MedecinListActivity : AppCompatActivity() {
    private lateinit var hashMap: HashMap<String, Any>
    private lateinit var medicinList: List<DataItem>

    var idList = java.util.ArrayList<String>()
    private var id: Int = 0
    private var institle: String = ""
    private lateinit var medicinlistAdapter: MedicinListAdapter
    private var progress: Progress? = null
    private var utils: Utils? = null
    private var helper: PreferenceHelper? = null
    private lateinit var rv_medicin: RecyclerView
    private lateinit var et_search: EditText
    private var mLayoutManager: LinearLayoutManager? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medecin_list)
        setSupportActionBar(toolbar)
        progress = Progress(this)
        utils = Utils(this)
        helper = PreferenceHelper(this, AppConfig.PREFERENCE.PREF_FILE)

        rv_medicin = findViewById(R.id.rv_medicin)
        et_search = findViewById(R.id.et_search)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        val intenttt = getIntent()
        hashMap = HashMap<String, Any>()
        id = intenttt.getIntExtra(AppConfig.EXTRA.INSUSRANCEID, 0)
        institle = intenttt.getStringExtra(AppConfig.EXTRA.INSUSRANCETITLE)
        txt_title.text = institle



        val gson = Gson()
        val insurancePlan = helper!!.LoadStringPref(AppConfig.PREFERENCE.MEDICINLIST, "")
        if(insurancePlan!!.isNotEmpty()){

//            val fooType  = object : TypeToken<DataItem>() {}.type
//            gson.toJson(foo, fooType)
//            gson.fromJson(json, fooType)

            val type = object : TypeToken<HashMap<String?, Any?>?>() {}.type
            hashMap = gson.fromJson(insurancePlan, type)

        }

        hashMap.forEach {
                k, v ->
            println("$k = $v")
            Log.e("medicinlist"," "+ k  )
            if(id.toString().equals(k)){


                Log.e("medicinlist" ,"  "+ v)



                idList = v as java.util.ArrayList<String>


//                medicinList = v


//
                setMedicinListData()

            }
        }


        initview()


//        insurancePlan.data!!.mapIndexed { index, dataItem ->
//
//            if(dataItem.id.equals(id)){
//
//
//            }
//
//        }




        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) { //after the change calling the method and passing the search input
                filter(editable.toString())
            }
        })



    }



    private fun initview() {

        getMedicinList(AppConfig.URL.TOKEN, "", id.toString())
    }

    private fun getMedicinList(header: String, sync_date: String, id: String) {

        progress!!.createDialog(false)
        progress!!.DialogMessage(getString(R.string.please_wait))
        progress!!.showDialog()
        utils!!.hideKeyboard()

        val apiService = ApiClient.client?.create(ApiInterface::class.java)
        val checkBookingResponce = apiService?.CALL_INSURANCEPLANDETAILS(
            token = header,
            syncDate = "",
            id = id
        )

        checkBookingResponce?.enqueue(object : Callback<ResponceMedicnList> {
            override fun onResponse(@NonNull call: Call<ResponceMedicnList>, @NonNull response: Response<ResponceMedicnList>) {
//                swipeRefreshLayout.isRefreshing = false

                progress!!.hideDialog()
                val newsResponse = response.body()
                Log.e("codee", " " + response.code())
                if (response.code() == AppConfig.URL.SUCCESS) {
                    if (newsResponse!!.apiStatus == 1) {

                        medicinList = newsResponse.data!!

                        for(name in medicinList){

                            idList.add(name.name)

                        }

                        hashMap.put(id, idList)


                        val gson = Gson()
                        val hashMapString = gson.toJson(hashMap)



                        helper!!.initPref()
                        helper!!.SaveStringPref(AppConfig.PREFERENCE.MEDICINLIST, hashMapString)
                        helper!!.ApplyPref()


                        setMedicinListData()



                    } else {

                        if (newsResponse.data == null || newsResponse.data.isEmpty()) {
                            Toast.makeText(
                                this@MedecinListActivity,
                                newsResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                } else {
                    Toast.makeText(
                        this@MedecinListActivity,
                        getString(R.string.msg_unexpected_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(@NonNull call: Call<ResponceMedicnList>, @NonNull t: Throwable) {
//                swipeRefreshLayout.isRefreshing = false

                progress!!.hideDialog()
//                Toast.makeText(
//                    this@MedecinListActivity,
//                    getString(R.string.msg_internet_conn),
//                    Toast.LENGTH_SHORT
//                ).show()

            }
        })

    }

    private fun setMedicinListData() {

        mLayoutManager = LinearLayoutManager(this@MedecinListActivity)
        rv_medicin.setLayoutManager(mLayoutManager)
        medicinlistAdapter = MedicinListAdapter(this@MedecinListActivity)

        medicinlistAdapter.swapData(idList)
        rv_medicin.adapter = medicinlistAdapter
    }

    private fun filter(text: String) {
        val filterdNames: ArrayList<String> = ArrayList()

        for (s in idList) { //if the existing elements contains the search input

            if (s.toLowerCase().contains(text.toLowerCase())) { //adding the element to filtered list
                filterdNames.add(s)
            }
        }
        //calling a method of the adapter class and passing the filtered list
        medicinlistAdapter.filterList(filterdNames)
    }


}
