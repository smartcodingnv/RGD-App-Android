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
import com.rgddev.app.adapter.ContactListAdapter
import com.rgddev.app.network.ApiClient
import com.rgddev.app.network.ApiInterface
import com.rgddev.app.responce.contactlist.DataItem
import com.rgddev.app.responce.contactlist.ResponceContactList
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.Progress
import com.rgddev.app.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ContactusFragment : Fragment() {

    private var contactResponce: String = ""
    private var contactListdata: List<DataItem>? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var rv_contacus: RecyclerView
    private lateinit var contactListAdapter: ContactListAdapter
    private var progress: Progress? = null
    private var utils: Utils? = null
    private var helper: PreferenceHelper? = null
    private lateinit var mView: View
    private var mLayoutManager: LinearLayoutManager? = null


    fun newInstance(): ContactusFragment {

        val args = Bundle()
        val fragment = ContactusFragment()
        fragment.setArguments(args)
        return fragment

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        progress = Progress(this.activity!!)
        utils = Utils(this.activity!!)
        helper = PreferenceHelper(this.activity!!, AppConfig.PREFERENCE.PREF_FILE)
        return inflater.inflate(R.layout.fragment_contactus, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        rv_contacus = mView.findViewById(R.id.rv_contacus)
        swipeRefreshLayout = mView.findViewById(R.id.swipeRefreshLayout)


        val gson = Gson()
        val contactList =
            gson.fromJson(
                helper!!.LoadStringPref(AppConfig.PREFERENCE.CONTACTUS, ""),
                ResponceContactList::class.java
            )



        if (contactList == null) {
            contactUSData(AppConfig.URL.TOKEN, "", 1)
        } else {
            contactListdata = contactList.data!!
            setContactList()
            contactUSData(AppConfig.URL.TOKEN, "", 2)
        }

        swipeRefreshLayout.setOnRefreshListener {

            if (contactList == null) {
                contactUSData(AppConfig.URL.TOKEN, "", 1)
            } else {
                contactListdata = contactList.data!!
                setContactList()
                contactUSData(AppConfig.URL.TOKEN, "", 2)
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

    private fun contactUSData(header: String, sync_date: String, flag: Int) {

        progress!!.createDialog(false)
        progress!!.DialogMessage(getString(R.string.please_wait))
        if (flag == 1) {
            progress!!.showDialog()
        }

        utils!!.hideKeyboard()

        val apiService = ApiClient.client?.create(ApiInterface::class.java)
        val contactList = apiService?.CALL_CONTACTLIST(
            token = header,
            syncDate = ""
        )

        contactList?.enqueue(object : Callback<ResponceContactList> {
            override fun onResponse(@NonNull call: Call<ResponceContactList>, @NonNull response: Response<ResponceContactList>) {
                swipeRefreshLayout.isRefreshing = false
                progress!!.hideDialog()
                val newsResponse = response.body()
                Log.e("codee", " " + response.code())
                if (response.code() == AppConfig.URL.SUCCESS) {
                    if (newsResponse!!.apiStatus == 1) {

                        val gson = Gson()
                        contactResponce = gson.toJson(newsResponse)

                        helper!!.initPref()
                        helper!!.SaveStringPref(AppConfig.PREFERENCE.CONTACTUS, contactResponce)
                        helper!!.ApplyPref()

                        contactListdata = newsResponse.data

                        setContactList()


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

            override fun onFailure(@NonNull call: Call<ResponceContactList>, @NonNull t: Throwable) {
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

    private fun setContactList() {

        mLayoutManager = LinearLayoutManager(activity)
        rv_contacus.setLayoutManager(mLayoutManager)
        contactListAdapter = ContactListAdapter(activity)
        contactListAdapter.swapData(contactListdata!!)
        rv_contacus.adapter = contactListAdapter
    }
}
