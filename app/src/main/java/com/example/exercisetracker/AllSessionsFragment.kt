package com.example.exercisetracker

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
import com.example.exercisetracker.adapters.SessionItemAdapter
import com.example.exercisetracker.databinding.FragmentAllSessionsBinding
import com.example.exercisetracker.db.UserProgramSession
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

class AllSessionsFragment : Fragment() {
    private var _binding: FragmentAllSessionsBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        _binding = FragmentAllSessionsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel


        }
        val adapter = SessionItemAdapter { session ->
            sharedViewModel.setCurrentSession(UserProgramSession(session.sessionId!!, session.userProgramId!!, session.sessionDescription!!, session.sessionStartTime!!, session.sessionTimeSpent!!))
            sharedViewModel.setCurrentDisplayableSession(session)
            findNavController().navigate(R.id.action_allSessionsFragment_to_sessionDetailsFragment)
        }



        val recyclerView = binding.sessionsRecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter



        val dividerItemDecoration = DividerItemDecoration(requireContext(),
            (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedViewModel.displayableSessions.collect { sessions ->
                adapter.submitList(sessions)
            }
        }
    }
}