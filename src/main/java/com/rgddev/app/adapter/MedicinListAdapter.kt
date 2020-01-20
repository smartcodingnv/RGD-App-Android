package com.rgddev.app.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rgddev.app.R
import com.rgddev.app.responce.insurancedetails.DataItem
import java.util.*


class MedicinListAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<MedicinListAdapter.FeatureViewHolder>() {

    private var data: List<String> = ArrayList()
    private val mContext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        return FeatureViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_insurance, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {

        holder.bind(data[position], context)

//            holder.itemView.setOnClickListener(View.OnClickListener {
//                val intent = Intent(mContext, NewsDetailActivity::class.java);
//                intent.putExtra(AppConfig.EXTRA.NEWSID, data[position].id)
//                intent.putExtra(AppConfig.EXTRA.NEWSCHECK,1)
//                mContext!!.startActivity(intent)
//
//            })


    }


    fun swapData(data: List<String>) {
        this.data = data
        notifyDataSetChanged()
    }

    class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: String,
            context: FragmentActivity?
        ) = with(itemView) {

            itemView.findViewById<TextView>(R.id.txt_insurance).text = item
            itemView.findViewById<ImageView>(R.id.img_medicin).visibility = View.GONE

        }
    }

    fun filterList(filterdNames: ArrayList<String>) {
        this.data = filterdNames
        notifyDataSetChanged()
    }


}