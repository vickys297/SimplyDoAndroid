package com.example.simplydo.ui.fragments.attachmentsFragments.map

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.simplydo.R
import com.example.simplydo.databinding.FragmentMapsBinding
import com.example.simplydo.utils.AppConstant
import com.example.simplydo.utils.AppFunctions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

internal val TAG = LocationFragment::class.java.canonicalName

class LocationFragment : Fragment(R.layout.fragment_maps) {


    private lateinit var locationRequest: LocationRequest
    private lateinit var locationManager: LocationManager
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationTask: Task<Location>
    private lateinit var latLngLocation: LatLng

    private lateinit var map: GoogleMap

    private lateinit var _binding: FragmentMapsBinding
    val binding: FragmentMapsBinding get() = _binding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult ?: return
            for (location in locationResult.locations) {
                // Update UI with location data
                // ...
                latLngLocation = LatLng(location.latitude, location.longitude)
                newMarkerLocation()
            }
        }

    }

    private val callback = OnMapReadyCallback { googleMap ->

        map = googleMap
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the account will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * account has installed Google Play services and returned to the app.
         */

        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.map_styled_json
            )
        )

        newMarkerLocation()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 12f))
        googleMap.setOnMapClickListener {
            latLngLocation = it
            newMarkerLocation()
        }
    }

    private fun newMarkerLocation() {
        map.clear()
        map.apply {
            addMarker(
                MarkerOptions()
                    .position(latLngLocation)
                    .icon(
                        AppFunctions.getDrawableToBitmap(
                            R.drawable.ic_map_marker,
                            requireActivity()
                        )
                    )
            )
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapsBinding.bind(view)
        latLngLocation = LatLng(11.082096, 77.032576)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            AppConstant.Key.NAVIGATION_LOCATION_DATA_KEY,
            latLngLocation
        )


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())



        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.let {
                latLngLocation = LatLng(it.latitude, it.longitude)
                newMarkerLocation()
            }
        }
        binding.buttonSelectLocation.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                AppConstant.Key.NAVIGATION_LOCATION_DATA_KEY,
                latLngLocation
            )
            findNavController().popBackStack()
        }

        binding.imageButtonClose.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.floatingActionButton.setOnClickListener {
            if (isLocationEnabled(requireContext())) {
                Log.i(TAG, "onViewCreated: ${isLocationEnabled(requireContext())}")
                if (isLocationPermissionGranted()) {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        it?.let {
                            Log.i(
                                TAG, "setupLocationTaskListener: $it\n " +
                                        "${it.latitude}\n" +
                                        "${it.longitude}\n" +
                                        "${it.accuracy}\n" +
                                        "${it.bearing}\n"
                            )
                            latLngLocation = LatLng(it.latitude, it.longitude)
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 12f))
                            newMarkerLocation()
                        }
                    }

                    fusedLocationClient.lastLocation.addOnFailureListener {
                        Log.e(TAG, "setupLocationTaskListener: ", it)
                    }
                } else {
                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            } else {
                showEnableLocationServiceDialog()
            }
        }


        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        it?.let {
                            Log.i(
                                TAG, "setupLocationTaskListener: $it\n " +
                                        "${it.latitude}\n" +
                                        "${it.longitude}\n" +
                                        "${it.accuracy}\n" +
                                        "${it.bearing}\n"
                            )
                            latLngLocation = LatLng(it.latitude, it.longitude)
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 12f))
                            newMarkerLocation()
                        }
                    }

                    fusedLocationClient.lastLocation.addOnFailureListener {
                        Log.e(TAG, "setupLocationTaskListener: ", it)
                    }
                }
            }


        checkForPermission()
    }

    override fun onResume() {
        super.onResume()

        requestLocationUpdateService()
    }

    private fun requestLocationUpdateService() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                com.google.android.gms.location.LocationRequest.create(),
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        stopRequestLocationUpdateService()
    }

    private fun stopRequestLocationUpdateService() {


    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkForPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // has access for location
                locationTask = fusedLocationClient.lastLocation

            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {

            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    private fun showEnableLocationServiceDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("GPS is disabled")
            .setMessage("Enable GPS to get current location")
            .setPositiveButton("Ok") { dialog, which ->
                requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog.dismiss()
            }.show()
    }

    private fun isLocationEnabled(context: Context): Boolean {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

}