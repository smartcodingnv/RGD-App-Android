package com.rgddev.app.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rgddev.app.interfaces.ItemClickListener
import com.rgddev.app.utils.PreferenceHelper
import com.rgddev.app.R
import com.rgddev.app.activity.ClinicDetailActivity
import com.rgddev.app.responce.clinic.DataItem
import com.rgddev.app.utils.AppConfig
import java.util.*


class ClinicListAdapter(
    val context: FragmentActivity?,
    val itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<ClinicListAdapter.FeatureViewHolder>() {

    private var data: List<DataItem> = ArrayList()
    private val mContext: Context? = context
    private var helper: PreferenceHelper? =
        PreferenceHelper(this.context!!, AppConfig.PREFERENCE.PREF_FILE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        return FeatureViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_clinic, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {

        holder.bind(data[position], context)

//        holder.itemView.setOnClickListener(View.OnClickListener {
//            val intent = Intent(mContext, MedecinListActivity::class.java);
//            intent.putExtra(AppConfig.EXTRA.INSUSRANCEID, data[position].id)
//            intent.putExtra(AppConfig.EXTRA.INSUSRANCETITLE, data[position].name)
//            mContext!!.startActivity(intent)
//
//        })

        holder.title!!.setOnClickListener(View.OnClickListener {
            itemClickListener.recyclerViewListClicked(it, position, data[position].id)
        })

        holder.imgclinic!!.setOnClickListener(View.OnClickListener {

            val intent = Intent(mContext, ClinicDetailActivity::class.java);
            intent.putExtra(AppConfig.EXTRA.CLINICID, data[position].id)
            mContext!!.startActivity(intent)

        })


    }


    fun swapData(data: List<DataItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public var imgclinic: ImageView? = null
        public var title: TextView? = null
        fun bind(
            item: DataItem,
            context: FragmentActivity?
        ) = with(itemView) {

            title = findViewById<TextView>(R.id.txt_insurance)
            imgclinic = findViewById<ImageView>(R.id.img_clinincdetails)
            itemView.findViewById<TextView>(R.id.txt_insurance).text = item.name


        }
    }

    fun filterList(filterdNames: ArrayList<DataItem>) {
        this.data = filterdNames
        notifyDataSetChanged()
    }

}