package com.codewithre.storyapp.view.maps

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.codewithre.storyapp.R
import com.codewithre.storyapp.data.remote.response.ListStoryItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.codewithre.storyapp.databinding.ActivityMapsBinding
import com.codewithre.storyapp.view.ViewModelFactory
import com.codewithre.storyapp.view.main.MainViewModel
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()

        viewModel.listMapStory.observe(this, Observer { listStoryItems ->
            if (listStoryItems != null) {
                addManyMarker(listStoryItems)
            }
        })

        viewModel.getLocationStories()
    }

    private fun addManyMarker(listStoryItem: List<ListStoryItem>) {
        mMap.clear()
        listStoryItem.forEach { storyItem ->
            val lat = storyItem.lat as? Double
            val lon = storyItem.lon as? Double
            if (lat != null && lon != null) {
                val position = LatLng(lat, lon)
                mMap.addMarker(MarkerOptions().position(position).title(storyItem.name).snippet(storyItem.description))
            }
        }
        if (listStoryItem.isNotEmpty()) {
            val firstStory = listStoryItem.first()
            val firstLat = firstStory.lat as? Double
            val firstLon = firstStory.lon as? Double
            if (firstLat != null && firstLon != null) {
                val firstPosition = LatLng(firstLat, firstLon)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstPosition, 5f))
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}