package com.rgddev.app.adapter


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.greenspot.app.interfaces.ItemClickListener
import com.rgddev.app.R
import com.rgddev.app.activity.ClinicDetailActivity
import com.rgddev.app.responce.clinic.ServicesItem
import com.rgddev.app.utils.AppConfig
import kotlinx.android.synthetic.main.layout_single_phone_number.view.*
import org.jetbrains.anko.layoutInflater
import java.util.*


class ClinicDetailsAdapter(
    val context: FragmentActivity?
) :
    RecyclerView.Adapter<ClinicDetailsAdapter.FeatureViewHolder>() {

    private var data: List<ServicesItem> = ArrayList()
    private val mContext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        return FeatureViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_clinicservice, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {

        holder.bind(data[position], context)
        val services = data[position]

//        holder.itemView.setOnClickListener(View.OnClickListener {
//            val intent = Intent(mContext, MedecinListActivity::class.java);
//            intent.putExtra(AppConfig.EXTRA.INSUSRANCEID, data[position].id)
//            intent.putExtra(AppConfig.EXTRA.INSUSRANCETITLE, data[position].name)
//            mContext!!.startActivity(intent)
//
//        })


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
        holder.phonelayout!!.removeAllViews()

        data[position].workingTimes?.forEach {
            //            if (!it.ext.trim().isEmpty() && !it.phone.trim().isEmpty())
//                phoneNumbers += it.phone.trim() + " ext. " + it.ext + "/"
//            else if (!it.phone.trim().isEmpty() && it.ext.trim().isEmpty())
//                phoneNumbers += it.phone.trim() + "/"
            workingTime += it.workingDays + " " + it.workingHours + "/"
        }

        if (!data[position].workingTimes!!.isNullOrEmpty()) {
            workingTime = workingTime.substring(0, workingTime.length - 1)
        }

        val working = workingTime.toString().split("/")

        for (i in 0..working.size - 1) {
            val workingLayout =
                mContext!!.layoutInflater.inflate(R.layout.layout_single_workingtime, null, false)
            workingLayout.txtContact.setTextColor(ContextCompat.getColor(mContext, R.color.black))
            if (i == working.size - 1) {
                workingLayout.txtContact.text = working[i]
                Log.e("data", "  "+working[i])
            } else {
                workingLayout.txtContact.text = working[i] + " | "
                Log.e("dataaa", "  "+working[i])
            }

            Log.e("data", "  "+working[i])
            holder.phonelayout!!.addView(workingLayout)
        }


/*        var workingTime = ""


        holder.phonelayout!!.removeAllViews()

        data[position].workingTimes?.forEach {
            //            if (!it.ext.trim().isEmpty() && !it.phone.trim().isEmpty())
//                phoneNumbers += it.phone.trim() + " ext. " + it.ext + "/"
//            else if (!it.phone.trim().isEmpty() && it.ext.trim().isEmpty())
//                phoneNumbers += it.phone.trim() + "/"
            workingTime += it.workingDays + " " + it.workingHours + "/"
        }

        if (!data[position].workingTimes!!.isNullOrEmpty()) {
            workingTime = workingTime.substring(0, workingTime.length - 1)
        }

        val contacts = workingTime.toString().split("/")

        for (i in 0..contacts.size - 1) {
            val phoneLayout =
                mContext!!.layoutInflater.inflate(R.layout.layout_single_workingtime, null, false)
            phoneLayout.txtContact.setTextColor(ContextCompat.getColor(mContext, R.color.black))
            if (i == contacts.size - 1) {
                phoneLayout.txtContact.text = contacts[i]
            } else {
                phoneLayout.txtContact.text = contacts[i] + " | "
            }

            holder.phonelayout!!.addView(phoneLayout)
        }*/


        holder.itemView.setOnClickListener(View.OnClickListener {

            val expanded = services.isExpanded()
            services.setExpanded(!expanded)
            notifyItemChanged(position)
        })



        /*     holder.imgclinic!!.setOnClickListener(View.OnClickListener {



                 val intent = Intent(mContext, ClinicDetailActivity::class.java);
                 intent.putExtra(AppConfig.EXTRA.CLINICID, data[position].id)
                 intent.putExtra(AppConfig.EXTRA.CLNICNAME, data[position].name)
                 intent.putExtra(AppConfig.EXTRA.CLNICADDRESS, data[position].address)
                 intent.putExtra(AppConfig.EXTRA.CLNICPHONENO, data[position].phone.toString())
                 intent.putExtra(AppConfig.EXTRA.CLNICSERVICE, data[position].services.toString())
                 mContext!!.startActivity(intent)
             })*/


    }


    fun swapData(data: List<ServicesItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public var phonelayout: LinearLayout? = null
        fun bind(
            item: ServicesItem,
            context: FragmentActivity?
        ) = with(itemView) {

            val title: TextView
            val layDetails: LinearLayout

            val expanded = item.isExpanded()
            title = itemView.findViewById(R.id.txt_title)
            layDetails = itemView.findViewById(R.id.lay_detailsclinic)
            layDetails.visibility = if (expanded) View.VISIBLE else View.GONE

            if (expanded) {

                title.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, ContextCompat.getDrawable(
                        context!!, R.drawable.ic_arrow_up
                    ), null
                )
            } else {

                title.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, ContextCompat.getDrawable(
                        context!!, R.drawable.ic_arrow_down
                    ), null
                )
            }
            title.setText(item.name)

            phonelayout = itemView.findViewById(R.id.layoutPhones)
            itemView.findViewById<TextView>(R.id.txt_services).text = item.serviceDetail
            itemView.findViewById<TextView>(R.id.txt_name).text = item.providers.toString().replace("[", "").replace("]", "")

        }

    }



}