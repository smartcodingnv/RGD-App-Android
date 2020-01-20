package com.rgddev.app.adapter


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.greenspot.app.interfaces.ItemClickListener
import com.rgddev.app.R
import com.rgddev.app.responce.lab.DataItem
import kotlinx.android.synthetic.main.layout_single_phone_number.view.*
import org.jetbrains.anko.layoutInflater
import java.util.*


class LabListAdapter(
    val context: FragmentActivity?,
    val itemClickListener: ItemClickListener
):
    RecyclerView.Adapter<LabListAdapter.FeatureViewHolder>() {
    private var data: List<DataItem> = ArrayList()
    private val mContext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        return FeatureViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_provider, parent, false)
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


        var workingTime = ""

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
        holder.workingtimelayout!!.removeAllViews()

        data[position].workingTimes?.forEach {
            //            if (!it.ext.trim().isEmpty() && !it.phone.trim().isEmpty())
//                phoneNumbers += it.phone.trim() + " ext. " + it.ext + "/"
//            else if (!it.phone.trim().isEmpty() && it.ext.trim().isEmpty())
//                phoneNumbers += it.phone.trim() + "/"
            workingTime += it.workingDays + " " + it.workingTime + "/"
        }

        if (!data[position].workingTimes!!.isNullOrEmpty()) {
            workingTime = workingTime.substring(0, workingTime.length - 1)
        }

        val working = workingTime.toString().split("/")

        for (i in 0..working.size - 1) {
            val workingLayout =
                mContext!!.layoutInflater.inflate(R.layout.layout_single_workingtime, null, false)
            if (i == working.size - 1) {
                workingLayout.txtContact.text = working[i]
            } else {
                workingLayout.txtContact.text = working[i] + " | "
            }

            holder.workingtimelayout!!.addView(workingLayout)
        }

        var phoneNumbers = ""

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
            val phoneLayout =
                mContext!!.layoutInflater.inflate(R.layout.layout_single_phone_number, null, false)

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

        holder.itemView.setOnClickListener(View.OnClickListener {
            itemClickListener.recyclerViewListClicked(it, position, data[position].id)
        })
    }


    fun swapData(data: List<DataItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        public var phonelayout: LinearLayout? = null
        public var workingtimelayout: LinearLayout? = null
        fun bind(
            item: DataItem,
            context: FragmentActivity?
        ) = with(itemView) {


            //            phonelayout = itemView.findViewById(R.id.layoutPhones)

            itemView.findViewById<LinearLayout>(R.id.lay_location).visibility = View.VISIBLE
            itemView.findViewById<LinearLayout>(R.id.lay_service).visibility = View.VISIBLE

            phonelayout = itemView.findViewById(R.id.layoutPhones)
            workingtimelayout = itemView.findViewById(R.id.layworkingtime)
            itemView.findViewById<TextView>(R.id.txt_lebellocation).text = context!!.getString(R.string.res_services)
            itemView.findViewById<TextView>(R.id.txt_title).text = item.name
            itemView.findViewById<TextView>(R.id.txt_address).text = item.address
            itemView.findViewById<TextView>(R.id.txt_location).text = item.locationName
            itemView.findViewById<TextView>(R.id.txt_services).text = item.serviceDetail



        }
    }


    fun filterList(filterdNames: ArrayList<DataItem>) {
        this.data = filterdNames
        notifyDataSetChanged()
    }
}