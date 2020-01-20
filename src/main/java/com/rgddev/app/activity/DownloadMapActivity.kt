package com.rgddev.app.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.here.android.mpa.odml.MapLoader
import com.here.android.mpa.odml.MapPackage
import com.rgddev.app.R
import com.rgddev.app.adapter.MapListAdapter
import com.rgddev.app.baseview.MapListView

import kotlinx.android.synthetic.main.activity_download_map.*
import kotlinx.android.synthetic.main.activity_download_map.toolbar
import kotlinx.android.synthetic.main.activity_notification_list.*
import kotlinx.android.synthetic.main.content_download_map.*

class DownloadMapActivity : AppCompatActivity() {

    private var m_mapListView: MapListView? = null
    private var m_progressTextView: TextView? = null
    private var m_mapLoader: MapLoader? = null
    private var m_listAdapter: MapListAdapter? = null
    private var m_currentMapPackageList: List<MapPackage>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_map)
        setSupportActionBar(toolbar)


        toolbar.setNavigationOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (hasPermissions(this, *RUNTIME_PERMISSIONS)) {
                setupMapListView()
            } else {
                ActivityCompat.requestPermissions(this, RUNTIME_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS)
            }
        } else{
            setupMapListView()
        }

    }

    private fun setupMapListView() {
        // All permission requests are being handled. Create map fragment view. Please note
        // the HERE SDK requires all permissions defined above to operate properly.
        m_mapListView = MapListView(this)
        list.setOnItemClickListener { parent, view, position, id ->
            m_mapListView!!.onListItemClicked(parent, view, position, id)
        }
    }

    companion object {


        private val REQUEST_CODE_ASK_PERMISSIONS = 1
        private val RUNTIME_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE)

        /**
         * Only when the app's target SDK is 23 or higher, it requests each dangerous permissions it
         * needs when the app is running.
         */
        private fun hasPermissions(context: Context, vararg permissions: String): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false
                    }
                }
            }
            return true
        }
    }

}
