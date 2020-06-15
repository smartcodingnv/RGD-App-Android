package com.rgddev.app.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rgddev.app.R
import com.rgddev.app.activity.GuardDetailActivity
import com.rgddev.app.responce.gardtimetable.DataItem
import com.rgddev.app.utils.AppConfig
import java.util.*


class GuardListAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<GuardListAdapter.FeatureViewHolder>() {

    private var data: List<DataItem> = ArrayList()
    private val mContext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        return FeatureViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_guard, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {

        holder.bind(data[position], context)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(mContext, GuardDetailActivity::class.java);
            intent.putExtra(AppConfig.EXTRA.GUARDID, data[position].id)
            intent.putExtra(AppConfig.EXTRA.CHECKNOTIFICAION, 1)
            mContext!!.startActivity(intent)

        })


    }


    fun swapData(data: List<DataItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: DataItem,
            context: FragmentActivity?
        ) = with(itemView) {

            itemView.findViewById<TextView>(R.id.txt_title).text = item.title
            itemView.findViewById<TextView>(R.id.txt_startdate).text = item.startDateTime
            itemView.findViewById<TextView>(R.id.txt_enddate).text = item.endDateTime

        }
    }

    fun filterList(filterdNames: ArrayList<DataItem>) {
        this.data = filterdNames
        notifyDataSetChanged()
    }


}