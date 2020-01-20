package com.rgddev.app.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.greenspot.app.utils.PreferenceHelper

import com.rgddev.app.R
import com.rgddev.app.adapter.GuardListAdapter
import com.rgddev.app.adapter.InsurancePlanAdapter
import com.rgddev.app.network.ApiClient
import com.rgddev.app.network.ApiInterface
import com.rgddev.app.responce.contactlist.ResponceContactList
import com.rgddev.app.responce.garddetails.ResponceGardDetails
import com.rgddev.app.responce.gardtimetable.DataItem
import com.rgddev.app.responce.gardtimetable.ResponceGardTimeTable
import com.rgddev.app.responce.insuranceplan.ResponceInsurancePlan
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.Progress
import com.rgddev.app.utils.Utils
import kotlinx.android.synthetic.main.fragment_gurd.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GurdFragment : Fragment() {
    private var gardResponce: String=""
    private var gardListdata: List<DataItem>?=null
    // TODO: Rename and change types of parameters
    private lateinit var rv_gurad: RecyclerView

    private var progress: Progress? = null
    private var utils: Utils? = null
    private var helper: PreferenceHelper? = null
    private lateinit var mView: View
    private var mLayoutManager: LinearLayoutManager? = null
    private lateinit var guardListAdapter: GuardListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout



    fun newInstance(): GurdFragment {

        val args = Bundle()
        val fragment = GurdFragment()
        fragment.setArguments(args)
        return fragment

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        progress = Progress(this.activity!!)
        utils = Utils(this.activity!!)
        helper = PreferenceHelper(this.activity!!, AppConfig.PREFERENCE.PREF_FILE)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gurd, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        rv_gurad = mView.findViewById(R.id.rv_gurad)
        swipeRefreshLayout = mView.findViewById(R.id.swipeRefreshLayout)


        val gson = Gson()
        val gardresponce =
            gson.fromJson(
                helper!!.LoadStringPref(AppConfig.PREFERENCE.GARDRESPONCE, ""),
                ResponceGardTimeTable::class.java
            )

        if (gardresponce == null) {
            guardlistdata(AppConfig.URL.TOKEN, "", 1)
        } else {
            gardListdata = gardresponce.data!!
            setgardData()
            guardlistdata(AppConfig.URL.TOKEN, "", 2)
        }

        swipeRefreshLayout.setOnRefreshListener {

            if (gardresponce == null) {
                guardlistdata(AppConfig.URL.TOKEN, "", 1)
            } else {
                gardListdata = gardresponce.data!!
                setgardData()
                guardlistdata(AppConfig.URL.TOKEN, "", 2)
            }

        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {


    }

    private fun guardlistdata(header: String, sync_date: String, flag:Int) {

        progress!!.createDialog(false)
        progress!!.DialogMessage(getString(R.string.please_wait))
        if(flag==1){
            progress!!.showDialog()
        }

        utils!!.hideKeyboard()

        val apiService = ApiClient.client?.create(ApiInterface::class.java)
        val checkBookingResponce = apiService?.CALL_GAURDLIST(
            token = header,
            syncDate = ""
        )

        checkBookingResponce?.enqueue(object : Callback<ResponceGardTimeTable> {
            override fun onResponse(@NonNull call: Call<ResponceGardTimeTable>, @NonNull response: Response<ResponceGardTimeTable>) {
                swipeRefreshLayout.isRefreshing = false

                progress!!.hideDialog()
                val newsResponse = response.body()
                Log.e("codee", " " + response.code())
                if (response.code() == AppConfig.URL.SUCCESS) {
                    if (newsResponse!!.apiStatus == 1) {

                        val gson = Gson()
                        gardResponce = gson.toJson(newsResponse)
                        helper!!.initPref()
                        helper!!.SaveStringPref(AppConfig.PREFERENCE.GARDRESPONCE, gardResponce)
                        helper!!.ApplyPref()
                        gardListdata = newsResponse.data
                        setgardData()


                    } else {

                        if (newsResponse.data == null || newsResponse.data.isEmpty()) {
                            Toast.makeText(
                                activity,
                                newsResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.msg_unexpected_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(@NonNull call: Call<ResponceGardTimeTable>, @NonNull t: Throwable) {
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

    private fun setgardData() {

        mLayoutManager = LinearLayoutManager(activity)
        rv_gurad.setLayoutManager(mLayoutManager)
        guardListAdapter = GuardListAdapter(activity)
        guardListAdapter.swapData(gardListdata!!)
        rv_gurad.adapter = guardListAdapter
    }

}
