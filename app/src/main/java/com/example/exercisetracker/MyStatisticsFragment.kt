package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.adapters.StatisticsItemAdapter
import com.example.exercisetracker.databinding.FragmentMyStatisticsBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MyStatisticsFragment: Fragment() {
    private var _binding: FragmentMyStatisticsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val replayClickListener = object: StatisticsItemAdapter.ReplayClickListener {
            override fun onReplayButtonClicked(userProgramSessionDataId: Int) {
                Toast.makeText(context, "Session: Replay button clicked", Toast.LENGTH_SHORT).show()
                // navigate to "start session" fragment, populate fields with data from chosen exercise
            }
        }

        val adapter = StatisticsItemAdapter(replayClickListener)

        """
        lifecycleScope.launchWhenStarted {
            sharedViewModel.userProgramSessionsData.collectLatest { userProgramSessionsData -> adapter.submitList(userProgramSessionsData)
            if (userProgramSessionsData.isEmpty()) {
                binding.tvNoSessions.visibility = View.VISIBLE
            }else{ binding.tvNoSessions.visibility = View.INVISIBLE }
            }
        }
        """
        binding.apply{
            statisticsRecycler.adapter = adapter
            adapter.notifyDataSetChanged()
            btRegisterNew.setOnClickListener{
                findNavController().navigate(R.id.action_myStatisticsFragment_to_SecondFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}