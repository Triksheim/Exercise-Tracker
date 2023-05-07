package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.adapters.ProgramItemAdapter
import com.example.exercisetracker.databinding.FragmentMyProgramsBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

class MyProgramsFragment: Fragment() {

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

        val adapter = ProgramItemAdapter { selectedProgram ->
            val action = MyProgramsFragmentDirections
                .actionMyProgramsFragmentToProgramDetailsFragment()
            val bundle = Bundle().apply {
                putInt("programId", selectedProgram.id)
            }
            findNavController().navigate(action.actionId, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
