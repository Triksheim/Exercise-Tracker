package com.example.exercisetracker

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.exercisetracker.databinding.FragmentSessionDetailsBinding
import com.example.exercisetracker.db.UserProgramSessionData
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class SessionDetailsFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentSessionDetailsBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSessionDetailsBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
        }

        sharedViewModel.sessionData.observe(viewLifecycleOwner) { sessionList ->
            if (::map.isInitialized) {
                updateMap(sessionList)
            }
        }
    }

    private fun updateMap(sessionList: List<UserProgramSessionData>) {
        if (!::map.isInitialized) return

        val coordinates = sessionList.map { LatLng(it.floatData1.toDouble(), it.floatData2.toDouble()) }

        // Clear any existing markers or lines
        map.clear()

        val polylineOptions = PolylineOptions().apply {
            addAll(coordinates)
            width(10f)
            color(Color.RED)
        }

        map.addPolyline(polylineOptions)

        // Add markers
        coordinates.forEach { coordinate ->
            map.addMarker(MarkerOptions().position(coordinate))
        }

        // Move the camera to the first position in the list, if it is not empty
        if (coordinates.isNotEmpty()) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates[0], 15f))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        sharedViewModel.sessionData.value?.let { sessionList ->
            updateMap(sessionList)
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}