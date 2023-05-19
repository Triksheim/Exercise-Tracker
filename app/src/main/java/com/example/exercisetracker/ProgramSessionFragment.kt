package com.example.exercisetracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils.formatElapsedTime
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.exercisetracker.databinding.FragmentProgramSessionBinding
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.db.UserProgramSession
import com.example.exercisetracker.db.UserProgramSessionEntity
import com.example.exercisetracker.repository.TrainingApplication
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ProgramSessionFragment: Fragment() {

    private var _binding: FragmentProgramSessionBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }

    private val binding get() = _binding!!

    private var userProgramId: Int? = null
    private var startTime: Long = 0L
    private var timeSpent: Int = 0
    private var pauseTime: Long = 0L
    private var isWorkoutRunning = false
    private val timerHandler = Handler(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProgramSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProgramId = arguments?.getInt("programId")

        val startPauseButton: Button = view.findViewById(R.id.start_pause_button)
        startPauseButton.setOnClickListener {
            if (isWorkoutRunning) {
                pauseOrContinueWorkoutSession(startPauseButton)
            } else {
                startWorkoutSession(startPauseButton)
            }
        }

        binding.saveWorkoutButton.setOnClickListener {
            // Call saveWorkoutSession inside a coroutine
            viewLifecycleOwner.lifecycleScope.launch {
                saveWorkoutSession()
            }

            // Navigate to MyStatisticsFragment
            val action = ProgramSessionFragmentDirections
            .actionProgramSessionFragmentToMyStatisticsFragment()
            findNavController().navigate(action)
        }
    }

    private suspend fun saveWorkoutSession() {
        if (isWorkoutRunning) {
            timeSpent += ((System.currentTimeMillis() - startTime) / 1000).toInt()
            isWorkoutRunning = false
            timerHandler.removeCallbacks(timerRunnable)
        }
        val timestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startTimeStr = dateFormat.format(Date(timestamp))
        val userProgramSession = UserProgramSession(
            id = 0,
            user_program_id = 0,
            startTime = startTimeStr,
            time_spent = timeSpent,
            description = binding.personalDescriptionEditText.text.toString()
        )
        sharedViewModel.createUserProgramSession(userProgramSession)
    }
    private fun startWorkoutSession(button: Button) {
        if (!isWorkoutRunning) {
            if (pauseTime > 0L) {
                val pauseDuration = System.currentTimeMillis() - pauseTime
                startTime += pauseDuration
                pauseTime = 0L
            } else {
                startTime = System.currentTimeMillis()
            }
            isWorkoutRunning = true
            timerHandler.postDelayed(timerRunnable, 0)
            updateElapsedTime()
            button.text = "Pause"
        }
    }

    private fun pauseOrContinueWorkoutSession(button: Button) {
        if (isWorkoutRunning) {
            pauseWorkoutSession(button)
        } else {
            startWorkoutSession(button)
        }
    }

    private fun pauseWorkoutSession(button: Button) {
        timeSpent += ((System.currentTimeMillis() - startTime) / 1000).toInt()
        isWorkoutRunning = false
        pauseTime = System.currentTimeMillis()
        timerHandler.removeCallbacks(timerRunnable)
        button.text = "Fortsett"
    }

    private fun updateElapsedTime() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - startTime + timeSpent
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}