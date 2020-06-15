/*
 * Copyright (c) 2011-2019 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rgddev.app.baseview

import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.here.android.mpa.common.ApplicationContext
import com.here.android.mpa.common.MapEngine
import com.here.android.mpa.common.MapSettings
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.odml.MapLoader
import com.here.android.mpa.odml.MapPackage
import com.rgddev.app.R
import com.rgddev.app.adapter.MapListAdapter
import java.util.*

internal class MapListView(private val m_activity: AppCompatActivity) {
    private var m_progressTextView: TextView? = null
    private var m_mapLoader: MapLoader? = null
    private var m_listAdapter: MapListAdapter? = null
    private var m_currentMapPackageList: List<MapPackage>? =
        null// Global variable to keep track of the map
    private var language = ""


    // Listener to monitor all activities of MapLoader.
    private val m_listener = object : MapLoader.Listener {
        override fun onProgress(i: Int) {
            if (i < 100) {
                m_progressTextView!!.text = "Vooruitgang: $i %"
            } else {
                m_progressTextView!!.text = m_activity.resources.getString(R.string.text_installing)
            }
            Log.d(TAG, "onProgress()")
        }

        override fun onInstallationSize(l: Long, l1: Long) {}

        override fun onGetMapPackagesComplete(
            rootMapPackage: MapPackage?,
            resultCode: MapLoader.ResultCode?
        ) {
            m_activity.findViewById<View>(R.id.cancelBtn).visibility = View.INVISIBLE

            Log.d(TAG, "onGetMapPackagesComplete()")
            /*
             * Please note that to get the latest MapPackage status, the application should always
             * use the rootMapPackage that being returned here. The same applies to other listener
             * call backs.
             */
            if (resultCode == MapLoader.ResultCode.OPERATION_SUCCESSFUL) {
                val children = rootMapPackage?.children
                refreshListView(ArrayList(children!!))
            } else if (resultCode == MapLoader.ResultCode.OPERATION_BUSY) {
                // The map loader is still busy, just try again.
                m_mapLoader!!.mapPackages
            }
        }

        override fun onCheckForUpdateComplete(
            updateAvailable: Boolean,
            current: String?,
            update: String?,
            resultCode: MapLoader.ResultCode?
        ) {
            m_progressTextView!!.text = ""

            Log.d(TAG, "onCheckForUpdateComplete()")
            if (resultCode == MapLoader.ResultCode.OPERATION_SUCCESSFUL) {
                if (updateAvailable) {
                    // Update the map if there is a new version available
                    val success = m_mapLoader!!.performMapDataUpdate()
                    if (!success) {
                        Toast.makeText(
                            m_activity,
                            m_activity.resources.getString(R.string.alert_busy_with_other_operations),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        Toast.makeText(
                            m_activity,
                            "Beginnen met kaartupdate van huidige versie: $current naar $update",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                } else {
                    m_activity.findViewById<View>(R.id.cancelBtn).visibility = View.GONE

                    Toast.makeText(
                        m_activity, "Huidige kaartversie $current is de nieuwste versie",
                        Toast.LENGTH_SHORT
                    ).show()


                }
            } else if (resultCode == MapLoader.ResultCode.OPERATION_BUSY) {
                // The map loader is still busy, just try again.
                m_mapLoader!!.checkForMapDataUpdate()
            }
        }

        override fun onPerformMapDataUpdateComplete(
            rootMapPackage: MapPackage?,
            resultCode: MapLoader.ResultCode?
        ) {
            m_activity.findViewById<View>(R.id.cancelBtn).visibility = View.INVISIBLE

            m_progressTextView!!.text = ""

            Log.d(TAG, "onPerformMapDataUpdateComplete()")
            if (resultCode == MapLoader.ResultCode.OPERATION_SUCCESSFUL) {
                Toast.makeText(
                    m_activity,
                    m_activity.resources.getString(R.string.alert_map_update_completed),
                    Toast.LENGTH_SHORT
                ).show()
                refreshListView(ArrayList(rootMapPackage?.children!!))
            }
        }

        override fun onInstallMapPackagesComplete(
            rootMapPackage: MapPackage?,
            resultCode: MapLoader.ResultCode?
        ) {
            m_activity.findViewById<View>(R.id.cancelBtn).visibility = View.INVISIBLE

            m_progressTextView!!.text = ""
            if (resultCode == MapLoader.ResultCode.OPERATION_SUCCESSFUL) {
                Toast.makeText(
                    m_activity,
                    m_activity.resources.getString(R.string.alert_installation_completed),
                    Toast.LENGTH_SHORT
                ).show()
                val children = rootMapPackage?.children
                refreshListView(ArrayList(children!!))
            } else if (resultCode == MapLoader.ResultCode.OPERATION_CANCELLED) {
                Toast.makeText(
                    m_activity,
                    m_activity.resources.getString(R.string.alert_installation_cancelled),
                    Toast.LENGTH_SHORT
                )
                    .show()

            }
        }

        override fun onUninstallMapPackagesComplete(
            rootMapPackage: MapPackage?,
            resultCode: MapLoader.ResultCode?
        ) {

            m_activity.findViewById<View>(R.id.cancelBtn).visibility = View.INVISIBLE

            m_progressTextView!!.text = ""

            if (resultCode == MapLoader.ResultCode.OPERATION_SUCCESSFUL) {

                Toast.makeText(
                    m_activity,
                    m_activity.resources.getString(R.string.alert_uninstallation_completed),
                    Toast.LENGTH_SHORT
                )
                    .show()
                val children = rootMapPackage?.children
                refreshListView(ArrayList(children!!))
            } else if (resultCode == MapLoader.ResultCode.OPERATION_CANCELLED) {
                Toast.makeText(
                    m_activity,
                    m_activity.resources.getString(R.string.alert_uninstallation_cancelled),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    init {
        initMapEngine()
    }

    private fun getFolderPath(): String {
        return m_activity.getExternalFilesDir("isolated-here-maps")?.path ?: ""
    }

    private fun initMapEngine() {
        // Retrieve intent name from manifest
        var intentName: String? = ""
        try {
            val ai = m_activity.packageManager.getApplicationInfo(
                m_activity.packageName,
                PackageManager.GET_META_DATA
            )
            val bundle = ai.metaData
            intentName = bundle.getString("INTENT_NAME")
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(
                this.javaClass.toString(),
                "Failed to find intent name, NameNotFound: " + e.message
            )
        }

        val success = MapSettings.setIsolatedDiskCacheRootPath(getFolderPath())
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {
            MapEngine.getInstance().init(ApplicationContext(m_activity)) { error ->
                if (error == OnEngineInitListener.Error.NONE) {
                    /*
                         * Similar to other HERE Android SDK objects, the MapLoader can only be
                         * instantiated after the MapEngine has been initialized successfully.
                         */
                    getMapPackages()
                } else {
                    Log.e(TAG, "Failed to initialize MapEngine: $error")
                }
            }

        }
    }

    private fun initUIElements() {
        val cancelButton = m_activity.findViewById<View>(R.id.cancelBtn) as TextView
        cancelButton.setOnClickListener {
            m_progressTextView!!.text = m_activity.resources.getString(R.string.text_cancelling)

            m_mapLoader!!.cancelCurrentOperation()
        }
        m_progressTextView = m_activity.findViewById<View>(R.id.tvToolbarTitle) as TextView
        m_progressTextView!!.text = ""
        val mapUpdateButton = m_activity.findViewById<View>(R.id.mapUpdateBtn) as TextView
        mapUpdateButton.setOnClickListener {
            m_activity.findViewById<View>(R.id.cancelBtn).visibility = View.VISIBLE
            /*
                 * Because all operations of MapLoader are mutually exclusive, if there is any other
                 * operation which has been triggered previously but yet to receive its call
                 * back,the current operation cannot be triggered at the same time.
                 */

            val success = m_mapLoader!!.checkForMapDataUpdate()
            if (!success) {
                Toast.makeText(
                    m_activity,
                    m_activity.resources.getString(R.string.alert_busy_with_other_operations),
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                getMapPackages()
            }
        }
    }

    private fun getMapPackages() {
        Log.d(TAG, "getMapPackages()")
        m_mapLoader = MapLoader.getInstance()
        // Add a MapLoader listener to monitor its status
        m_mapLoader!!.addListener(m_listener)
        m_mapLoader!!.mapPackages
        initUIElements()

    }

    // Handles the click action on map list item.
    fun onListItemClicked(l: AdapterView<*>, v: View, position: Int, id: Long) {
        val clickedMapPackage = m_currentMapPackageList!![position]

        val children = clickedMapPackage.children
        if (children.size > 0) {
            // Children map packages exist.Show them on the screen.
            refreshListView(ArrayList(children))
        } else {
            /*
             * No children map packages are available, we should perform downloading or
             * un-installation action.
             */
            val idList = ArrayList<Int>()
            idList.add(clickedMapPackage.id)
            if (clickedMapPackage.installationState == MapPackage.InstallationState.INSTALLED) {

                val builder = AlertDialog.Builder(m_activity)

                builder.setMessage("Wilt u de kaart van ${clickedMapPackage.englishTitle} verwijderen?")

                builder.setPositiveButton(m_activity.resources.getString(R.string.label_ok)) { dialog, which ->
                    val success = m_mapLoader!!.uninstallMapPackages(idList)
                    if (!success) {
                        Toast.makeText(
                            m_activity,
                            m_activity.resources.getString(R.string.alert_busy_with_other_operations),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        m_activity.findViewById<View>(R.id.cancelBtn).visibility = View.VISIBLE
                        Toast.makeText(
                            m_activity,
                            m_activity.resources.getString(R.string.alert_uninstalling),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                builder.setNegativeButton(m_activity.resources.getString(R.string.label_cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()


            } else {
                m_activity.findViewById<View>(R.id.cancelBtn).visibility = View.VISIBLE
                val success = m_mapLoader!!.installMapPackages(idList)
                if (!success) {
                    Toast.makeText(
                        m_activity,
                        m_activity.resources.getString(R.string.alert_busy_with_other_operations),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        m_activity,
                        m_activity.getString(R.string.alert_downloading) + " " + clickedMapPackage.title,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun refreshListView(list: ArrayList<MapPackage>) {
        if (m_listAdapter != null) {
            m_listAdapter!!.clear()
            m_listAdapter!!.addAll(list)
            m_listAdapter!!.notifyDataSetChanged()
        } else {
            m_listAdapter = MapListAdapter(
                m_activity, android.R.layout.simple_list_item_1,
                list
            )
            m_activity.findViewById<ListView>(android.R.id.list).adapter = m_listAdapter
        }
        m_currentMapPackageList = list
    }

    companion object {
        private val TAG = MapListView::class.java.simpleName
    }
}
