package com.rgddev.app.fragment

import android.Manifest
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.here.android.mpa.common.*
import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.mapping.*
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.routing.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rgddev.app.R
import com.rgddev.app.activity.DownloadMapActivity
import com.rgddev.app.adapter.PharmacyListAdapter
import com.rgddev.app.baseview.HereMapBaseFragment
import com.rgddev.app.interfaces.ItemClickListener
import com.rgddev.app.network.ApiClient
import com.rgddev.app.network.ApiInterface
import com.rgddev.app.others.GPSTracker
import com.rgddev.app.responce.pharmcy.DataItem
import com.rgddev.app.responce.pharmcy.ResponcePharmcy
import com.rgddev.app.utils.AppConfig
import com.rgddev.app.utils.PreferenceHelper
import com.rgddev.app.utils.Progress
import com.rgddev.app.utils.Utils
import kotlinx.android.synthetic.main.dialog_search.*
import kotlinx.android.synthetic.main.fragment_pharmacy.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.ref.WeakReference


class PharmacyFragment : HereMapBaseFragment(), HereMapBaseFragment.OnLocationListner,
    ItemClickListener {
    // TODO: Rename and change types of parameters

    private var pharmacyResponce: String = ""
    private var pharmcyData: ArrayList<DataItem> = ArrayList()
    private var progress: Progress? = null
    private var utils: Utils? = null
    private var helper: PreferenceHelper? = null
    private lateinit var dialog: Dialog

    private lateinit var mView: View
    private lateinit var proivderListAdapter: PharmacyListAdapter
    private var mLayoutManager: LinearLayoutManager? = null

    private val mMapObjectList = ArrayList<MapObject>()
    private var mMapRoute: MapRoute? = null
    private var positioningManager: PositioningManager? = null
    private lateinit var mapFragment: SupportMapFragment
    private var nearBy: Int = 500000
    private var searchText: String = ""
    private lateinit var gps: GPSTracker


    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val str = intent.getStringExtra(AppConfig.EXTRA.SEARCHKEY);
                if (str.equals(AppConfig.EXTRA.SEARCHVALUE)) {
                    openSearchDialog()
                }
                // get all your data from intent and do what you want
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onLocationListener = this


    }

    fun newInstance(): PharmacyFragment {

        val args = Bundle()
        val fragment = PharmacyFragment()
        fragment.setArguments(args)
        return fragment

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        progress = Progress(this.activity!!)
        utils = Utils(this.activity!!)
        helper = PreferenceHelper(this.activity!!, AppConfig.PREFERENCE.PREF_FILE)
        gps = GPSTracker(context)
        LocalBroadcastManager.getInstance(activity!!)
            .registerReceiver(userDataChangeReceiver, IntentFilter(AppConfig.EXTRA.ACTION_SEARCH))

        return inflater.inflate(R.layout.fragment_pharmacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mView = view
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        val imgpin1 = mView.findViewById<ImageView>(R.id.ivPin1)
        val imgpin2 = mView.findViewById<ImageView>(R.id.ivPin2)
        val imgpin3 = mView.findViewById<ImageView>(R.id.ivPin3)
        val imgpin4 = mView.findViewById<ImageView>(R.id.ivPin4)
        val imgpin5 = mView.findViewById<ImageView>(R.id.ivPin5)

        imgpin1.visibility = View.INVISIBLE
        imgpin2.visibility = View.INVISIBLE
        imgpin3.visibility = View.INVISIBLE
        imgpin4.visibility = View.INVISIBLE
        imgpin5.visibility = View.VISIBLE

        val gson = Gson()
        val pharmcy =
            gson.fromJson(
                helper!!.LoadStringPref(AppConfig.PREFERENCE.PHARMACYRESPONCE, ""),
                ResponcePharmcy::class.java
            )

        if (pharmcy == null) {
            getpharmacyData(AppConfig.URL.TOKEN, "", 1)
        } else {

            pharmcyData = pharmcy.data as ArrayList<DataItem>

            setProviderData(1)
            getpharmacyData(AppConfig.URL.TOKEN, "", 2)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withActivity(activity)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {

                            mLocationPermissionGranted = true
                            doAsync {
                                setupMapFragmentView()
                            }
                        } else {
                            setupMapFragmentView()
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
            mLocationPermissionGranted = true
            doAsync {
                setupMapFragmentView()
            }
        }

        hidePharmacyAndPracticeList()

        imgBtnUpArrow.setOnClickListener(View.OnClickListener {

            if (llPharmacyPracticeList.visibility == View.VISIBLE) {

                hidePharmacyAndPracticeList()

            } else {

                showPharmacyAndPracticeList()
            }
        })

        imgBtnCurrentLocation.setOnClickListener(View.OnClickListener {

            getMyLocation()
            if (mMap != null && mLastKnownGeoCoordinate != null) {
                mMap!!.setCenter(mLastKnownGeoCoordinate!!, Map.Animation.BOW)
                mMap!!.zoomLevel = DEFAULT_ZOOM
            }
            hidePharmacyAndPracticeList()
        })

        btnPin1.setOnClickListener(View.OnClickListener {

            imgpin1.visibility = View.VISIBLE
            imgpin2.visibility = View.INVISIBLE
            imgpin3.visibility = View.INVISIBLE
            imgpin4.visibility = View.INVISIBLE
            imgpin5.visibility = View.INVISIBLE
            nearBy = 5
            hidePharmacyAndPracticeList()
            setProviderData(2)

        })

        btnPin2.setOnClickListener(View.OnClickListener {
            imgpin1.visibility = View.INVISIBLE
            imgpin2.visibility = View.VISIBLE
            imgpin3.visibility = View.INVISIBLE
            imgpin4.visibility = View.INVISIBLE
            imgpin5.visibility = View.INVISIBLE
            nearBy = 10


            /*     sliderPins.forEach {
                     if (it.isSelected) {
                         mView.findViewById<ImageView>(it.pinId).visibility = View.VISIBLE
                     }
                 }*/
            hidePharmacyAndPracticeList()
            setProviderData(2)

        })

        btnPin3.setOnClickListener(View.OnClickListener {
            imgpin1.visibility = View.INVISIBLE
            imgpin2.visibility = View.INVISIBLE
            imgpin3.visibility = View.VISIBLE
            imgpin4.visibility = View.INVISIBLE
            imgpin5.visibility = View.INVISIBLE
            nearBy = 15

            hidePharmacyAndPracticeList()
            setProviderData(2)

        })

        btnPin4.setOnClickListener(View.OnClickListener {
            imgpin1.visibility = View.INVISIBLE
            imgpin2.visibility = View.INVISIBLE
            imgpin3.visibility = View.INVISIBLE
            imgpin4.visibility = View.VISIBLE
            imgpin5.visibility = View.INVISIBLE
            nearBy = 20


            hidePharmacyAndPracticeList()
            setProviderData(2)
//            prepareData(searchBy)

        })

        btnPin5.setOnClickListener(View.OnClickListener {
            imgpin1.visibility = View.INVISIBLE
            imgpin2.visibility = View.INVISIBLE
            imgpin3.visibility = View.INVISIBLE
            imgpin4.visibility = View.INVISIBLE
            imgpin5.visibility = View.VISIBLE
            nearBy = 500000

            hidePharmacyAndPracticeList()
            setProviderData(2)
//            prepareData(searchBy)

        })

        imgBtnDownloadMap.setOnClickListener(View.OnClickListener {
            startActivity(intentFor<DownloadMapActivity>())
        })


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

    }

    private fun getpharmacyData(header: String, sync_date: String, flag: Int) {

        progress!!.createDialog(false)
        progress!!.DialogMessage(getString(R.string.please_wait))

        if (flag == 1) {
            progress!!.showDialog()
        }
        utils!!.hideKeyboard()
        val apiService = ApiClient.client?.create(ApiInterface::class.java)
        val responceProvider = apiService?.CALL_PHARMACYLIST(
            token = header,
            syncDate = ""
        )

        responceProvider?.enqueue(object : Callback<ResponcePharmcy> {
            override fun onResponse(
                @NonNull call: Call<ResponcePharmcy>,
                @NonNull response: Response<ResponcePharmcy>
            ) {
                progress!!.hideDialog()
                val providerresponce = response.body()
                Log.e("codee", " " + response.code())
                if (response.code() == AppConfig.URL.SUCCESS) {
                    if (providerresponce!!.apiStatus == 1) {

                        val gson = Gson()
                        pharmacyResponce = gson.toJson(providerresponce)
                        helper!!.initPref()
                        helper!!.SaveStringPref(
                            AppConfig.PREFERENCE.PHARMACYRESPONCE,
                            pharmacyResponce
                        )
                        helper!!.ApplyPref()
                        pharmcyData = providerresponce.data as ArrayList<DataItem>
                        setProviderData(1)

                    } else {

                        if (providerresponce.data == null || providerresponce.data.isEmpty()) {
                            Toast.makeText(
                                activity,
                                providerresponce.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }
                } else {
//                    Toast.makeText(
//                        activity,
//                        getString(R.string.msg_unexpected_error),
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }

            override fun onFailure(@NonNull call: Call<ResponcePharmcy>, @NonNull t: Throwable) {
                progress!!.hideDialog()
//                if(flag==1){
//                    Toast.makeText(
//                        activity,
//                        getString(R.string.msg_internet_conn),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }

            }
        })
    }

    private fun hidePharmacyAndPracticeList() {
        llPharmacyPracticeList.visibility = View.GONE
        imgBtnUpArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_up))
    }

    private fun showPharmacyAndPracticeList() {

        val slideUp = AnimationUtils.loadAnimation(activity, R.anim.slide_up_animation)
        llPharmacyPracticeList.visibility = View.VISIBLE
        llPharmacyPracticeList.startAnimation(slideUp)
        imgBtnUpArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_down))
    }

    private fun openSearchDialog() {

        dialog = Dialog(this.activity!!)
        dialog.setContentView(R.layout.dialog_search)
        dialog.window?.setBackgroundDrawableResource(R.color.colorIdolDetailDialogBackground)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.txt_title.setText("Zoeken Apotheek")


        if (searchText.isNullOrEmpty()) {
            dialog.et_search.hint = "Zoeken in de buurt apotheek"
        } else {
            dialog.et_search.setText(searchText)

        }

        dialog.btn_search.setOnClickListener(View.OnClickListener {
            searchText = dialog.et_search.text.toString()
            filter(dialog.et_search.text.toString())

            dialog.dismiss()
        })


        dialog.lay_dialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun filter(text: String) {
        val filterdNames: java.util.ArrayList<DataItem> =
            java.util.ArrayList()

        for (s in pharmcyData) { //if the existing elements contains the search input

            if (s.name.toLowerCase()
                    .contains(text.toLowerCase())
            ) { //adding the element to filtered list
                filterdNames.add(s)
            }
        }
        //calling a method of the adapter class and passing the filtered list
        if (filterdNames.isNotEmpty()) {
            showPharmacyAndPracticeList()
        } else {
            hidePharmacyAndPracticeList()
            toast(getString(R.string.alert_no_data_found))
        }
        proivderListAdapter.filterList(filterdNames)
    }

    private fun setProviderData(flag: Int) {

        cleanMap()
        getMyLocation()
        val dataWithDistance = ArrayList<DataWithDistance>()
        if (mLastKnownGeoCoordinate != null && mLocationPermissionGranted) {

            Log.e("checkdata", "  if")

            val filteredData = pharmcyData.filter {
                it.latitude.trim().isNotEmpty()
                        && getDistance(
                    it.latitude,
                    it.longitude,
                    mLastKnownGeoCoordinate!!.latitude,
                    mLastKnownGeoCoordinate!!.longitude
                ) <= nearBy
            }

            filteredData.forEach {
                val distance = getDistanceee(
                    it.latitude.toDouble(),
                    it.longitude.toDouble(),
                    mLastKnownGeoCoordinate!!.latitude,
                    mLastKnownGeoCoordinate!!.longitude
                )
                dataWithDistance.add(
                    DataWithDistance(
                        it,
                        GeoCoordinate(it.latitude.toDouble(), it.longitude.toDouble()),
                        R.drawable.ic_pharmacy_pin,
                        distance
                    )
                )

            }

            filteredData.forEach {

                addMarkerAtPlace(it, R.drawable.ic_pharmacy_pin, null)
            }

            val sortedList = dataWithDistance.sortedWith(compareBy { it.distance })
            /*     var count = 0
                 sortedList.forEach {
                     count++
                     addMarkerAtPlace(it.dataItem, R.drawable.ic_pharmacy_pin, count)
                 }*/


            mLayoutManager = LinearLayoutManager(activity)
            rvPharmacyPractice.setLayoutManager(mLayoutManager)
            proivderListAdapter = PharmacyListAdapter(activity, this)
            proivderListAdapter.swapData(filteredData)
            rvPharmacyPractice.adapter = proivderListAdapter


        } else {
            Log.e("checkdata", "  else")
            nearBy = 500000
            val filteredData = pharmcyData.filter {
                it.latitude.trim().isNotEmpty()
            }

            filteredData.forEach {

                addMarkerAtPlace(it, R.drawable.ic_pharmacy_pin, null)
            }
//                            pharmacyOrPracticeListAdapter?.swapData(filteredData)

            mLayoutManager = LinearLayoutManager(activity)
            rvPharmacyPractice.setLayoutManager(mLayoutManager)
            proivderListAdapter = PharmacyListAdapter(activity, this)
            proivderListAdapter.swapData(filteredData)
            rvPharmacyPractice.adapter = proivderListAdapter
        }


    }

    fun getDistanceee(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val theta = lon1 - lon2
        var dist =
            (Math.sin(deg2rad(lat1))
                    * Math.sin(deg2rad(lat2))
                    + (Math.cos(deg2rad(lat1))
                    * Math.cos(deg2rad(lat2))
                    * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    private fun getDistance(lat1: String, lon1: String, lat2: Double, lon2: Double): Float {
        val loc1 = android.location.Location("point A")
        loc1.latitude = lat1.toDouble()
        loc1.longitude = lon1.toDouble()

        val loc2 = android.location.Location("point B")
        loc2.latitude = lat2
        loc2.longitude = lon2

        return loc1.distanceTo(loc2) / 1000
    }

    private fun addMarkerAtPlace(provider: DataItem, pin: Int, count: Int?) {
        if (!isAdded) return
        doAsync {

            val img = Image()
            try {

                if (count != null)
                    img.setBitmap(createMarkerBitmap(pin, "$count ${provider.name}"))
                else
                    img.setBitmap(createMarkerBitmap(pin, provider.name))

            } catch (e: IOException) {
                e.printStackTrace()
            }

            val mapMarker = MapMarker()
            mapMarker.title = provider.name
            mapMarker.icon = img
            mapMarker.coordinate =
                GeoCoordinate(provider.latitude!!.toDouble(), provider.longitude!!.toDouble())
            val address = (provider.name + ", " + provider.address).replace(
                " SR",
                ", Suriname",
                ignoreCase = false
            )

            mapMarker.description = address
            mMap!!.addMapObject(mapMarker)
            mMapObjectList.add(mapMarker)
        }
    }

    private fun createMarkerBitmap(pin: Int, title: String, isShowTitle: Boolean = false): Bitmap {

        val markerLayout = layoutInflater.inflate(R.layout.layout_marker, null)
        markerLayout.findViewById<ImageView>(R.id.ivPin)
            .setImageDrawable(context!!.getDrawable(pin))
        markerLayout.findViewById<TextView>(R.id.txtTitle).text = title

        if (isShowTitle) {
            markerLayout.findViewById<TextView>(R.id.txtTitle).visibility = View.VISIBLE
            markerLayout.findViewById<View>(R.id.emptyLayout).visibility = View.VISIBLE
        }

//        if (getPharmacyOrPracice == 0) {
//            markerLayout.findViewById<TextView>(R.id.txtTitle).textColor = resources.getColor(R.color.headerTextColor)
//            markerLayout.findViewById<LinearLayout>(R.id.layoutBackground).backgroundColor = resources.getColor(R.color.toolbarTextColor)
//        } else {
//            markerLayout.findViewById<TextView>(R.id.txtTitle).textColor = resources.getColor(R.color.toolbarTextColor)
//            markerLayout.findViewById<LinearLayout>(R.id.layoutBackground).backgroundColor = resources.getColor(R.color.headerTextColor)
//
//        }

        markerLayout.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        markerLayout.layout(0, 0, markerLayout.measuredWidth, markerLayout.measuredHeight)

        val bitmap = Bitmap.createBitmap(
            markerLayout.measuredWidth,
            markerLayout.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        markerLayout.draw(canvas)
        return bitmap
    }

    private fun cleanMap() {
        if (mMapObjectList.isNotEmpty()) {
            mMap!!.removeMapObjects(mMapObjectList)
            mMapRoute?.let { mMap!!.removeMapObject(it) }
            mMap!!.setTilt(0f, Map.Animation.BOW)
            mMapObjectList.clear()

//            pharmacyOrPractices.clear()
//            pharmacyOrPracticeListAdapter?.swapData(pharmacyOrPractices)
        }
    }

    private fun createRoute(geoCoordinate: GeoCoordinate) {
        // prevent creating route if location is not found
        if (!mLocationPermissionGranted || mLastKnownGeoCoordinate == null) return

        // remove old route plan if it exists
        if (mMap != null && mMapRoute != null) {
            mMap!!.removeMapObject(mMapRoute!!)
            mMapRoute = null
        }

        // create a new route plan
        val routePlan = RoutePlan()
        val routeOptions = RouteOptions()
        routeOptions.transportMode = RouteOptions.TransportMode.CAR
        routeOptions.setHighwaysAllowed(true)
        routeOptions.routeType = RouteOptions.Type.SHORTEST
        routeOptions.routeCount = 1
        routePlan.routeOptions = routeOptions
        routePlan.addWaypoint(RouteWaypoint(mLastKnownGeoCoordinate!!))
        routePlan.addWaypoint(RouteWaypoint(geoCoordinate))


        // calculate route and start navigation
        CoreRouter().calculateRoute(
            routePlan,
            object : Router.Listener<List<RouteResult>, RoutingError> {
                override fun onCalculateRouteFinished(
                    routeResults: List<RouteResult>?,
                    routingError: RoutingError
                ) {
                    if (routingError == RoutingError.NONE) {
                        if (routeResults?.get(0)?.route != null) {
                            mMapRoute = MapRoute(routeResults[0].route)

                            routePlan.getWaypoint(0)?.navigablePosition?.let {
                                mMap!!.setCenter(
                                    it,
                                    Map.Animation.BOW
                                )
                            }
                            mMap!!.zoomLevel = DEFAULT_ZOOM

                            mMapRoute!!.isManeuverNumberVisible = true

                            NavigationManager.getInstance().mapUpdateMode =
                                NavigationManager.MapUpdateMode.POSITION_ANIMATION
                            mMap!!.tilt = 80f

                            NavigationManager.getInstance().setMap(mMap)
                            mMapRoute!!.route?.let {
                                NavigationManager.getInstance().startNavigation(
                                    it
                                )
                            }
                            NavigationManager.getInstance().addRerouteListener(
                                WeakReference<NavigationManager.RerouteListener>(mRerouteListner)
                            )
//                        NavigationManager.getInstance().addNavigationManagerEventListener(WeakReference<NavigationManager.NavigationManagerEventListener>(object : NavigationManager.NavigationManagerEventListener() {
//                            override fun onEnded(p0: NavigationManager.NavigationMode?) {
//                                super.onEnded(p0)
//                                mMap!!.removeMapObject(mMapRoute)
//                                mMapRoute = null
//                                NavigationManager.getInstance().stop()
//                            }
//                        }))

                            mapFragment.positionIndicator.isVisible = false

                            mMap!!.addMapObject(mMapRoute!!)

                        } else {
                            Toast.makeText(
                                activity,
                                "route results returned is not valid",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Log.d(TAG, "route calculation returned error code: $routingError")
                        /*Toast.makeText(
                            activity,
                            "Route does not found",
                            Toast.LENGTH_LONG
                        ).show()*/
                    }
                }

                override fun onProgress(p0: Int) {

                }
            })
    }

    private val mRerouteListner = object : NavigationManager.RerouteListener() {
        override fun onRerouteEnd(routeResult: RouteResult, p1: RoutingError?) {
            super.onRerouteEnd(routeResult, p1)
            mMapRoute?.setRoute(routeResult.route)

        }
    }

    private val mapGestureListner = object : MapGesture.OnGestureListener {
        override fun onRotateEvent(p0: Float): Boolean {
            return true
        }

        override fun onMultiFingerManipulationStart() {

        }

        override fun onPinchLocked() {

        }

        override fun onPinchZoomEvent(p0: Float, p1: PointF): Boolean {
            return false
        }

        override fun onTapEvent(p0: PointF): Boolean {
            Log.d("ONTAP", p0!!.x.toString() + " " + p0.x.toString())
            return true
        }

        override fun onPanStart() {

        }

        override fun onMultiFingerManipulationEnd() {

        }

        override fun onDoubleTapEvent(p0: PointF): Boolean {
            return false
        }

        override fun onPanEnd() {

        }

        override fun onTiltEvent(p0: Float): Boolean {

            return false
        }

        override fun onMapObjectsSelected(list: MutableList<ViewObject>): Boolean {
            list.forEach {
                if (it.baseType == ViewObject.Type.USER_OBJECT) {
                    val mapObject = it as MapObject

                    if (mapObject.type == MapObject.Type.MARKER && currentPositionMarker != mapObject) {

                        // store object to changes its marker title
                        val marker = (mapObject as (MapMarker))
                        marker.setVisibleMask(14)

                        // clean map to show routing clearly
                        //                        cleanMap()

                        setProviderData(3)
                        // at title to selected marker
                        val img = Image()
                        try {
                            //                            val pin = if (getPharmacyOrPracice == 0) R.drawable.ic_pharmacy_pin else R.drawable.ic_practice_pin
                            val pin = R.drawable.ic_pharmacy_pin
                            img.setBitmap(createMarkerBitmap(pin, marker.title ?: "", true))
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        marker.icon = img
                        mMap!!.addMapObject(marker)
                        mMapObjectList.add(marker)



                        Log.d(
                            "Position->",
                            marker.title + "\n" + marker.coordinate.latitude.toString() + ", " + marker.coordinate.longitude.toString()
                        )

                        // show routing on the map
                        createRoute(GeoCoordinate(marker.coordinate))

                        return false
                    }
                }

            }
            return false
        }

        override fun onRotateLocked() {

        }

        override fun onLongPressEvent(p0: PointF): Boolean {
            return true
        }

        override fun onTwoFingerTapEvent(p0: PointF): Boolean {
            return true
        }

        override fun onLongPressRelease() {

        }
    }


    private fun updateLocationDetails() {

        Log.e("check ", " 1")
        setProviderData(1)

    }

    private fun getFolderPath(): String {
        return activity?.getExternalFilesDir("isolated-here-maps")?.path ?: ""
    }

    private fun setupMapFragmentView() {

        // Retrieve intent name from manifest
        var intentName: String? = ""
        try {
            val ai = activity!!.packageManager.getApplicationInfo(
                activity!!.packageName,
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
        Log.e("check", " " + success)
        if (!success) {
            Log.e("check", " if" + success)
        } else {
            Log.e("check", " else" + success)
            mapFragment.init { error ->
                Log.e("check", " else" + error.details)

                if (error == OnEngineInitListener.Error.NONE) {


                    mMap = mapFragment.map
                    mLastKnownGeoCoordinate = GeoCoordinate(gps.latitude, gps.longitude)

                    getMyLocation()
                    if (mMap != null && mLastKnownGeoCoordinate != null) {

                        mMap!!.setCenter(mLastKnownGeoCoordinate!!, Map.Animation.NONE)
                        mMap!!.setZoomLevel((mMap!!.getMaxZoomLevel() + mMap!!.getMinZoomLevel()) / 2)

                    } else {
                        mMap!!.setCenter(GeoCoordinate(5.7774088, -55.26501596), Map.Animation.NONE)
                        mMap!!.setZoomLevel((mMap!!.getMaxZoomLevel() + mMap!!.getMinZoomLevel()) / 2)

                    }

                    if (activity != null && isAdded) {

                        mapFragment.mapGesture.addOnGestureListener(mapGestureListner, 0, false)
                        mapFragment.positionIndicator.isVisible = true


                        positioningManager = PositioningManager.getInstance()
                        getMyLocation()
                        positioningManager?.addListener(
                            WeakReference<PositioningManager.OnPositionChangedListener>(
                                positionListner
                            )
                        )
                        positioningManager?.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR)

                        doAsync {
                            updateLocationDetails()
                        }
                    }
                }
            }
        }
    }

    private val positionListner = object : PositioningManager.OnPositionChangedListener {
        override fun onPositionUpdated(
            method: PositioningManager.LocationMethod, position: GeoPosition?,
            isMapMatched: Boolean
        ) {
            if (currentPositionMarker != null) {
                if (position != null) {
                    currentPositionMarker!!.coordinate = position.coordinate
                }
            }
            if (position != null) {
                mLastKnownGeoCoordinate = position.coordinate
            }
        }

        override fun onPositionFixChanged(
            method: PositioningManager.LocationMethod,
            status: PositioningManager.LocationStatus
        ) {

        }
    }

    override fun onResume() {
        super.onResume()
        MapEngine.getInstance().onResume()
        positioningManager?.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR)

//        showFirstTimeAlerts()
    }
//    private fun showFirstTimeAlerts() {
//        val preference = AppPreference.getInstanceLocal(context!!)
//        val isFirstLogin = preference!!.getBoolean(AppPreference.IS_FIRST_LOGIN_FOR_MAP, true)
//
//
//        if (!isNetworkAvailable(context!!) && isFirstLogin) {
//            val builder = AlertDialog.Builder(context!!)
//            builder.setTitle(getString(R.string.alert_go_to_setting))
//            builder.setCancelable(false)
//            builder.setMessage(getString(R.string.alert_check_internet));
//            builder.setPositiveButton(getString(R.string.btn_ok)) { dialog, which ->
//                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
//
//            }
//            builder.setNegativeButton(getString(R.string.btn_cancel)) { dialog, which ->
//                dialog.dismiss()
//            }
//            val dialog: AlertDialog = builder.create()
//            dialog.show()
//        }
//
//        if (isNetworkAvailable(context!!) && isFirstLogin) {
//            val builder = AlertDialog.Builder(context!!)
//            builder.setTitle(getString(R.string.alert_title_download_map))
//            builder.setMessage(getString(R.string.alert_download_map))
//            builder.setCancelable(false)
//            builder.setPositiveButton(getString(R.string.btn_ok)) { dialog, which ->
//                startActivity(intentFor<MapListActivity>())
//                preference.edit().putBoolean(AppPreference.IS_FIRST_LOGIN_FOR_MAP, false).apply()
//
//            }
//            builder.setNegativeButton(getString(R.string.btn_cancel)) { dialog, which ->
//                dialog.dismiss()
//                preference.edit().putBoolean(AppPreference.IS_FIRST_LOGIN_FOR_MAP, false).apply()
//            }
//            val dialog: AlertDialog = builder.create()
//            dialog.show()
//        }
//
//        if (mMap != null && mLocationPermissionGranted) {
//            updateLocationDetails()
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(userDataChangeReceiver);
        positioningManager?.removeListener(positionListner)

        mMap = null
    }

    override fun onPause() {
        super.onPause()
        MapEngine.getInstance().onPause()
        positioningManager?.stop()
    }

    data class DataWithDistance(
        var dataItem: DataItem,
        var geoCoordinate: GeoCoordinate,
        var pin: Int,
        var distance: Double
    )

    override fun onReceiveCurrentLocation(mLastKnownGeoCoordinate: GeoCoordinate?) {


    }


    override fun recyclerViewListClicked(v: View, position: Int, flag: Int) {

        if (mMap != null) {


            // remove old selected pin
            Log.e("checkflaag", " " + flag)

            pharmcyData.mapIndexed { index, dataItem ->

                if (dataItem.id == flag) {


                    mMapObjectList.forEach {
                        if (it.type == MapObject.Type.MARKER) {
                            val img = Image()
                            val marker = (it as (MapMarker))

                            try {
                                img.setBitmap(
                                    createMarkerBitmap(
                                        R.drawable.ic_pharmacy_pin,
                                        marker.title ?: ""
                                    )
                                )

                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            marker.icon = img

                        }
                    }

                    // focus and zoom in to selected pin coordinate
                    mMap!!.setCenter(
                        GeoCoordinate(
                            pharmcyData[index].latitude.toDouble(),
                            pharmcyData[index].longitude.toDouble()
                        ), Map.Animation.BOW
                    )
                    mMap!!.setZoomLevel(DEFAULT_ZOOM, Map.Animation.BOW)
                    mMap!!.setTilt(0f, Map.Animation.BOW)
                    if (mMapRoute != null) {
                        mMap!!.removeMapObject(mMapRoute!!)
                        mMapRoute = null
                    }

                    // set selection of new pin
                    mMapObjectList.forEach {
                        if (it.type == MapObject.Type.MARKER) {
                            val marker = (it as (MapMarker))
                            if (marker.title == pharmcyData[index].name) {
                                val img = Image()
                                try {
                                    img.setBitmap(
                                        createMarkerBitmap(
                                            R.drawable.ic_pharmacy_pin,
                                            marker.title ?: "",
                                            true
                                        )
                                    )
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                                marker.icon = img
                            }
                        }
                    }

                    // stop navigation if it is already started
                    NavigationManager.getInstance().stop()

                    // hide list of pharmacy and practice
                    hidePharmacyAndPracticeList()
                }
            }


        }
    }

}
