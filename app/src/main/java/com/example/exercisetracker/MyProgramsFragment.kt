package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.adapters.ProgramItemAdapter
import com.example.exercisetracker.databinding.FragmentMyProgramsBinding
import com.example.exercisetracker.db.AppProgramType
import com.example.exercisetracker.db.UserProgram
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.launch

class MyProgramsFragment : Fragment() {
    private var _binding: FragmentMyProgramsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProgramsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.setToolbarTitle(getString(R.string.title_my_programs))

        // Override fun in Click-interface for buttons on ProgramItems
        val userProgramClickListener = object: ProgramItemAdapter.UserProgramClickListener {
            override fun onEditButtonClick(userProgram: UserProgram) {
                sharedViewModel.setCurrentUserProgram(userProgram)
                val actionEditProgram = MyProgramsFragmentDirections
                    .actionMyProgramsFragmentToNewProgramFragment(userProgram.app_program_type_id, userProgram.id)
                findNavController().navigate(actionEditProgram)
            }
            override fun onStartProgramButtonClick(userProgram: UserProgram) {
                sharedViewModel.setCurrentUserProgram(userProgram)
                findNavController().navigate(R.id.action_myProgramsFragment_to_programSessionFragment)
            }
        }

        // Override fun in Interface to get the programtype for each program-item in ProgramItemAdapter
        val userProgramType = object: ProgramItemAdapter.UserProgramType {
            override fun getProgramTypeForProgram(userProgram: UserProgram): AppProgramType? {
                return sharedViewModel.getProgramTypeForProgram(userProgram)
            }
        }

        // Initialize the adapter, passing the interface objects to retrieve type, and handle button clicks
        val adapter = ProgramItemAdapter(
            userProgramType,
            userProgramClickListener,
            onItemClickListener = { selectedProgram ->
                sharedViewModel.setCurrentUserProgram(selectedProgram)
                sharedViewModel.setProgramTypeByUserProgram(selectedProgram)
                val action = MyProgramsFragmentDirections.actionMyProgramsFragmentToProgramDetailsFragment(selectedProgram.id)
                findNavController().navigate(action)
            }
        )

        binding.apply {
            binding.programRecycler.adapter = adapter
            adapter.notifyDataSetChanged()
            // New program-button navigates back to second fragment to start creating a new Program
            buttonNewProgram.setOnClickListener{
                val actionNewProgram = MyProgramsFragmentDirections.actionMyProgramsFragmentToSecondFragment()
                findNavController().navigate(actionNewProgram)
            }
        }

        // Get data for the adapter from the viewModel
        sharedViewModel.activeUser.observe(viewLifecycleOwner, Observer { activeUser ->
            val userId = activeUser?.id
            viewLifecycleOwner.lifecycleScope.launch {
                sharedViewModel.userPrograms.flowWithLifecycle(viewLifecycleOwner.lifecycle,
                    Lifecycle.State.STARTED)
                    .collect { userPrograms ->
                    val filteredUserPrograms = userPrograms.filter { it.user_id == userId }
                    val sortedFilteredPrograms = sortProgramsByBackColor(filteredUserPrograms)
                    adapter.submitList(sortedFilteredPrograms)
                }
            }
        })

    }

    private fun sortProgramsByBackColor(userPrograms: List<UserProgram>): List<UserProgram> {
        val sortedList = userPrograms.sortedBy { userProgram ->
            val appProgramType = sharedViewModel.getProgramTypeForProgram(userProgram)
            appProgramType?.back_color
        }
        return sortedList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
