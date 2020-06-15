package com.rgddev.app.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rgddev.app.R
import com.rgddev.app.responce.insurancedetails.DataItem
import kotlinx.android.synthetic.main.item_insurance.view.*
import java.util.*


class MedicineListAdapter(val activity: FragmentActivity?) :
    RecyclerView.Adapter<MedicineListAdapter.Holder>() {

    private var listData = ArrayList<DataItem>()

    fun swapData(listData: ArrayList<DataItem>) {
        this.listData = listData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_insurance,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val itemView = holder.itemView
        val data = listData[position]

        itemView.txt_insurance.text = data.name
        itemView.img_medicin.visibility = View.GONE
    }

    override fun getItemCount() = listData.size

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

}