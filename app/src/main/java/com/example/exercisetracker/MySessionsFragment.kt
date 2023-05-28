package com.example.exercisetracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exercisetracker.adapters.SessionItemAdapter
import com.example.exercisetracker.databinding.FragmentMySessionsBinding
import com.example.exercisetracker.databinding.FragmentProgramTypeBinding
import com.example.exercisetracker.db.UserProgramSession
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

class MySessionsFragment : Fragment() {
    private var _binding: FragmentMySessionsBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        _binding = FragmentMySessionsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.setToolbarTitle(getString(R.string.trainning_statistics))

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            newSessionButton.setOnClickListener{
                findNavController().navigate(R.id.action_mySessionsFragment_to_myProgramsFragment)
            }

        }
        val adapter = SessionItemAdapter { session ->
            sharedViewModel.setCurrentSession(UserProgramSession(session.sessionId!!, session.userProgramId!!, session.sessionDescription!!, session.sessionStartTime!!, session.sessionTimeSpent!!))
            sharedViewModel.setCurrentDisplayableSession(session)
            findNavController().navigate(R.id.action_mySessionsFragment_to_sessionDetailsFragment)
        }



        val recyclerView = binding.sessionsRecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        //recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter



        val dividerItemDecoration = DividerItemDecoration(requireContext(),
            (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedViewModel.displayableSessions.collect { sessions ->
                val limitedSessions = if (sessions.size > 5) sessions.takeLast(5) else sessions
                adapter.submitList(limitedSessions)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}