package com.example.exercisetracker

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.adapters.ExerciseItemAdapter
import com.example.exercisetracker.databinding.FragmentSessionDetailsBinding
import com.example.exercisetracker.db.DisplayableSession
import com.example.exercisetracker.db.UserExercise
import com.example.exercisetracker.db.UserProgramSession
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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

        // Observe currentDiaplayableSession, bind viewmodel and views
        sharedViewModel.currentDisplayableSession.observe(this.viewLifecycleOwner) { displayableSession ->
            // Set current program for this session
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                sharedViewModel.userPrograms.collect() { userPrograms ->
                    val sessionProgram =
                        userPrograms.find { it.id == displayableSession.userProgramId }
                    if (sessionProgram != null) {
                        sharedViewModel.setCurrentUserProgram(sessionProgram)
                    } else { Toast.makeText(
                            requireContext(),
                            getString(R.string.failed_to_load_exercises),
                            Toast.LENGTH_SHORT)
                            .show() }
                }
            }

            binding.apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = sharedViewModel
                // Set program type icon
                val resourceId = resources.getIdentifier(
                    displayableSession.programTypeIcon,
                    "drawable", requireContext().packageName
                )
                sessionIcon.setImageResource(resourceId)

                // Set visibility of views displaying session data based upon user choices
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
                // Bind delete button and replay button
                replaySessionButton.setOnClickListener { replaySession() }
                deleteSessionButton.setOnClickListener { deleteSession(displayableSession) }
            }
        }

        sharedViewModel.sessionData.observe(viewLifecycleOwner) { sessionList ->
            if (sessionList.isEmpty()) {
                binding.map.visibility = View.GONE
            } else {
                if (::map.isInitialized) {
                    updateMap(sessionList)
                }
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

    // Initialize interface for buttons on the exercise-items in recyclerView
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
        val builder = LatLngBounds.Builder()
        for (coordinate in coordinates) {
            builder.include(coordinate)
        }

        // Check if there are coordinates to be displayed
        if (coordinates.isNotEmpty()) {
            val bounds = builder.build()

            // Move the camera to show all markers
            val padding = 100 // offset from edges of the map in pixels
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)

            // Post a Runnable to the view's message queue to delay the camera update
            binding.map.post {
                try {
                    map.moveCamera(cameraUpdate)
                } catch (e: Exception) {
                    // In case of an exception, print or handle the error as needed
                    e.printStackTrace()
                }
            }
        } else {
            // There are no coordinates to be displayed
            // Move the camera to Narvik by default
            val narvik = LatLng(68.438498, 17.427261)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(narvik, 10f)) // zoom level 10
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        sharedViewModel.sessionData.value?.let { sessionDataList ->
            updateMap(sessionDataList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaySession(){
       // current program is already set for this session
       findNavController().navigate(R.id.action_sessionDetailsFragment_to_programSessionFragment)

    }

    private fun deleteSession(displayableSession: DisplayableSession) {
               val negativeButtonClick =  { dialog: DialogInterface, which: Int ->
            Toast.makeText(requireContext(), getString(R.string.canceled), Toast.LENGTH_SHORT).show()
        }

        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            // Delete UserProgramSession and UserProgramSessionData
            sharedViewModel.deleteProgramSessionByDisplayedSession(displayableSession)

            findNavController().navigate(R.id.action_sessionDetailsFragment_to_allSessionsFragment)
        }

        // Create dialog and display it to user
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Slett Treningsøkt")
            .setMessage("Vil du slette denne treningsøkten?" +
                    " All data slettes for godt")
            .setPositiveButton("Slett treningsøkt", positiveButtonClick)
            .setNegativeButton(getString(R.string.cancel), negativeButtonClick)
            .show()

    }
}