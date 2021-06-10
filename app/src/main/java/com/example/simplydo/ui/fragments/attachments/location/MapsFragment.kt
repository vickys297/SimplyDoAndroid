package com.example.simplydo.ui.fragments.attachments.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.simplydo.R
import com.example.simplydo.utli.AppFunctions
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

internal val TAG = MapsFragment::class.java.canonicalName

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),
            R.raw.map_styled_json))

        val home = LatLng(11.082096, 77.032576)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 12f))
        googleMap.addMarker(
            MarkerOptions()
                .position(home)
                .title("Marker in Sydney")
                .icon(AppFunctions.getDrawableToBitmap(R.drawable.ic_map_marker, requireActivity())))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(home))


        googleMap.setOnMapClickListener {
            val newMarker = it

            googleMap.clear()

            googleMap.addMarker(
                MarkerOptions()
                    .position(newMarker)
                    .icon(AppFunctions.getDrawableToBitmap(R.drawable.ic_map_marker,
                        requireActivity())))
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment


        Places.initialize(requireContext(),"AIzaSyCGc2VLW1Xg1Bj1KuOH_8e4VVN53Q85fBg")

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.d(TAG, "Place: ${place.name}, ${place.id}")
                Log.d(TAG, "onPlaceSelected: latlng ${place.latLng}")
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.d(TAG, "An error occurred: $status")
            }
        })

    }
}