package com.example.storyins.ui.main.story.maps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyins.R
import com.example.storyins.databinding.ActivityMapsBinding
import com.example.storyins.preference.UserPreference
import com.example.storyins.source.model.Wrapper
import com.example.storyins.source.model.local.entity.StoryEntity
import com.example.storyins.source.model.local.entity.UserLocation
import com.example.storyins.ui.main.MainViewModel
import com.example.storyins.ui.factory.StoryViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val android.content.Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var activityMapsBinding: ActivityMapsBinding
    private lateinit var mMap : GoogleMap
    private lateinit var mainViewModel: MainViewModel
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var userPreference: UserPreference
    private  var data : StoryEntity?=null
    private  var progressBar : Dialog?=null


    companion object {
        const val LOCATION_USER="location_user"
        const val GEOFENCE_RADIUS=400.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMapsBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(activityMapsBinding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mainViewModel = ViewModelProvider(this , StoryViewModelFactory(this))[MainViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        userPreference = UserPreference.getInstance(this.dataStore)

        data = intent.getParcelableExtra(LOCATION_USER)

        viewLoading()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true


        getMyLocation()
        setMapStyle()

        if(data != null){
            userLocation()
        }else{
            addManyMarker()
        }


        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pointOfInterest.latLng, 15f))
            poiMarker?.showInfoWindow()
        }



    }




    private fun userLocation(){

        val userLocation = LatLng(data?.lat!!.toDouble(), data?.lon!!.toDouble())
        val userAddress = getAddress(data?.lat!!.toDouble(), data?.lon!!.toDouble())

        mMap.addMarker(
            MarkerOptions()
                .position(userLocation)
                .title(getString(R.string.location))
                .snippet(userAddress)
        )

        mMap.addCircle(
            CircleOptions()
                .center(userLocation)
                .radius(GEOFENCE_RADIUS)
                .fillColor(0x22536DFE)
                .strokeColor(Color.BLUE)
                .strokeWidth(3f)
        )

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
    }


    private fun addManyMarker() {

        val token = runBlocking { "Bearer ${userPreference.getToken().first()}" }

        mainViewModel.getStoriesWithLocation(token).observe(this) {
            when (it) {
                is Wrapper.Loading -> {
                    showLoading()
                }
                is Wrapper.Success -> {
                    dismissLoading()
                    showAnnyMarker(it.data)
                }
                is Wrapper.Error -> {
                    dismissLoading()
                    showDialogError(it.msg)
                }
                else -> {
                    dismissLoading()
                    Toast.makeText(this, "Error get maps", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun showAnnyMarker(data: List<StoryEntity>) {
        val boundsBuilder = LatLngBounds.Builder()

        data.map { locationAny ->
            val latLng = LatLng(locationAny.lat!!.toDouble(), locationAny.lon!!.toDouble())
            val addressName = getAddress(locationAny.lat!!.toDouble(), locationAny.lon!!.toDouble())
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(locationAny.name)
                    .snippet(addressName)

            )
            boundsBuilder.include(latLng)

        }
        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                150
            )
        )

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.standard_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }



    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if(it != null){
                    val location = UserLocation(it.latitude.toString(),it.longitude.toString())
                    mainViewModel.setLocation(location)
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }





    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_syle))
            if (!success) {
                Log.d("error style", "Style map error")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.d("error", "message ", exception)
        }
    }


    private fun getAddress(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        try {
            val location = geocoder.getFromLocation(lat, lon, 1)
            if (location != null && location.size != 0) {
                addressName = location[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    private fun showDialogError(message : String?){
        AlertDialog.Builder(this).apply {
            setMessage(message)
            setTitle(R.string.error_title)
            setNegativeButton(getString(R.string.cancel)
            ) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            create()
            show()
        }
    }

    @SuppressLint("InflateParams")
    private fun viewLoading(){
        progressBar = Dialog(this)
        val progressbarLayout = layoutInflater.inflate(R.layout.dialog_progressbar , null)

        progressBar?.let {
            it.setContentView(progressbarLayout)
            it.setCancelable(false)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }


    private fun showLoading() {
        progressBar?.show()
    }

    private fun dismissLoading() {
        progressBar?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissLoading()
    }


}
