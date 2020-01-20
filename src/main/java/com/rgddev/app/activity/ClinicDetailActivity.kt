package com.rgddev.app.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.greenspot.app.utils.PreferenceHelper
import com.rgddev.app.R
import com.rgddev.app.adapter.ClinicDetailsAdapter
import com.rgddev.app.responce.clinic.ClinicResponce
import com.rgddev.app.responce.clinic.DataItem
import com.rgddev.app.responce.clinic.ServicesItem
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.Progress
import com.rgddev.app.utils.Utils
import kotlinx.android.synthetic.main.activity_clinic_detail.*
import kotlinx.android.synthetic.main.content_clinic_detail.*
import kotlinx.android.synthetic.main.layout_single_phone_number.view.*
import org.jetbrains.anko.layoutInflater


class ClinicDetailActivity : AppCompatActivity() {


    private  var clininservice: ArrayList<ServicesItem> = ArrayList()
    private var clinincData: List<DataItem>? = null
    private lateinit var clinicDetailsAdapter: ClinicDetailsAdapter


    private var mLayoutManager: LinearLayoutManager? = null

    private var progress: Progress? = null
    private var utils: Utils? = null
    private var helper: PreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clinic_detail)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        val img_notification: ImageView = findViewById(R.id.img_notification)

        img_notification.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, NotificationListActivity::class.java);
            startActivity(intent)

        })

        progress = Progress(this)
        utils = Utils(this)
        helper = PreferenceHelper(this, AppConfig.PREFERENCE.PREF_FILE)

        val intenttt = getIntent()
        val id = intenttt.getIntExtra(AppConfig.EXTRA.CLINICID, 0)

        val gson = Gson()
        val clinicResponce =
            gson.fromJson(
                helper!!.LoadStringPref(AppConfig.PREFERENCE.CLINICRESPONCE, ""),
                ClinicResponce::class.java
            )

        clinincData = clinicResponce.data


        clinincData!!.mapIndexed { index, dataItem ->

            if (id.equals(dataItem.id)) {
                txt_clinicname.text = clinincData!![index].name
                txt_address.text = clinincData!![index].address

                var phoneNumbers = ""

                clinincData!![index].phone?.forEach {
                    if (!it.ext.trim().isEmpty() && !it.phone.trim().isEmpty())
                        phoneNumbers += it.phone.trim() + " ext. " + it.ext + "/"
                    else if (!it.phone.trim().isEmpty() && it.ext.trim().isEmpty())
                        phoneNumbers += it.phone.trim() + "/"
                }

                if (!clinincData!![index].phone!!.isNullOrEmpty()) {
                    phoneNumbers = phoneNumbers.substring(0, phoneNumbers.length - 1)

                }


                val contacts = phoneNumbers.toString().split("/")
                layoutPhones.removeAllViews()
                for (i in 0..contacts.size - 1) {


                    // get image layout
                    val phoneLayout =
                        layoutInflater.inflate(R.layout.layout_single_phone_number, null, false)

                    if (i == contacts.size - 1) {
                        phoneLayout.txtContact.text = contacts[i]
                    } else {
                        phoneLayout.txtContact.text = contacts[i] + ","
                    }

                    phoneLayout.txtContact.setOnClickListener {
                        if (contacts[i].isNotEmpty()) {
                            val dialContact = if (contacts[i].contains("ext")) {
                                contacts[i].substringBefore("ext")
                            } else {
                                contacts[i]
                            }
                            val intent =
                                Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", dialContact, null))
                            startActivity(intent)
                        }
                    }

                    layoutPhones!!.addView(phoneLayout)
                }


                if(clinincData!![index].services!=null && !clinincData!![index].services!!.isEmpty())
                {

                    clininservice = (clinincData!![index].services as ArrayList<ServicesItem>?)!!

                    if(clininservice.isNotEmpty()){
                        mLayoutManager = LinearLayoutManager(this)
                        rv_clinicservice.setLayoutManager(mLayoutManager)
                        clinicDetailsAdapter = ClinicDetailsAdapter(this)
                        clinicDetailsAdapter.swapData(clinincData!![index].services!!)
                        rv_clinicservice.adapter = clinicDetailsAdapter
                    }
                }


            }

        }


    }

}
