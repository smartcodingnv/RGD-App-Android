package com.rgddev.app.activity

import android.content.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.rgddev.app.R
import com.rgddev.app.baseview.HereMapBaseFragment
import com.rgddev.app.firebase.MyFirebaseMessagingService.Companion.NOTIFICATION_COUNT_INTENT
import com.rgddev.app.fragment.*
import com.rgddev.app.premissionmanager.PermissionsManager
import com.rgddev.app.premissionmanager.PermissionsResultAction
import com.rgddev.app.utils.AppConfig
import kotlinx.android.synthetic.main.app_bar_sliding_drawer.*
import org.jetbrains.anko.locationManager
import java.util.*


class MainActivity : AppCompatActivity() {


    var array =
        arrayOf(
            "Nieuwsbrief",
            "Huisartsen",
            "Apotheek",
            "Lab",
            "Diensten",
            "Geneesmiddelen",
            "Wachtdienst",
            "Contact ons"
        )

    private var notificationReciever: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == NOTIFICATION_COUNT_INTENT) {
//                    if (typeOrId == "GUEST") {
//                        homeViewModel.getNotificationDataFromServer("")
//                    } else {
//                        homeViewModel.getNotificationDataFromServer(typeOrId!!)
//                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        getFirebaseToken()
        intiView()
        askPermissions()


        if (!isLocationEnabled(locationManager)) {
            alertgps()
        }
    }

    private fun alertgps() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage(getString(R.string.error_turn_on_gps))
        builder1.setCancelable(false)

        builder1.setPositiveButton(
            getString(R.string.res_ok),
            DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()

                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            })


        val alert11 = builder1.create()
        alert11.show()

    }

    private fun intiView() {

        navigationView()
        setNewsFragmnet(NewsFragment().newInstance())
    }

    private fun navigationView() {

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val img_notification: ImageView = findViewById(R.id.img_notification)
        val img_search: ImageView = findViewById(R.id.img_search)


        img_search.setOnClickListener(View.OnClickListener {

            val intent = Intent(AppConfig.EXTRA.ACTION_SEARCH)
            intent.putExtra(AppConfig.EXTRA.SEARCHKEY, AppConfig.EXTRA.SEARCHVALUE)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

//            itmeclick!!.data(it,  1)
        })

        img_notification.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, NotificationListActivity::class.java);
            startActivity(intent)

        })

        img_notification.visibility = View.VISIBLE
        img_search.visibility = View.GONE
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.setToolbarNavigationClickListener(View.OnClickListener {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        })

        val adapter = ArrayAdapter(
            this,
            R.layout.listview_item, array
        )

        val listView: ListView = findViewById(R.id.lst_menu_items)
        listView.setAdapter(adapter)

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {

                // value of item that is clicked
                val itemValue = listView.getItemAtPosition(position) as String
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                when (itemValue) {
                    "Nieuwsbrief" -> {
                        setNewsFragmnet(NewsFragment().newInstance())
                        img_notification.visibility = View.VISIBLE
                        img_search.visibility = View.GONE
                    }
                    "Huisartsen" -> {
                        setProviderFragmnet(ProviderFragment().newInstance())
                        img_notification.visibility = View.GONE
                        img_search.visibility = View.VISIBLE
                    }
                    "Apotheek" -> {
                        setPharmacyFragmnet(PharmacyFragment().newInstance())
                        img_notification.visibility = View.GONE
                        img_search.visibility = View.VISIBLE
                    }
                    "Lab" -> {
                        setLABFragmnet(LabFragment().newInstance())
                        img_notification.visibility = View.GONE
                        img_search.visibility = View.VISIBLE
                    }
                    "Diensten" -> {
                        setClinicFragmnet(ClinicFragment().newInstance())
                        img_notification.visibility = View.GONE
                        img_search.visibility = View.VISIBLE

//                        setPharmacyFragmnet(PharmacyFragment().newInstance())
//                        img_notification.visibility = View.GONE
//                        img_search.visibility = View.VISIBLE
                    }
                    "Geneesmiddelen" -> {

                        setInsurancePlan(InsuranceFragment().newInstance())
                        img_notification.visibility = View.VISIBLE
                        img_search.visibility = View.GONE
                    }
                    "Wachtdienst" -> {
                        setGuard(GurdFragment().newInstance())
                        img_notification.visibility = View.VISIBLE
                        img_search.visibility = View.GONE
                    }
                    "Contact ons" -> {
                        setContacus(ContactusFragment().newInstance())
                        img_notification.visibility = View.VISIBLE
                        img_search.visibility = View.GONE
                    }
                }

                // Toast the values
//                Toast.makeText(applicationContext,
//                    "Position :$position\nItem Value : $itemValue", Toast.LENGTH_LONG)
//                    .show()
            }
        }
    }

    private fun setNewsFragmnet(newsFragment: NewsFragment) {
        val t = supportFragmentManager.beginTransaction()
        t.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        t.replace(R.id.content_frame, newsFragment)
        t.commitAllowingStateLoss()
    }

    private fun setLABFragmnet(labFragment: LabFragment) {
        val t = supportFragmentManager.beginTransaction()
        t.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        t.replace(R.id.content_frame, labFragment)
        t.commitAllowingStateLoss()
    }

    private fun setClinicFragmnet(clinicFragment: ClinicFragment) {
        val t = supportFragmentManager.beginTransaction()
        t.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        t.replace(R.id.content_frame, clinicFragment)
        t.commitAllowingStateLoss()
    }

    private fun setProviderFragmnet(gpsFragment: ProviderFragment) {
        val t = supportFragmentManager.beginTransaction()
        t.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        t.replace(R.id.content_frame, gpsFragment)
        t.commitAllowingStateLoss()
    }

    private fun setInsurancePlan(insuranceFragment: InsuranceFragment) {
        val t = supportFragmentManager.beginTransaction()
        t.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        t.replace(R.id.content_frame, insuranceFragment)
        t.commitAllowingStateLoss()
    }

    private fun setGuard(gurdFragment: GurdFragment) {
        val t = supportFragmentManager.beginTransaction()
        t.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        t.replace(R.id.content_frame, gurdFragment)
        t.commitAllowingStateLoss()
    }

    private fun setContacus(contactusFragment: ContactusFragment) {
        val t = supportFragmentManager.beginTransaction()
        t.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        t.replace(R.id.content_frame, contactusFragment)
        t.commitAllowingStateLoss()
    }

    private fun setPharmacyFragmnet(pharmacyFragment: PharmacyFragment) {
        val t = supportFragmentManager.beginTransaction()
        t.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        t.replace(R.id.content_frame, pharmacyFragment)
        t.commitAllowingStateLoss()
    }

    private fun askPermissions() {
        PermissionsManager.getInstance()
            .requestAllManifestPermissionsIfNecessary(this, object : PermissionsResultAction() {
                override fun onGranted() {

                }

                override fun onDenied(permission: String) {
                    val message = String.format(
                        Locale.getDefault(),
                        getString(R.string.message_denied),
                        permission
                    )
                    //                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            })
    }

    private fun getFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                Log.d("FirebaseToken", token.toString())
            })

        FirebaseMessaging.getInstance().subscribeToTopic("GeneralNotificationAndroid")
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName
        public var isRunning = false


    }

    override fun onResume() {
        super.onResume()
        isRunning = true

        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(notificationReciever!!, IntentFilter(NOTIFICATION_COUNT_INTENT))

//        setLocalNotifications(

    }

    override fun onPause() {
        super.onPause()
        isRunning = false

        if (notificationReciever != null) {
            LocalBroadcastManager
                .getInstance(this).unregisterReceiver(notificationReciever!!)
        }

    }
}
