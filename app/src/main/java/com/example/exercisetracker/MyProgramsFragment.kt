package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.adapters.ProgramItemAdapter
import com.example.exercisetracker.databinding.FragmentMyProgramsBinding
import com.example.exercisetracker.db.AppProgramType
import com.example.exercisetracker.db.UserProgram
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

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
        // Iinitiating the adapter interface to get the programtype for each program
        // to be recycled in ProgramItemAdapter
        val userProgramType = object: ProgramItemAdapter.UserProgramType {
            override fun getProgramTypeForProgram(userProgram: UserProgram): AppProgramType? {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    sharedViewModel.getProgramTypeForProgram(userProgram)
                    }
                return sharedViewModel.userProgramType.value
            }
        }

        val adapter = ProgramItemAdapter(
            userProgramType,
            userProgramClickListener,
            onItemClickListener = { selectedProgram ->
                sharedViewModel.setCurrentUserProgram(selectedProgram)
                findNavController().navigate(R.id.action_myProgramsFragment_to_programDetailsFragment)
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

        sharedViewModel.activeUser.observe(viewLifecycleOwner, Observer { activeUser ->
            val userId = activeUser?.id
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                sharedViewModel.userPrograms.collect { userPrograms ->
                    val filteredUserPrograms = userPrograms.filter { it.user_id == userId }
                    adapter.submitList(filteredUserPrograms)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
