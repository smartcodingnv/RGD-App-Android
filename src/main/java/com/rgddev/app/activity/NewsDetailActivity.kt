package com.rgddev.app.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.greenspot.app.utils.Common
import com.greenspot.app.utils.PreferenceHelper
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView
import com.rgddev.app.R
import com.rgddev.app.model.GalleryImgList
import com.rgddev.app.network.ApiClient
import com.rgddev.app.network.ApiInterface
import com.rgddev.app.responce.news.DataItem
import com.rgddev.app.responce.news.ResponceNews
import com.rgddev.app.responce.newsdetails.ResponceNewsDetails
import com.rgddev.app.responce.notificationlist.ResponceNotification
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.Progress
import com.rgddev.app.utils.Utils
import it.sephiroth.android.library.imagezoom.ImageViewTouch
import kotlinx.android.synthetic.main.activity_news_detail.*
import kotlinx.android.synthetic.main.content_news_detail.*
import org.jetbrains.anko.imageBitmap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class NewsDetailActivity : AppCompatActivity() {

    private var notificationnewsData: List<com.rgddev.app.responce.notificationlist.DataItem>? =
        null
    private var newsData: List<DataItem>? = null
    private var id: Int = 0
    private var progress: Progress? = null
    private var utils: Utils? = null
    private var helper: PreferenceHelper? = null

    private var gallaryImageList: ArrayList<String> = ArrayList()
    private var gallaryVidoList: ArrayList<String> = ArrayList()
    private var gallaryCombineList: ArrayList<GalleryImgList> = ArrayList()
    var count: Int = 0
    private var imgURl: String? = ""
    private var videoId: String? = ""
    private var videoLink: String? = ""





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        setSupportActionBar(toolbar)



        progress = Progress(this)
        utils = Utils(this)

        helper = PreferenceHelper(this, AppConfig.PREFERENCE.PREF_FILE)


        val intenttt = getIntent()

        id = intenttt.getIntExtra(AppConfig.EXTRA.NEWSID, 0)
        val checknoti = intenttt.getIntExtra(AppConfig.EXTRA.CHECKNOTIFICAION, 0)


        val img_notification: ImageView = findViewById(R.id.img_notification)

        if(Utils.isInternetAvailable(this)){
            lay_imgaeview.visibility = View.VISIBLE
        }else{
            lay_imgaeview.visibility = View.GONE
        }

        img_notification.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, NotificationListActivity::class.java);
            startActivity(intent)

        })

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
        Log.e("checkkk", " " + id)

        if (checknoti == 1) {
            val gson = Gson()
            val newsresponce =
                gson.fromJson(
                    helper!!.LoadStringPref(AppConfig.PREFERENCE.NEWSRESPONCE, ""),
                    ResponceNews::class.java
                )

            newsData = newsresponce.data
            newsData!!.mapIndexed { index, dataItem ->
                if (id.equals(dataItem.id)) {
                    txt_title.text = newsresponce.data!![index].title
                    txt_desc.text = newsresponce.data!![index].description
                    txt_date.text = newsresponce.data!![index].date
                    if (newsresponce.data!![index].image != null) {
                        gallaryImageList = newsresponce.data!![index].image as ArrayList<String>
                    }


                    if (newsresponce.data!![index].vides != null) {
                        gallaryVidoList = newsresponce.data!![index].vides as ArrayList<String>
                    }


                    for (image in gallaryImageList) {

                        gallaryCombineList.add(
                            GalleryImgList(
                                image,
                                2
                            )
                        )
                    }
                    if (newsresponce.data!![index].vides != null) {
                        for (video in gallaryVidoList) {

                            gallaryCombineList.add(
                                GalleryImgList(
                                    video,
                                    1
                                )
                            )
                        }
                    }

                    if (0 != gallaryCombineList.size - 1) {
                        lay_next.visibility = View.VISIBLE
                        lay_prev.visibility = View.VISIBLE
                    } else {
                        lay_next.visibility = View.GONE
                        lay_prev.visibility = View.GONE
                    }

                    Glide.with(this@NewsDetailActivity)
                        .load(gallaryCombineList[0].item_name.toString())
                        .placeholder(R.drawable.ic_placeholder)
                        .centerCrop()
                        .into(img_place)

                    imgURl = gallaryCombineList[0].item_name


                }

            }

        } else if (checknoti == 2) {

            initview()

        } else if (checknoti == 3) {

            val gson = Gson()
            val newsresponce =
                gson.fromJson(
                    helper!!.LoadStringPref(AppConfig.PREFERENCE.NOTIFICATIONRESPONCE, ""),
                    ResponceNotification::class.java
                )

            notificationnewsData = newsresponce.data
            notificationnewsData!!.mapIndexed { index, dataItem ->
                if (id.equals(dataItem.typeId)) {
                    txt_title.text = newsresponce.data!![index].news.title
                    txt_desc.text = newsresponce.data!![index].news.description
                    txt_date.text = newsresponce.data!![index].news.date
                    if (newsresponce.data!![index].news.image != null) {
                        gallaryImageList =
                            newsresponce.data!![index].news.image as ArrayList<String>
                    }


                    if (newsresponce.data!![index].news.vides != null) {
                        gallaryVidoList = newsresponce.data!![index].news.vides as ArrayList<String>
                    }


                    for (image in gallaryImageList) {

                        gallaryCombineList.add(
                            GalleryImgList(
                                image,
                                2
                            )
                        )
                    }
                    if (newsresponce.data!![index].news.vides != null) {
                        for (video in gallaryVidoList) {

                            gallaryCombineList.add(
                                GalleryImgList(
                                    video,
                                    1
                                )
                            )
                        }
                    }

                    if (0 != gallaryCombineList.size - 1) {
                        lay_next.visibility = View.VISIBLE
                        lay_prev.visibility = View.VISIBLE
                    } else {
                        lay_next.visibility = View.GONE
                        lay_prev.visibility = View.GONE
                    }

                    Glide.with(this@NewsDetailActivity)
                        .load(gallaryCombineList[0].item_name.toString())
                        .placeholder(R.drawable.ic_placeholder)
                        .centerCrop()
                        .into(img_place)

                    imgURl = gallaryCombineList[0].item_name


                }

            }
        }





        lay_prev.setOnClickListener(View.OnClickListener {
            if (count == 0) {
                return@OnClickListener
            }
            count--

            if (gallaryCombineList[count].flag == 1) {

                img_play.visibility = View.VISIBLE
                videoId = extractYoutubeId(gallaryCombineList[count].item_name);
                imgURl = "http://img.youtube.com/vi/" + videoId + "/0.jpg"
                videoLink = gallaryCombineList[count].item_name
                Glide.with(this)
                    .load(imgURl)
                    .placeholder(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(img_place)

            } else {

                img_play.visibility = View.GONE
                imgURl = gallaryCombineList[count].item_name
                Glide.with(this)
                    .load(imgURl)
                    .placeholder(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(img_place)
            }

            Log.e("prev", " " + count)

        })

        lay_next.setOnClickListener(View.OnClickListener {
            val position = gallaryCombineList.size - 1
            if (position == count) {
                if (gallaryCombineList[position].flag == 1) {

                    img_play.visibility = View.VISIBLE
                    videoId = extractYoutubeId(gallaryCombineList[position].item_name);
                    imgURl = "http://img.youtube.com/vi/" + videoId + "/0.jpg"
                    videoLink = gallaryCombineList[position].item_name
                    Glide.with(this)
                        .load(imgURl)
                        .placeholder(R.drawable.ic_placeholder)
                        .centerCrop()
                        .into(img_place)
                } else {

                    img_play.visibility = View.GONE
                    videoLink = ""
                    imgURl = gallaryCombineList[position].item_name
                    Glide.with(this)
                        .load(imgURl)
                        .placeholder(R.drawable.ic_placeholder)
                        .centerCrop()
                        .into(img_place)
                }

                return@OnClickListener
            }

            count++

            if (gallaryCombineList[count].flag == 1) {

                img_play.visibility = View.VISIBLE
                videoId = extractYoutubeId(gallaryCombineList[count].item_name);
                imgURl = "http://img.youtube.com/vi/" + videoId + "/0.jpg"
                videoLink = gallaryCombineList[count].item_name
                Glide.with(this@NewsDetailActivity)
                    .load(imgURl)
                    .placeholder(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(img_place)
            } else {

                img_play.visibility = View.GONE
                videoLink = ""
                imgURl = gallaryCombineList[count].item_name
                Glide.with(this@NewsDetailActivity)
                    .load(imgURl)
                    .placeholder(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(img_place)
            }

            Log.e("next", " " + count)


        })


        img_place.setOnClickListener(View.OnClickListener {

            if (gallaryCombineList[count].flag == 1) {
                showLargeImage(context = this, url = videoLink, type = "V")
            } else {

                showLargeImage(context = this, url = imgURl, type = "I")
            }


        })
    }

    private fun initview() {

        newsDetails(AppConfig.URL.TOKEN, "", id.toString())
    }

    private fun newsDetails(header: String, sync_date: String, id: String) {

        progress!!.createDialog(false)
        progress!!.DialogMessage(getString(R.string.please_wait))
        progress!!.showDialog()
        utils!!.hideKeyboard()

        val apiService = ApiClient.client?.create(ApiInterface::class.java)
        val checkBookingResponce = apiService?.CALL_NEWSDETAILS(
            token = header,
            syncDate = "",
            id = id

        )

        checkBookingResponce?.enqueue(object : Callback<ResponceNewsDetails> {
            override fun onResponse(@NonNull call: Call<ResponceNewsDetails>, @NonNull response: Response<ResponceNewsDetails>) {

                progress!!.hideDialog()
                val newsResponse = response.body()
                Log.e("codee", " " + response.code())
                if (response.code() == AppConfig.URL.SUCCESS) {
                    if (newsResponse!!.apiStatus == 1) {

                        txt_title.text = newsResponse.data.title
                        txt_desc.text = newsResponse.data.description
                        txt_date.text = newsResponse.data.date
                        if (newsResponse.data.image != null) {
                            gallaryImageList = newsResponse.data.image as ArrayList<String>
                        }


                        if (newsResponse.data.video != null) {
                            gallaryVidoList = newsResponse.data.video as ArrayList<String>
                        }


                        for (image in gallaryImageList) {

                            gallaryCombineList.add(
                                GalleryImgList(
                                    image,
                                    2
                                )
                            )
                        }
                        if (newsResponse.data.video != null) {
                            for (video in gallaryVidoList) {

                                gallaryCombineList.add(
                                    GalleryImgList(
                                        video,
                                        1
                                    )
                                )
                            }
                        }

                        if (0 != gallaryCombineList.size - 1) {
                            lay_next.visibility = View.VISIBLE
                            lay_prev.visibility = View.VISIBLE
                        } else {
                            lay_next.visibility = View.GONE
                            lay_prev.visibility = View.GONE
                        }

                        Glide.with(this@NewsDetailActivity)
                            .load(gallaryCombineList[0].item_name.toString())
                            .placeholder(R.drawable.ic_placeholder)
                            .centerCrop()
                            .into(img_place)

                        imgURl = gallaryCombineList[0].item_name


                    } else {


                        Toast.makeText(
                            this@NewsDetailActivity,
                            newsResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                } else {
                    Toast.makeText(
                        this@NewsDetailActivity,
                        getString(R.string.msg_unexpected_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(@NonNull call: Call<ResponceNewsDetails>, @NonNull t: Throwable) {
                progress!!.hideDialog()
//                Toast.makeText(
//                    this@NewsDetailActivity,
//                    getString(R.string.msg_internet_conn),
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        })

    }

    fun extractYoutubeId(url: String?): String? {
        var id: String? = null


        val regex =
            "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";

        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        val matcher = pattern.matcher(url);
        if (matcher.find()) {
            id = matcher.group(1);
            Log.e("youtubeid", " " + id)
        }
        return id

    }

    fun showLargeImage(
        context: Context,
        url: String? = null,
        bitmap: Bitmap? = null,
        type: String
    ) {
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(this.layoutInflater.inflate(R.layout.layout_large_video, null))

        val btnClose = dialog.findViewById<ImageButton>(R.id.btnIvClose)
        val ivPreview = dialog.findViewById<ImageViewTouch>(R.id.iv_preview_image)
        val youtubeView = dialog.findViewById<YouTubePlayerView>(R.id.youtubeView)
        var youTubePlayer: YouTubePlayer? = null

//
        if (bitmap != null) {
            youtubeView.visibility = View.VISIBLE
            ivPreview.imageBitmap = bitmap
        }

        if (type == "I" && bitmap == null) {
            Log.e("url", " " + url)
            youtubeView.visibility = View.GONE
            btnClose.visibility = View.VISIBLE
            ivPreview.visibility = View.VISIBLE
            Glide.with(context).load(url).into(ivPreview)
        } else if (type == "V") {
            ivPreview.visibility = View.GONE
            youtubeView.visibility = View.VISIBLE
            btnClose.visibility = View.VISIBLE
            youtubeView.initialize({
                it.addListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady() {
                        youTubePlayer = it
                        it.loadVideo(Common.getYoutubeId(url!!)!!, 0f)
                    }
                })
            }, true)
            youtubeView.enterFullScreen()
            youtubeView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
                override fun onYouTubePlayerEnterFullScreen() {}

                override fun onYouTubePlayerExitFullScreen() {
                    dialog.dismiss()
                }
            })
        }

        btnClose.setOnClickListener {
            youtubeView.release();
            dialog.dismiss()
            youTubePlayer?.pause()
            youTubePlayer = null
        }

        dialog.setOnDismissListener {
            youtubeView.release();
            youTubePlayer?.pause()
            youTubePlayer = null
        }


        dialog.show()
    }




}
