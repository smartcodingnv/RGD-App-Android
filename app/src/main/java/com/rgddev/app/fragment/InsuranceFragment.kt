package com.rgddev.app.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.rgddev.app.utils.PreferenceHelper
import com.rgddev.app.R
import com.rgddev.app.adapter.InsurancePlanAdapter
import com.rgddev.app.network.ApiClient
import com.rgddev.app.network.ApiInterface
import com.rgddev.app.responce.insuranceplan.DataItem
import com.rgddev.app.responce.insuranceplan.ResponceInsurancePlan
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.Progress
import com.rgddev.app.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InsuranceFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var insuranceResponce: String = ""
    private lateinit var insuranceData: List<DataItem>
    private lateinit var rv_insurance: RecyclerView
    private var progress: Progress? = null
    private var utils: Utils? = null
    private var helper: PreferenceHelper? = null
    private lateinit var mView: View
    private var mLayoutManager: LinearLayoutManager? = null
    private lateinit var insurancePlanAdapter: InsurancePlanAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    fun newInstance(): InsuranceFragment {

        val args = Bundle()
        val fragment = InsuranceFragment()
        fragment.setArguments(args)
        return fragment

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        progress = Progress(this.activity!!)
        utils = Utils(this.activity!!)
        helper = PreferenceHelper(this.activity!!, AppConfig.PREFERENCE.PREF_FILE)
        return inflater.inflate(R.layout.fragment_insurance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        rv_insurance = mView.findViewById(R.id.rv_insurance)
        swipeRefreshLayout = mView.findViewById(R.id.swipeRefreshLayout)


        val gson = Gson()
        val insurancePlan =
            gson.fromJson(
                helper!!.LoadStringPref(AppConfig.PREFERENCE.INSURANCEPLAN, ""),
                ResponceInsurancePlan::class.java
            )

        if (insurancePlan == null) {
            insurancePlan(AppConfig.URL.TOKEN, "", 1)
        } else {
            insuranceData = insurancePlan.data!!
            setInsuranceData()
            insurancePlan(AppConfig.URL.TOKEN, "", 2)
        }

        swipeRefreshLayout.setOnRefreshListener {

            if (insurancePlan == null) {
                insurancePlan(AppConfig.URL.TOKEN, "", 1)
            } else {
                insuranceData = insurancePlan.data!!
                setInsuranceData()
                insurancePlan(AppConfig.URL.TOKEN, "", 2)
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

    private fun insurancePlan(header: String, sync_date: String, check: Int) {

        progress!!.createDialog(false)
        progress!!.DialogMessage(getString(R.string.please_wait))
        if (check == 1) {
            progress!!.showDialog()
        }

        utils!!.hideKeyboard()

        val apiService = ApiClient.client?.create(ApiInterface::class.java)
        val checkBookingResponce = apiService?.CALL_INSURANCEPLAN(
            token = header,
            syncDate = ""
        )

        checkBookingResponce?.enqueue(object : Callback<ResponceInsurancePlan> {
            override fun onResponse(@NonNull call: Call<ResponceInsurancePlan>, @NonNull response: Response<ResponceInsurancePlan>) {
                swipeRefreshLayout.isRefreshing = false

                progress!!.hideDialog()
                val newsResponse = response.body()
                Log.e("codee", " " + response.code())
                if (response.code() == AppConfig.URL.SUCCESS) {
                    if (newsResponse!!.apiStatus == 1) {

                        val gson = Gson()
                        insuranceResponce = gson.toJson(newsResponse)

                        helper!!.initPref()
                        helper!!.SaveStringPref(
                            AppConfig.PREFERENCE.INSURANCEPLAN,
                            insuranceResponce
                        )
                        helper!!.ApplyPref()

                        insuranceData = newsResponse.data!!
                        setInsuranceData()


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

            override fun onFailure(@NonNull call: Call<ResponceInsurancePlan>, @NonNull t: Throwable) {
                swipeRefreshLayout.isRefreshing = false

                progress!!.hideDialog()
                /*  Toast.makeText(
                      activity,
                      getString(R.string.msg_internet_conn),
                      Toast.LENGTH_SHORT
                  ).show()*/
            }
        })

    }

    private fun setInsuranceData() {
        mLayoutManager = LinearLayoutManager(activity)
        rv_insurance.setLayoutManager(mLayoutManager)
        insurancePlanAdapter = InsurancePlanAdapter(activity)
        insurancePlanAdapter.swapData(insuranceData)
        rv_insurance.adapter = insurancePlanAdapter

    }

}
