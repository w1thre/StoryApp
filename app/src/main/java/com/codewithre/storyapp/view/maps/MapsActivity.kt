package com.codewithre.storyapp.view.maps

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()

        viewModel.listMapStory.observe(this) { listStoryItems ->
            if (listStoryItems != null) {
                addManyMarker(listStoryItems)
            }
        }

        viewModel.getLocationStories()
    }

    private fun addManyMarker(listStoryItem: List<ListStoryItem>) {
        mMap.clear()
        listStoryItem.forEach { storyItem ->
            val lat = storyItem.lat
            val lon = storyItem.lon
            if (lat != null && lon != null) {
                val position = LatLng(lat, lon)
                mMap.addMarker(MarkerOptions().position(position).title(storyItem.name).snippet(storyItem.description))
            }
        }
        if (listStoryItem.isNotEmpty()) {
            val firstStory = listStoryItem.first()
            val firstLat = firstStory.lat
            val firstLon = firstStory.lon
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