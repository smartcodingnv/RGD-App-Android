package com.rgddev.app.baseview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapMarker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rgddev.app.R
import com.rgddev.app.others.GPSTracker
import org.jetbrains.anko.support.v4.toast


open class HereMapBaseFragment : Fragment() {

    private lateinit var gps: GPSTracker

    var mMap: Map? = null
    var currentPositionMarker: MapMarker? = null

    var mLocationPermissionGranted: Boolean = false
    var mylocation: Location? = null
    var mLastKnownGeoCoordinate: GeoCoordinate? = null
    var googleApiClient: GoogleApiClient? = null

    private val REQUEST_CHECK_SETTINGS_GPS = 0x1

    val PERMISSION_ID = 42





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        gps = GPSTracker(context)
        getLastLocation()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setCurrentLocation(geoCoordinate: GeoCoordinate, zoom: Double) {
        mMap!!.removeMapObject(currentPositionMarker)

        /*    val img = Image()
            try {

                img.setImageResource(R.drawable.ic_pin)

            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (currentPositionMarker == null) {
                currentPositionMarker = MapMarker()
            }
            currentPositionMarker!!.icon = img
            currentPositionMarker!!.coordinate = geoCoordinate
            mMap!!.addMapObject(currentPositionMarker)*/


    }




    var onLocationListener: OnLocationListner? = null

    interface OnLocationListner {
        fun onReceiveCurrentLocation(mLastKnownGeoCoordinate: GeoCoordinate?)
    }

    /*   fun getLocation() {


           mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
           val locationRequest = LocationRequest()
           locationRequest.interval = 3000
           locationRequest.fastestInterval = 3000
           locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
           val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
           builder.setAlwaysShow(true)
   //        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
           val result =
               LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())

           result.setResultCallback {
               result.addStatusListener {
                   when (it.statusCode) {
                       LocationSettingsStatusCodes.SUCCESS -> {
                           if (googleApiClient != null) {
                               mylocation = LocationServices.FusedLocationApi
                                   .getLastLocation(googleApiClient)
                               if (mMap != null && mylocation != null) {
                                   Log.e("lat"," "+mylocation!!.latitude)
                                   Log.e("long"," "+mylocation!!.longitude)
                                   mLastKnownGeoCoordinate = GeoCoordinate(mylocation!!.latitude, mylocation!!.longitude)
                                   setCurrentLocation(mLastKnownGeoCoordinate!!, 10.0)
                                   onLocationListener?.onReceiveCurrentLocation(mLastKnownGeoCoordinate!!)
                               } else {
                                   toast(getString(R.string.alert_cant_locate_location))
                                   onLocationListener?.onReceiveCurrentLocation(null)
                               }
                           }
                       }
                       LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                           try {
                               it.startResolutionForResult(
                                   activity!!,
                                   REQUEST_CHECK_SETTINGS_GPS
                               )
                           } catch (e: IntentSender.SendIntentException) {

                           }
                       }
                       LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                           activity!!.finish()
                       }
                   }
               }
           }
       }*/

    fun getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient!!.isConnected) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Dexter.withActivity(activity)
                        .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ).withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                if (report.areAllPermissionsGranted()) {
                                    try {
                                        getLastLocation()
//                                        getLocation()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                permissions: List<PermissionRequest>,
                                token: PermissionToken
                            ) {
                                token.continuePermissionRequest()
                            }
                        }).check()

                } else {
                    try {
                        getLastLocation()
//                        getLocation()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        }

    }

    companion object {

        val TAG = HereMapBaseFragment::class.java.simpleName
        val DEFAULT_ZOOM = 16.0
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        googleApiClient?.stopAutoManage(activity!!)
        googleApiClient?.disconnect()
        googleApiClient = null
    }

    fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
                getLastLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mLastKnownGeoCoordinate = GeoCoordinate(gps.latitude, gps.longitude)
                if (mMap != null && mLastKnownGeoCoordinate != null) {
                mLastKnownGeoCoordinate = GeoCoordinate(gps.latitude, gps.longitude)
                setCurrentLocation(mLastKnownGeoCoordinate!!, 10.0)
                onLocationListener?.onReceiveCurrentLocation(mLastKnownGeoCoordinate!!)
                }else{
//                    toast(getString(R.string.alert_cant_locate_location))
                    onLocationListener?.onReceiveCurrentLocation(null)
                }

            } else {
//                toast("Turn on location")
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }




    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation

            if (mMap != null && mLastLocation != null) {
                Log.e("lat", "2 " + mylocation!!.latitude)
                Log.e("long", "2 " + mylocation!!.longitude)
                mLastKnownGeoCoordinate =
                    GeoCoordinate(mLastLocation.latitude, mLastLocation.longitude)
                setCurrentLocation(mLastKnownGeoCoordinate!!, 10.0)
                onLocationListener?.onReceiveCurrentLocation(mLastKnownGeoCoordinate!!)
            } else {
                toast(getString(R.string.alert_cant_locate_location))
                onLocationListener?.onReceiveCurrentLocation(null)
            }

        }
    }


}


