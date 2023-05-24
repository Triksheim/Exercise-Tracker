package com.example.exercisetracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.databinding.FragmentProgramSessionBinding
import com.example.exercisetracker.db.UserProgram
import com.example.exercisetracker.db.UserProgramSession
import com.example.exercisetracker.db.UserProgramSessionData
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.android.gms.location.LocationRequest
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exercisetracker.adapters.ExerciseItemAdapter
import com.example.exercisetracker.db.UserExercise

const val PROGRAM_SESSION_FRAGMENT = "ProgramSessionFragment"

class ProgramSessionFragment: Fragment() {

    private var _binding: FragmentProgramSessionBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }

    private val binding get() = _binding!!

    private var sessionId: Int? = null

    // Timer variables
    private var startTime: Long = 0L
    private var timeSpent: Int = 0
    private var pauseStartTime: Long = 0L
    private var pauseDuration: Long = 0L
    private var isWorkoutRunning = false
    private val timerHandler = Handler(Looper.getMainLooper())

    // GPS variables
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val userProgramSessionDataList = mutableListOf<UserProgramSessionData>()

    // Photo and camera variables
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_GALLERY = 2
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProgramSessionBinding.inflate(inflater, container, false)

        // initialize RecyclerView and nested scroll
        val recyclerView = binding.exercisesContainer
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.isNestedScrollingEnabled = false
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.setToolbarTitle(getString(R.string.title_my_statistics))

        val currentProgram = sharedViewModel.currentProgram.value


        // Exercise items
        // Set the adapter for the RecyclerView
        // Buttons are invisible and will never be called in this fragment
        val exerciseItemAdapter = ExerciseItemAdapter(object: ExerciseItemAdapter.ExerciseClickListener {
            override fun onEditButtonClick(userExercise: UserExercise) {}
            override fun onAddButtonClick(userExercise: UserExercise) {}
            override fun onRemoveButtonClick(userExercise: UserExercise) {}
        }, PROGRAM_SESSION_FRAGMENT)

        binding.exercisesContainer.adapter = exerciseItemAdapter
        binding.exercisesContainer.layoutManager = LinearLayoutManager(context)

        // Call the function in ViewModel to fetch exercises
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
            exerciseItemAdapter.submitList(exercisesForProgram)
        })

        // Photo and image buttons
        imageView = binding.imageviewExercise

        val buttonCamera = binding.buttonCamera
        buttonCamera.setOnClickListener {
            dispatchTakePictureIntent()
        }

        val buttonGallery = binding.buttonGallery
        buttonGallery.setOnClickListener {
            dispatchGalleryIntent()

        }

        // GPS and location variables
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        binding.rbGpsNo.isChecked = true
        binding.gpsOptions.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_gps_yes -> {
                    // The "Yes" option is selected
                        if (checkLocationPermission()) {
                            // Location permission granted, start location updates
                            startLocationUpdates()
                        } else {
                            // Location permission not granted
                            requestLocationPermission()
                        }
                    }
                R.id.rb_gps_no -> {
                    // The "No" option is selected
                    stopLocationUpdates()
                }
            }
        }

        val startPauseButton: Button = view.findViewById(R.id.start_pause_button)
        startPauseButton.setOnClickListener {
            if (isWorkoutRunning) {
                pauseOrContinueWorkoutSession(startPauseButton)
            } else {
                startWorkoutSession(startPauseButton)
                startLocationUpdates()
            }
        }

        binding.saveWorkoutButton.setOnClickListener {
            // Call saveWorkoutSession inside a coroutine
            viewLifecycleOwner.lifecycleScope.launch {
                stopLocationUpdates()
                saveWorkoutSession(currentProgram!!)

                // Upload the location data to the database if the user selected the "Yes" option
                if (binding.rbGpsYes.isChecked) {
                    uploadLocationDataToDatabase(userProgramSessionDataList)
                }

                // Navigate to MyStatisticsFragment after saving the session and uploading the data
                val action = ProgramSessionFragmentDirections
                    .actionProgramSessionFragmentToMyStatisticsFragment()
                findNavController().navigate(action)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun saveWorkoutSession(currentProgram: UserProgram) {
        if (isWorkoutRunning) {
            val currentTime = System.currentTimeMillis()
            pauseDuration = currentTime - pauseStartTime
            timeSpent += ((currentTime - startTime - pauseDuration) / 1000).toInt()
            isWorkoutRunning = false
            timerHandler.removeCallbacks(timerRunnable)
        }
        val timestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startTimeStr = dateFormat.format(Date(timestamp))
        val userProgramSession = UserProgramSession(
            id = 0,
            user_program_id = currentProgram.id,
            startTime = startTimeStr,
            time_spent = timeSpent,
            description = binding.personalDescriptionEditText.text.toString()
        )
        sessionId = sharedViewModel.createUserProgramSession(userProgramSession)
    }

    private fun startWorkoutSession(button: Button) {
        if (!isWorkoutRunning) {
            if (pauseDuration > 0L) {
                startTime += pauseDuration
                pauseDuration = 0L
            } else {
                startTime = System.currentTimeMillis()
            }
            isWorkoutRunning = true
            timerHandler.postDelayed(timerRunnable, 0)
            updateElapsedTime()
            button.text = "Pause"
            startLocationUpdates()
        }
    }

    private fun pauseOrContinueWorkoutSession(button: Button) {
        if (isWorkoutRunning) {
            pauseWorkoutSession(button)
            stopLocationUpdates()
        } else {
            startWorkoutSession(button)
            startLocationUpdates()
        }
    }

    private fun pauseWorkoutSession(button: Button) {
        val currentTime = System.currentTimeMillis()
        pauseStartTime = currentTime
        timeSpent += ((currentTime - startTime) / 1000).toInt()
        isWorkoutRunning = false
        timerHandler.removeCallbacks(timerRunnable)
        button.text = "Fortsett"
    }

    // Timer functions
    private fun updateElapsedTime() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - startTime + timeSpent * 1000
        val formattedTime = formatElapsedTime(elapsedTime)
        binding.timerText.text = formattedTime
    }

    private fun formatElapsedTime(elapsedTime: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isWorkoutRunning) {
                updateElapsedTime() // Update the elapsed time
                timerHandler.postDelayed(this, 1000)
            }
        }
    }

    // GPS functions
    private fun startLocationUpdates() {
        if (checkLocationPermission()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).apply {
                setMinUpdateDistanceMeters(10.0f)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        // Call the function to update the location data
                        updateLocation(location)
                    }
                }
            }
            locationRequest?.let { fusedLocationClient.requestLocationUpdates(it,
                locationCallback as LocationCallback, null) }
        } else {
            requestLocationPermission()
        }
    }

    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        locationCallback = null
    }

    override fun onResume() {
        super.onResume()
        if (isWorkoutRunning) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun updateLocation(location: Location) {
        // Create a UserProgramSessionData object with the location data
        val userProgramSessionData = UserProgramSessionData(
            id = 0,
            user_program_session_id = 0, // To be set at a later time
            floatData1 = location.latitude.toFloat(),  // Latitude
            floatData2 = location.longitude.toFloat(),  // Longitude
            floatData3 = location.altitude.toFloat(),  // Altitude
            textData1 = location.time.toString()  // Timestamp
        )
        // Add the UserProgramSessionData object to the list
        userProgramSessionDataList.add(userProgramSessionData)
    }


    private fun uploadLocationDataToDatabase(userProgramSessionDataList: List<UserProgramSessionData>) {
        // Update the user_program_session_id of each location data in the list
        userProgramSessionDataList.forEach { userProgramSessionData ->
            userProgramSessionData.user_program_session_id = sessionId!!
        }
        // Insert the location data into the database using a single transaction
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.insertUserProgramSessionDataList(userProgramSessionDataList)
        }
    }

    // GPS Permissions
    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
            // Permission granted, start location updates
                startLocationUpdates()
            } else {
            // Permission denied, show an error message
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
            // Show a rationale for requesting the permission to the user
            AlertDialog.Builder(requireContext())
                .setTitle("Location Permission")
                .setMessage("This app requires access to your location to track your workout. Please grant the location permission.")
                .setPositiveButton("OK") { _, _ ->
                // Request the permission after showing the rationale
                    requestPermissionLauncher.launch(permission)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                // Handle the case where the user cancels the permission request
                    dialog.dismiss()
                }
                .show()
        } else {
            // Request the permission directly
            requestPermissionLauncher.launch(permission)
        }
    }

    // Photo and camera functions
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            @Suppress("DEPRECATION")
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            e.localizedMessage?.let { Log.d("DEBUG", it) }
        }
    }

    private fun dispatchGalleryIntent() {
        val galleryIntent = Intent(
            Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        @Suppress("DEPRECATION")
        startActivityForResult(galleryIntent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(bitmap)
        } else if (requestCode == REQUEST_GALLERY && resultCode == AppCompatActivity.RESULT_OK) {
            imageView.setImageURI(data?.data)
        } else {
            Toast.makeText(context, "Feil ved visning av bilde", Toast.LENGTH_SHORT).show()
        }
    }
}
