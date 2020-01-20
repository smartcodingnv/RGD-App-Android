package com.rgddev.app.adapter


import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.greenspot.app.utils.Common
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView
import com.rgddev.app.R
import it.sephiroth.android.library.imagezoom.ImageViewTouch
import kotlinx.android.synthetic.main.item_gardimg.view.*
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.layoutInflater
import java.util.*


class GuardImageAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<GuardImageAdapter.FeatureViewHolder>() {

    private var data: List<String> = ArrayList()
    private val mContext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        return FeatureViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_gardimg, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {

        holder.bind(data[position], context)

        holder.itemView.setOnClickListener(View.OnClickListener {
            showLargeImage(context = mContext!!, url = data[position].toString(), type = "I")
        })

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


            Glide.with(itemView)
                .load(item.toString())
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .into(itemView.img_gard)
        }
    }

    fun showLargeImage(
        context: Context,
        url: String? = null,
        bitmap: Bitmap? = null,
        type: String
    ) {
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(mContext!!.layoutInflater.inflate(R.layout.layout_large_video, null))

        val btnClose = dialog.findViewById<ImageButton>(R.id.btnIvClose)
        val ivPreview = dialog.findViewById<ImageViewTouch>(R.id.iv_preview_image)
        val youtubeView = dialog.findViewById<YouTubePlayerView>(R.id.youtubeView)
        var youTubePlayer: YouTubePlayer? = null

//
        if (bitmap != null) {
            youtubeView.visibility = View.VISIBLE
            ivPreview.imageBitmap = bitmap
        }

        if (type == "I" && bitmap == null) {
            Log.e("url", " " + url)
            youtubeView.visibility = View.GONE
            btnClose.visibility = View.VISIBLE
            ivPreview.visibility = View.VISIBLE
            Glide.with(context).load(url).into(ivPreview)
        } else if (type == "V") {
            ivPreview.visibility = View.GONE
            youtubeView.visibility = View.VISIBLE
            btnClose.visibility = View.VISIBLE
            youtubeView.initialize({
                it.addListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady() {
                        youTubePlayer = it
                        it.loadVideo(Common.getYoutubeId(url!!)!!, 0f)
                    }
                })
            }, true)
            youtubeView.enterFullScreen()
            youtubeView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
                override fun onYouTubePlayerEnterFullScreen() {}

                override fun onYouTubePlayerExitFullScreen() {
                    dialog.dismiss()
                }
            })
        }

        btnClose.setOnClickListener {
            youtubeView.release();
            dialog.dismiss()
            youTubePlayer?.pause()
            youTubePlayer = null
        }

        dialog.setOnDismissListener {
            youtubeView.release();
            youTubePlayer?.pause()
            youTubePlayer = null
        }


        dialog.show()
    }



}