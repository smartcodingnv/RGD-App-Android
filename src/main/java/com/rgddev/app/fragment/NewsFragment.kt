package com.rgddev.app.fragment

import android.content.Context
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
import com.greenspot.app.utils.PreferenceHelper
import com.rgddev.app.R
import com.rgddev.app.adapter.NewsListAdapter
import com.rgddev.app.network.ApiClient
import com.rgddev.app.network.ApiInterface
import com.rgddev.app.responce.gardtimetable.ResponceGardTimeTable
import com.rgddev.app.responce.news.DataItem
import com.rgddev.app.responce.news.ResponceNews
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.Progress
import com.rgddev.app.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var newsListdata: List<DataItem>?=null
    private var newsResponce: String=""
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var rv_news: RecyclerView
    private lateinit var newslistadapter: NewsListAdapter
    private var progress: Progress? = null
    private var utils: Utils? = null
    private var helper: PreferenceHelper? = null
    private lateinit var mView: View
    private var mLayoutManager: LinearLayoutManager? = null

    //    private val upcomingBookingList: LinkedList<RecordsItem> =
//        LinkedList<RecordsItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    fun newInstance(): NewsFragment {

        val args = Bundle()

        val fragment = NewsFragment()
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


        return inflater.inflate(R.layout.fragment_news, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        rv_news = mView.findViewById(R.id.rv_news)
        swipeRefreshLayout = mView.findViewById(R.id.swipeRefreshLayout)


        val gson = Gson()
        val newsresponce =
            gson.fromJson(
                helper!!.LoadStringPref(AppConfig.PREFERENCE.NEWSRESPONCE, ""),
                ResponceNews::class.java
            )

        if (newsresponce == null) {
            upcomingNews(AppConfig.URL.TOKEN, "", 1)
        } else {
            newsListdata = newsresponce.data!!
            setNewsData()
            upcomingNews(AppConfig.URL.TOKEN, "", 2)
        }

        swipeRefreshLayout.setOnRefreshListener {

            if (newsresponce == null) {
                upcomingNews(AppConfig.URL.TOKEN, "", 1)
            } else {
                newsListdata = newsresponce.data!!
                setNewsData()
                upcomingNews(AppConfig.URL.TOKEN, "", 2)
            }

        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }


    private fun upcomingNews(header: String, sync_date: String, flag:Int) {

        progress!!.createDialog(false)
        progress!!.DialogMessage(getString(R.string.please_wait))
        if(flag==1){
            progress!!.showDialog()
        }


        utils!!.hideKeyboard()

        val apiService = ApiClient.client?.create(ApiInterface::class.java)
        val checkBookingResponce = apiService?.CALL_NEWS(
            token = header,
            syncDate = ""
        )

        checkBookingResponce?.enqueue(object : Callback<ResponceNews> {
            override fun onResponse(@NonNull call: Call<ResponceNews>, @NonNull response: Response<ResponceNews>) {
                swipeRefreshLayout.isRefreshing = false
                progress!!.hideDialog()
                val newsResponse = response.body()
                Log.e("codee", " " + response.code())
                if (response.code() == AppConfig.URL.SUCCESS) {
                    if (newsResponse!!.apiStatus == 1) {
                        val gson = Gson()
                        newsResponce = gson.toJson(newsResponse)
                        helper!!.initPref()
                        helper!!.SaveStringPref(AppConfig.PREFERENCE.NEWSRESPONCE, newsResponce)
                        helper!!.ApplyPref()
                        newsListdata = newsResponse.data

                        setNewsData()




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

            override fun onFailure(@NonNull call: Call<ResponceNews>, @NonNull t: Throwable) {
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

    private fun setNewsData() {
        mLayoutManager = LinearLayoutManager(activity)
        rv_news.setLayoutManager(mLayoutManager)
        newslistadapter = NewsListAdapter(activity)
        newslistadapter.swapData(this.newsListdata!!)
        rv_news.adapter = newslistadapter

    }


    override fun onDestroy() {
        super.onDestroy()


    }


}
