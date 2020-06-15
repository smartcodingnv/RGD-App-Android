package com.rgddev.app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


import com.rgddev.app.R
import com.rgddev.app.activity.NewsDetailActivity
import com.rgddev.app.responce.news.DataItem
import com.rgddev.app.utils.AppConfig
import kotlinx.android.synthetic.main.item_news.view.*
import java.util.*


class NewsListAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<NewsListAdapter.FeatureViewHolder>() {

    private var data: List<DataItem> = ArrayList()
    private val mContext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        return FeatureViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {

        holder.bind(data[position], context)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(mContext, NewsDetailActivity::class.java);
            intent.putExtra(AppConfig.EXTRA.NEWSID, data[position].id)
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
            itemView.findViewById<TextView>(R.id.txt_date).text = item.date
            itemView.findViewById<TextView>(R.id.txt_desc).text = item.description

            Glide.with(itemView)
                .load(item.image!![0].toString())
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .into(itemView.imageView1)


        }
    }


}