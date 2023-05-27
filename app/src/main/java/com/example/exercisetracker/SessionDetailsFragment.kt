package com.example.exercisetracker

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.exercisetracker.adapters.ExerciseItemAdapter
import com.example.exercisetracker.databinding.FragmentSessionDetailsBinding
import com.example.exercisetracker.db.DisplayableSession
import com.example.exercisetracker.db.UserExercise
import com.example.exercisetracker.db.UserProgramSessionData
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.flow.collect

const val SESSION_DETAILS = "sessionDetailsFragment"

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

        sharedViewModel.setToolbarTitle(getString(R.string.title_session_details))

        sharedViewModel.currentDisplayableSession.observe(this.viewLifecycleOwner) { displayableSession ->
            binding.apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = sharedViewModel

                val resourceId = resources.getIdentifier(
                    displayableSession.programTypeIcon,
                    "drawable", requireContext().packageName
                )
                sessionIcon.setImageResource(resourceId)

                when (displayableSession.useTiming) {
                    0 -> cardTime.visibility = View.GONE
                    1 -> cardTime.visibility = View.VISIBLE
                }
                when (displayableSession.useGps) {
                    0 -> {
                        cardDistance.visibility = View.GONE
                        cardHeight.visibility = View.GONE
                        cardSpeed.visibility = View.GONE
                    }
                    1 -> {
                        cardDistance.visibility = View.VISIBLE
                        cardHeight.visibility = View.VISIBLE
                        cardSpeed.visibility = View.VISIBLE
                    }
                }
                when (displayableSession.programTypeBackColor) {
                    OUTDOORCOLOR -> {
                        exerciseRecycler.visibility = View.GONE
                        map.visibility = View.VISIBLE
                    }
                    INDOORCOLOR -> {
                        exerciseRecycler.visibility = View.VISIBLE
                        map.visibility = View.GONE
                        bindExerciseRecycler(displayableSession)
                    }
                }
            }
        }

        sharedViewModel.sessionData.observe(viewLifecycleOwner) { sessionList ->
            if (::map.isInitialized) {
                updateMap(sessionList)
            }
        }
    }

    private fun bindExerciseRecycler(displayableSession: DisplayableSession) {
        val adapter = ExerciseItemAdapter(getExerciseClickListener(), SESSION_DETAILS)
        binding.exerciseRecycler.adapter = adapter

        // Set current program for this session
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedViewModel.userPrograms.collect() {userPrograms ->
                val sessionProgram = userPrograms.find{it.id == displayableSession.userProgramId}
                if (sessionProgram != null) {sharedViewModel.setCurrentUserProgram(sessionProgram!!)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.failed_to_load_exercises), Toast.LENGTH_SHORT).show()
                }

                // Fetch exercises for the current program:
                sharedViewModel.flowExercisesForCurrentProgram()
                // Observe the LiveData of program exercises
                sharedViewModel.userProgramExercises.observe(viewLifecycleOwner, Observer { programExercises ->
                    // Fetch the list of all user exercises
                    val userExercises = sharedViewModel.userExercises.value
                    // Filter the user exercises that are in the program
                    val exercisesForProgram = userExercises.filter { userExercise ->
                        programExercises.any { programExercise ->
                            programExercise.user_exercise_id == userExercise.id
                        }
                    }
                    // Submit the new list of exercises to the adapter
                    adapter.submitList(exercisesForProgram)
                })
            }
        }
    }

    private fun getExerciseClickListener(): ExerciseItemAdapter.ExerciseClickListener {
        return object : ExerciseItemAdapter.ExerciseClickListener {
            override fun onEditButtonClick(userExercise: UserExercise) {} // Not Visible in this fragment
            override fun onAddButtonClick(userExercise: UserExercise) {} // Not Visible in this fragment
            override fun onRemoveButtonClick(userExercise: UserExercise) {} // Not Visible in this fragment
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

        // Calculate LatLngBounds
        if (coordinates.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            for (coordinate in coordinates) {
                builder.include(coordinate)
            }
            val bounds = builder.build()

            // Move the camera to show all markers
            val padding = 50 // offset from edges of the map in pixels
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            map.moveCamera(cameraUpdate)
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