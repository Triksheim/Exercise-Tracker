package com.example.exercisetracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.exercisetracker.databinding.FragmentProgramSessionBinding
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import com.example.exercisetracker.db.UserProgramSession
import com.example.exercisetracker.db.UserProgramSessionEntity
import com.example.exercisetracker.repository.TrainingApplication
import java.text.SimpleDateFormat
import java.util.*

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

        val startWorkoutButton: Button = view.findViewById(R.id.start_workout_button)
        startWorkoutButton.setOnClickListener {
            startWorkoutSession()
        }

        val stopPauseButton: Button = view.findViewById(R.id.stop_pause_button)
        stopPauseButton.setOnClickListener {
            pauseOrContinueWorkoutSession(stopPauseButton)
        }

        val saveWorkoutButton: Button = view.findViewById(R.id.save_workout_button)
        saveWorkoutButton.setOnClickListener {
            saveWorkoutSession()
        }
    }

    private fun startWorkoutSession() {
        val timestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startTimeStr = dateFormat.format(Date(timestamp))
        val userProgramSession = UserProgramSession(
            id = 0,
            user_program_id = userProgramId ?: return,
            startTime = startTimeStr,
            time_spent = 0,
            description = ""
        )
        sharedViewModel.addUserProgramSession(userProgramSession)
        if (!isWorkoutRunning) {
            startTime = System.currentTimeMillis()
            isWorkoutRunning = true
            timerHandler.postDelayed(timerRunnable, 0)
        }
    }

    private fun pauseOrContinueWorkoutSession(stopPauseButton: Button) {
        if (isWorkoutRunning) {
            timeSpent += ((System.currentTimeMillis() - startTime) / 1000).toInt()
            isWorkoutRunning = false
            timerHandler.removeCallbacks(timerRunnable)
            stopPauseButton.text = "Fortsett"
        } else {
            startTime = System.currentTimeMillis()
            isWorkoutRunning = true
            timerHandler.postDelayed(timerRunnable, 0)
            stopPauseButton.text = "Stopp/pause"
        }
    }

    private fun saveWorkoutSession() {
        if (isWorkoutRunning) {
            timeSpent += ((System.currentTimeMillis() - startTime) / 1000).toInt()
            isWorkoutRunning = false
            timerHandler.removeCallbacks(timerRunnable)
        }
        // Save the timeSpent to the UserProgramSession object or your data storage
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isWorkoutRunning) {
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - startTime + timeSpent
                // Update your UI with elapsedTime
                timerHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}