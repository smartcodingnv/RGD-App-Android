package com.greenspot.app.utils

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class Common {
    companion object {
        //hide soft keyboard
        fun hideSoftKeyboard(activity: Activity) {
            val focusedView = activity.currentFocus
            if (focusedView != null) {
                val inputMethodManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
            }
        }


        //show soft keyboard
        fun showSoftKeyboard(view: View) {
            val inputMethodManager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            view.requestFocus()
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }

        //set horizontal layout manager of recyclerview
        fun setHorizontalRecyclerView(context: Context, layoutManagerRecyclerView: RecyclerView) {
            layoutManagerRecyclerView.layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
        }


        //set vertical layout manager of recyclerview
        fun setVerticalRecyclerView(
            context: Context,
            layoutManagerRecyclerView: RecyclerView
        ): LinearLayoutManager {
            val layoutManager: LinearLayoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.VERTICAL
            }
            layoutManagerRecyclerView.layoutManager = layoutManager
            return layoutManager
        }


        //set grid layout manager of recyclerview
        fun setGridRecyclerView(
            context: Context,
            layoutManagerRecyclerView: RecyclerView,
            numberOfColumns: Int
        ) {
            layoutManagerRecyclerView.layoutManager = GridLayoutManager(context, numberOfColumns)
        }

        fun replaceFragment(activity: FragmentActivity, fragmentClass: Class<*>?, id: Int) {
            lateinit var fragment: Fragment

            val fragmentClass: Class<*>? = fragmentClass
            try {
                fragment = (fragmentClass?.newInstance() as Fragment)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val fragmentManager = activity.supportFragmentManager
            fragmentManager?.beginTransaction()?.replace(id, fragment)?.commit()
        }

        fun replaceFragment(activity: FragmentActivity, fragment: Fragment, id: Int) {
            val fragmentManager = activity.supportFragmentManager
            fragmentManager?.beginTransaction()?.replace(id, fragment)?.commit()
        }


        fun getCurrentDate(): String {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd")
            return df.format(c)
        }

        @SuppressLint("SimpleDateFormat")
        fun formatDate(input: String): String {

            // Note, MM is months, not mm
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            val date = inputFormat.parse(input)
            return outputFormat.format(date)

        }

        @SuppressLint("SimpleDateFormat")
        fun formatDateToLongForm(input: String): String {

            // Note, MM is months, not mm
            val outputFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.ENGLISH)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = inputFormat.parse(input)
            return outputFormat.format(date)

        }

        fun getYoutubeId(url: String): String? {
            val pattern =
                "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"

            val compiledPattern = Pattern.compile(pattern)
            val matcher = compiledPattern.matcher(url)
            if (matcher.find()) {
                return matcher.group()
            }

            return ""
        }

        fun getThumbnailFromVideo(path: String): Bitmap {
            return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND)
        }

        fun getThumbnailFromImage(path: String): Bitmap {

            val fis = FileInputStream(path)
            val imageBitmap = BitmapFactory.decodeStream(fis)

            return ThumbnailUtils.extractThumbnail(imageBitmap, 100, 100)

        }


        fun createFile(context: Context, bitmap: Bitmap): File {
            val f = File(context.cacheDir, "image.png")
            if (!f.exists()) {
                try {
                    f.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)

            val bitmapdata = bos.toByteArray()

            val fos = FileOutputStream(f)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()

            return f
        }


        fun formatNumberToShortForm(number: Int): String {
            return when {
                Math.abs(number / 1000000) > 1 -> (number / 1000000).toString() + "M"
                Math.abs(number / 1000) > 1 -> (number / 1000).toString() + "K"
                else -> number.toString()
            }
        }

        fun shareToSocial(context: Context, subject: String, text: String) {
            val intent = Intent(Intent.ACTION_SEND)

            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.type = "text/plain"
            context.startActivity(Intent.createChooser(intent, "Share"))
        }

        private val SECOND_MILLIS = 1000
        private val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private val DAY_MILLIS = 24 * HOUR_MILLIS

        private fun currentDate(): Date {
            val calendar = Calendar.getInstance()
            return calendar.time
        }

        fun getPrettyTime(date: String): String? {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val mDate = sdf.parse(date)
            var timeInLong = mDate.time

            if (timeInLong < 1000000000000L) {
                timeInLong *= 1000
            }

            val now = currentDate().time
            if (timeInLong > now || timeInLong <= 0) {
                return "in the future"
            }

            val diff = now - timeInLong
            return when {
                diff < MINUTE_MILLIS -> "Just now"
                diff < 2 * MINUTE_MILLIS -> "1 minute ago"
                diff < 50 * MINUTE_MILLIS -> diff / MINUTE_MILLIS + "minutes ago"
                diff < 90 * MINUTE_MILLIS -> "1 hour ago"
                diff < 24 * HOUR_MILLIS -> diff / HOUR_MILLIS + "hours ago"
                else -> null
            }
        }

        fun getYTThumbUrl(url: String): String {
            return "https://img.youtube.com/vi/From${getYoutubeId(url)}/hqdefault.jpg"
        }





    }


}

private operator fun Long.plus(s: String): String {
    return "${this} $s"
}
