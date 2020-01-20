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
import com.rgddev.app.activity.NewsDetailActivity
import com.rgddev.app.responce.notificationlist.DataItem
import com.rgddev.app.utils.AppConfig
import java.util.*


class NotificationListAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<NotificationListAdapter.FeatureViewHolder>() {

    private var data: List<DataItem> = ArrayList()
    private val mContext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        return FeatureViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {

        holder.bind(data[position], context)

        holder.itemView.setOnClickListener(View.OnClickListener {

            if (data[position].type.equals("guard_timetable")) {

                val intent = Intent(mContext, GuardDetailActivity::class.java);
                intent.putExtra(AppConfig.EXTRA.GUARDID, data[position].typeId)
                intent.putExtra(AppConfig.EXTRA.CHECKNOTIFICAION, 3)
                mContext!!.startActivity(intent)

            } else if (data[position].type.equals("news")) {

                val intent = Intent(mContext, NewsDetailActivity::class.java);
                intent.putExtra(AppConfig.EXTRA.NEWSID, data[position].typeId)
                intent.putExtra(AppConfig.EXTRA.CHECKNOTIFICAION, 3)
                mContext!!.startActivity(intent)
            }


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
            itemView.findViewById<TextView>(R.id.txt_address).text = item.description

        }
    }


}