package com.example.simplydo.ui.fragments.attachmentsFragments.location

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.simplydo.R
import com.example.simplydo.databinding.FragmentMapsBinding
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppFunctions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

internal val TAG = MapsFragment::class.java.canonicalName

class MapsFragment : Fragment(R.layout.fragment_maps) {

    private lateinit var location: LatLng

    private lateinit var _binding : FragmentMapsBinding
    val binding : FragmentMapsBinding get() = _binding

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

        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.map_styled_json
            )
        )


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
        googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title("Marker in Sydney")
                .icon(AppFunctions.getDrawableToBitmap(R.drawable.ic_map_marker, requireActivity()))
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))


        googleMap.setOnMapClickListener {
            location = it

            googleMap.clear()

            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .icon(AppFunctions.getDrawableToBitmap(R.drawable.ic_map_marker,
                        requireActivity())))
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapsBinding.bind(view)

        location = LatLng(11.082096, 77.032576)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


        binding.buttonSelectLocation.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.NAVIGATION_LOCATION_DATA_KEY, location)
            findNavController().popBackStack()
        }
        binding.imageButtonClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}