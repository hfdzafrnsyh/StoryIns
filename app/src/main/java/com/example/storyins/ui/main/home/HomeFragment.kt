package com.example.storyins.ui.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyins.R
import com.example.storyins.databinding.FragmentHomeBinding
import com.example.storyins.preference.UserPreference
import com.example.storyins.source.model.local.entity.StoryEntity
import com.example.storyins.source.model.local.entity.UserLocation
import com.example.storyins.ui.adapter.LoadingStateAdapter
import com.example.storyins.ui.adapter.StoryViewAdapter
import com.example.storyins.ui.main.MainViewModel
import com.example.storyins.ui.main.detail.DetailStoryActivity
import com.example.storyins.ui.main.story.maps.MapsActivity
import com.example.storyins.ui.factory.StoryViewModelFactory
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.*



class HomeFragment : Fragment(), StoryViewAdapter.StoryClickCallback {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    private lateinit var userPreference: UserPreference
    private lateinit var homeViewModel: MainViewModel
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var storyAdapter : StoryViewAdapter
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return fragmentHomeBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreference.getInstance(requireContext().dataStore)
        homeViewModel = ViewModelProvider(this , StoryViewModelFactory(requireContext()))[MainViewModel::class.java]
        storyAdapter = StoryViewAdapter(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create()


        checkLocationUser()
        initView()



    }



    private fun initView(){
        fragmentHomeBinding.rvStory.layoutManager = LinearLayoutManager(context)

        storyAdapter.addLoadStateListener {
            fragmentHomeBinding.progressBars.isVisible = it.refresh is LoadState.Loading
        }


        fragmentHomeBinding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        getStories()


    }

    private fun getStories(){
        val token = runBlocking { "Bearer ${userPreference.getToken().first()}" }
        homeViewModel.stories(token).observe(viewLifecycleOwner) {
            if (it != null) {
                storyAdapter.submitData(lifecycle, it)
            }
        }
    }


    override fun showDetail(story: StoryEntity, optionCompact : ActivityOptionsCompat) {
        val intent = Intent(activity , DetailStoryActivity::class.java)
            intent.putExtra(DetailStoryActivity.DETAIL_STORY , story)
            startActivity(intent, optionCompact.toBundle())
    }

    override fun showDialogLocation(story: StoryEntity) {

        if(story.lat == null && story.lon == null){
            AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.location))
            .setMessage(getString(R.string.nothing_location))
            .setNegativeButton(requireContext().getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
                .create()
                .show()
        }else{
          showLocation(story)
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showLocation(story: StoryEntity){
        val addressName = getAddress(story.lat!!.toDouble(),story.lon!!.toDouble())
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.location))
            .setIcon(requireContext().getDrawable(R.drawable.ic_baseline_location_on_24))
            .setMessage(addressName)
            .setNegativeButton(requireContext().getString(R.string.cancel)){ dialog,_ ->
                dialog.cancel()
            }
            .setPositiveButton(getString(R.string.see_location)){ _, _ ->
                startMapActivity(story)
            }
            .create()
            .show()

    }

    private fun getAddress(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        try {
            val location = geocoder.getFromLocation(lat, lon, 1)
            if (location != null && location.size != 0) {
                addressName = location[0].subAdminArea
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    private fun startMapActivity(story: StoryEntity){
        val intent = Intent(requireContext() , MapsActivity::class.java)
        intent.putExtra(MapsActivity.LOCATION_USER,story)
        startActivity(intent)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                checkLocationUser()
            }
        }

    private fun checkLocationUser() {
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            locationUpdate()
            return
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    @SuppressLint("MissingPermission")
    private fun locationUpdate() {
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates=2
        fusedLocationClient.requestLocationUpdates(
            locationRequest,locationCallback, Looper.getMainLooper()
        )

    }

    private val locationCallback = object  : LocationCallback(){
        override fun onLocationResult(locResult: LocationResult) {
            val lastLocation = locResult.lastLocation
            val location = UserLocation(lastLocation.latitude.toString(),lastLocation.longitude.toString())
            homeViewModel.setLocation(location)
        }

    }

}
