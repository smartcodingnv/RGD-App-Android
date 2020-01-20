package com.rgddev.app.adapter


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rgddev.app.R
import com.rgddev.app.activity.GuardDetailActivity
import com.rgddev.app.responce.contactlist.DataItem

import com.rgddev.app.utils.AppConfig
import kotlinx.android.synthetic.main.layout_single_phone_number.view.*
import org.jetbrains.anko.layoutInflater
import java.util.*


class ContactListAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<ContactListAdapter.FeatureViewHolder>() {
    private var data: List<DataItem> = ArrayList()
    private val mContext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        return FeatureViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contactus, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {

        holder.bind(data[position], context)

        /*    holder.itemView.setOnClickListener(View.OnClickListener {
                val intent = Intent(mContext, GuardDetailActivity::class.java);
                intent.putExtra(AppConfig.EXTRA.GUARDID, data[position].id)
                mContext!!.startActivity(intent)

            })*/





        var phoneNumbers = ""

//        it.telephoneno?.forEach {
//            if (!it.ext.trim().isEmpty() && !it.telephoneno.trim().isEmpty())
//                phoneNumbers += it.telephoneno.trim() + " ext. " + it.ext + "/"
//            else if (!it.telephoneno.trim().isEmpty() && it.ext.trim().isEmpty())
//                phoneNumbers += it.telephoneno.trim() + "/"
//        }
//
//        if (!it.telephoneno!!.isNullOrEmpty()) {
//            phoneNumbers = phoneNumbers.substring(0, phoneNumbers.length - 1)
//
//        }

        data[position].phone?.forEach {
            if (!it.ext.trim().isEmpty() && !it.phone.trim().isEmpty())
                phoneNumbers += it.phone.trim() + " ext. " + it.ext + "/"
            else if (!it.phone.trim().isEmpty() && it.ext.trim().isEmpty())
                phoneNumbers += it.phone.trim() + "/"
        }

        if (!data[position].phone!!.isNullOrEmpty()) {
            phoneNumbers = phoneNumbers.substring(0, phoneNumbers.length - 1)

        }


        val contacts = phoneNumbers.toString().split("/")
        holder.phonelayout!!.removeAllViews()
        for (i in 0..contacts.size - 1) {



            // get image layout
            val phoneLayout = mContext!!.layoutInflater.inflate(R.layout.layout_single_phone_number, null, false)

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
                    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", dialContact, null))
                    mContext!!.startActivity(intent)
                }
            }

            holder.phonelayout!!.addView(phoneLayout)
        }

    }


    fun swapData(data: List<DataItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        public var phonelayout: LinearLayout? = null

        fun bind(
            item: DataItem,
            context: FragmentActivity?
        ) = with(itemView) {


            phonelayout = itemView.findViewById(R.id.layoutPhones)
            itemView.findViewById<TextView>(R.id.txt_location).text = item.location
            itemView.findViewById<TextView>(R.id.txt_address).text = item.address
            itemView.findViewById<TextView>(R.id.txt_email).text = item.email
            itemView.findViewById<TextView>(R.id.txt_opentime).text = item.openingTime

        }
    }

    fun filterList(filterdNames: ArrayList<DataItem>) {
        this.data = filterdNames
        notifyDataSetChanged()
    }


}