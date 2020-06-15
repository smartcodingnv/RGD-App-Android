package com.rgddev.app.firebase

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rgddev.app.R
import com.rgddev.app.activity.GuardDetailActivity
import com.rgddev.app.activity.MainActivity
import com.rgddev.app.activity.NewsDetailActivity
import com.rgddev.app.utils.AppConfig
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var NOTIFICATION_COUNT_INTENT: String = "NOTIFICATION_COUNT"
        var NOTIFICATION: String = "NOTIFICATION"
    }

    private val TAG: String = "FIREBASETAG"
    private var notifManager: NotificationManager? = null
    private val bundle: Bundle? = null

    override fun onCreate() {
        super.onCreate()
        notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.e(TAG, remoteMessage.data.toString())

        if (remoteMessage != null) {
            if (remoteMessage.data != null) {
//                val firebaseData: FirebaseData = Gson().fromJson(remoteMessage.data["data"].toString(), object : TypeToken<FirebaseData>() {}.type)
//                sendNotification(firebaseData)

                sendNotification(remoteMessage.data)

            }
        }


        val broadcast = Intent(NOTIFICATION_COUNT_INTENT)
        LocalBroadcastManager.getInstance(application).sendBroadcast(broadcast)
    }

    @SuppressLint("WrongConstant")
    private fun sendNotification(data: Map<String, String>) {

        var module_ID: String? = null
        var idd: String? = null
        var body: String? = null
        var title: String? = null
        val pendingIntent = getPendingIntent(data)

        module_ID = data.get("module_ID").toString()
        idd = data.get("id").toString()
        body = data.get("body").toString()
        title = data.get("title").toString()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder: NotificationCompat.Builder
        val channelId = applicationContext.getString(R.string.default_notification_channel_id)

        builder = NotificationCompat.Builder(applicationContext, channelId).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(title)
            setContentText(Html.fromHtml(body).toString())
            setDefaults(Notification.DEFAULT_ALL)
            priority = NotificationCompat.PRIORITY_HIGH
            setAutoCancel(true)
            setContentIntent(pendingIntent)
            setWhen(System.currentTimeMillis())
            setTicker(title)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = Html.fromHtml(body).toString()
            }
            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(this)) {
            notify(notificationUniqueId, builder.build())
        }

        /*  val name = "my_package_channel"
          val id =
              getString(R.string.default_notification_channel_id) // The user-visible name of the channel.

          val description =
              "my_package_first_channel" // The user-visible description of the channel.

          var intent: Intent
          val pendingIntent: PendingIntent? = null
          var builder: NotificationCompat.Builder

          if (notifManager == null) {
              notifManager =
                  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
          }


          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
              val importance = NotificationManager.IMPORTANCE_HIGH
              var mChannel = notifManager!!.getNotificationChannel(id)
              if (mChannel == null) {
                  mChannel = NotificationChannel(id, name, importance)
                  mChannel.description = description
                  mChannel.enableVibration(true)
                  mChannel.lightColor = Color.GREEN
                  mChannel.vibrationPattern = longArrayOf(
                      100,
                      200,
                      300,
                      400,
                      500,
                      400,
                      300,
                      200,
                      400
                  )
                  notifManager!!.createNotificationChannel(mChannel)
              }
              builder = NotificationCompat.Builder(this, id)

              if (module_ID.equals("1")) {

                  if (Utils.isAppIsInBackground(applicationContext)) {
                      intent = Intent(
                          this,
                          NewsDetailActivity::class.java
                      ).putExtra(AppConfig.EXTRA.NEWSID, idd)
                  } else {
                      intent = Intent(
                          this,
                          NewsDetailActivity::class.java
                      ).putExtra(AppConfig.EXTRA.NEWSID, idd)

                  }
                  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                  intent.addFlags(
                      Intent.FLAG_ACTIVITY_SINGLE_TOP + Intent.FLAG_ACTIVITY_NEW_TASK
                              + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                              + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                              + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                              + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                  )
                  startActivity(intent)

              } else if (module_ID.equals("2")) {

                  if (Utils.isAppIsInBackground(applicationContext)) {
                      intent = Intent(
                          this,
                          GuardDetailActivity::class.java
                      ).putExtra(AppConfig.EXTRA.GUARDID, idd)
                  } else {
                      intent = Intent(
                          this,
                          GuardDetailActivity::class.java
                      ).putExtra(AppConfig.EXTRA.GUARDID, idd)

                  }
                  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                  intent.addFlags(
                      Intent.FLAG_ACTIVITY_SINGLE_TOP + Intent.FLAG_ACTIVITY_NEW_TASK
                              + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                              + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                              + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                              + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                  )
                  startActivity(intent)
              }

              builder.setContentTitle(title) // required
                  .setSmallIcon(R.mipmap.ic_launcher) // required
                  .setContentText(this.getString(R.string.app_name)) // required
                  .setDefaults(Notification.DEFAULT_ALL)
                  .setAutoCancel(true)
                  .setPriority(NotificationCompat.PRIORITY_MAX)
                  .setContentIntent(pendingIntent)
                  .setTicker(body)
                  .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))


          } else {
              builder = NotificationCompat.Builder(this)

              if (module_ID.equals("1")) {

                  if (Utils.isAppIsInBackground(applicationContext)) {
                      intent = Intent(
                          this,
                          NewsDetailActivity::class.java
                      ).putExtra(AppConfig.EXTRA.NEWSID, idd)
                  } else {
                      intent = Intent(
                          this,
                          NewsDetailActivity::class.java
                      ).putExtra(AppConfig.EXTRA.NEWSID, idd)

                  }
                  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                  intent.addFlags(
                      Intent.FLAG_ACTIVITY_SINGLE_TOP + Intent.FLAG_ACTIVITY_NEW_TASK
                              + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                              + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                              + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                              + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                  )
                  startActivity(intent)

              } else if (module_ID.equals("2")) {

                  if (Utils.isAppIsInBackground(applicationContext)) {
                      intent = Intent(
                          this,
                          GuardDetailActivity::class.java
                      ).putExtra(AppConfig.EXTRA.GUARDID, idd)
                  } else {
                      intent = Intent(
                          this,
                          GuardDetailActivity::class.java
                      ).putExtra(AppConfig.EXTRA.GUARDID, idd)

                  }
                  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                  intent.addFlags(
                      Intent.FLAG_ACTIVITY_SINGLE_TOP + Intent.FLAG_ACTIVITY_NEW_TASK
                              + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                              + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                              + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                              + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                  )
                  startActivity(intent)
              }

              builder.setContentTitle(title) // required
                  .setSmallIcon(R.mipmap.ic_launcher) // required
                  .setContentIntent(pendingIntent)
                  .setTicker(body)
                  .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                  .setPriority(Notification.PRIORITY_HIGH)
                  .setPriority(NotificationCompat.PRIORITY_MAX)
                  .setDefaults(Notification.DEFAULT_ALL)
                  .setCategory(Notification.CATEGORY_CALL)
                  .setOngoing(false)
                  .setAutoCancel(true)
                  .setWhen(System.currentTimeMillis())
                  .setSmallIcon(R.mipmap.ic_launcher)
                  .setPriority(NotificationCompat.PRIORITY_HIGH)
                  .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                  .setContentText(this.getString(R.string.app_name))
                  .setFullScreenIntent(pendingIntent, true)


          }*/


    }

    private fun getPendingIntent(data: Map<String, String>): PendingIntent {
        var module_ID: String? = null
        var idd: String? = null
        var body: String? = null
        var title: String? = null


        module_ID = data.get("module_ID").toString()
        idd = data.get("id").toString()
        body = data.get("body").toString()
        title = data.get("title").toString()
        getNotificationId()
        val menusIntent = intentFor<MainActivity>().newTask()
        val backIntent = intentFor<MainActivity>().newTask()
        backIntent.putExtra(AppConfig.EXTRA.NEWSID, idd)
        var intent = Intent()
        if (module_ID.equals("1")) {

            Log.e("checkk", " fireabse " + idd)

            intent = Intent(this, NewsDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(AppConfig.EXTRA.NEWSID, idd.toInt());
            intent.putExtra(AppConfig.EXTRA.CHECKNOTIFICAION, 2);

        } else {

            intent = Intent(this, GuardDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(AppConfig.EXTRA.GUARDID, idd.toInt());
            intent.putExtra(AppConfig.EXTRA.CHECKNOTIFICAION, 2);
        }

        /*val intent =
            if (module_ID.equals("1")) {

                intentFor<NewsDetailActivity>().newTask()

            } else {
                intentFor<GuardDetailActivity>().newTask()
            }


        if (!MainActivity.isRunning) {
            intent.putExtra(AppConfig.EXTRA.NEWSID, idd)
            return PendingIntent.getActivities(
                applicationContext, notificationUniqueId,
                arrayOf(intent), PendingIntent.FLAG_ONE_SHOT
            )
        } else {
            intent.putExtra(AppConfig.EXTRA.NEWSID, idd)
            return PendingIntent.getActivity(
                applicationContext, notificationUniqueId, intent, PendingIntent.FLAG_ONE_SHOT
            )
        }*/
        return PendingIntent.getActivity(
            applicationContext, notificationUniqueId, intent, PendingIntent.FLAG_ONE_SHOT
        )

    }

    override fun onNewToken(token: String) {


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        FirebaseMessaging.getInstance().subscribeToTopic("GeneralNotificationAndroid")
    }

    private var notificationUniqueId: Int = 0
    private fun getNotificationId() {
        notificationUniqueId = (Date().time / 1000L % Integer.MAX_VALUE).toInt()
    }


//    private fun sendNotification(data: FirebaseData) {
//
//        val title = data.greenStr
//        val body = data.message
////        val pendingIntent = getPendingIntent(data)
//        val notificationManager =
//            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val builder: NotificationCompat.Builder
//        val channelId = applicationContext.getString(R.string.default_notification_channel_id)
//
//        builder = NotificationCompat.Builder(applicationContext, channelId).apply {
//            setSmallIcon(R.mipmap.ic_launcher)
//            setContentTitle(title)
//            setContentText(Html.fromHtml(body).toString())
//            setDefaults(Notification.DEFAULT_ALL)
//            priority = NotificationCompat.PRIORITY_HIGH
//            setAutoCancel(true)
////            setContentIntent(pendingIntent)
//            setWhen(System.currentTimeMillis())
//            setTicker(title)
//        }
//
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(channelId, name, importance).apply {
//                description = Html.fromHtml(body).toString()
//            }
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        with(NotificationManagerCompat.from(this)) {
//            notify(notificationUniqueId, builder.build())
//        }
//    }

/* private fun getPendingIntent(data: FirebaseData): PendingIntent {
     getNotificationId()
     val menusIntent = intentFor<MenusActivity>().newTask()
     val backIntent = intentFor<MainActivity>().newTask()
     backIntent.putExtra("TYPE", "NEWS")

     val intent = if (data.category.equals(TYPE_GUARD_TIME_TABLE)) {
         intentFor<GuardTimeTableDetailActivity>().newTask()
     } else {
         intentFor<NotificationDetailActivity>().newTask()
     }


     if (!MainActivity.isRunning) {
         intent.putExtra("id", data.id)
             .putExtra("green_str", data.greenStr)
             .putExtra("message", data.message)
             .putExtra("start_date", data.startDate)
             .putExtra("end_date", data.endDate)
             .putExtra("image", data.image)
             .putExtra("category", data.category)
             .putExtra("refrence_record_id", data.refrenceRecordId)

         return PendingIntent.getActivities(
             applicationContext, notificationUniqueId,
             arrayOf(menusIntent, backIntent, intent), PendingIntent.FLAG_ONE_SHOT
         )
     } else {

         intent.putExtra("id", data.id)
             .putExtra("green_str", data.greenStr)
             .putExtra("message", data.message)
             .putExtra("start_date", data.startDate)
             .putExtra("end_date", data.endDate)
             .putExtra("image", data.image)
             .putExtra("category", data.category)
             .putExtra("refrence_record_id", data.refrenceRecordId)

         return PendingIntent.getActivity(
             applicationContext, notificationUniqueId,
             intent, PendingIntent.FLAG_ONE_SHOT
         )
     }


 }*/

/* private fun getPendingIntent(data: FirebaseData): PendingIntent {
     getNotificationId()
     val menusIntent = intentFor<MainActivity>().newTask()
     val backIntent = intentFor<MainActivity>().newTask()
     backIntent.putExtra("TYPE", "NEWS")

     val intent = if (data.category.equals(TYPE_GUARD_TIME_TABLE)) {
         intentFor<GuardTimeTableDetailActivity>().newTask()
     } else {
         intentFor<NotificationDetailActivity>().newTask()
     }


     if (!MainActivity.isRunning) {
         intent.putExtra("id", data.id)
             .putExtra("green_str", data.greenStr)
             .putExtra("message", data.message)
             .putExtra("start_date", data.startDate)
             .putExtra("end_date", data.endDate)
             .putExtra("image", data.image)
             .putExtra("category", data.category)
             .putExtra("refrence_record_id", data.refrenceRecordId)

         return PendingIntent.getActivities(
             applicationContext, notificationUniqueId,
             arrayOf(menusIntent, backIntent, intent), PendingIntent.FLAG_ONE_SHOT
         )
     } else {

         intent.putExtra("id", data.id)
             .putExtra("green_str", data.greenStr)
             .putExtra("message", data.message)
             .putExtra("start_date", data.startDate)
             .putExtra("end_date", data.endDate)
             .putExtra("image", data.image)
             .putExtra("category", data.category)
             .putExtra("refrence_record_id", data.refrenceRecordId)

         return PendingIntent.getActivity(
             applicationContext, notificationUniqueId,
             intent, PendingIntent.FLAG_ONE_SHOT
         )
     }


 }*/

}