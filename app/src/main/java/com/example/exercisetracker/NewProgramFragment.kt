package com.example.exercisetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.databinding.FragmentNewProgramBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

class NewProgramFragment: Fragment() {

    private val navigationArgs: NewProgramFragmentArgs by navArgs()

    private var _binding: FragmentNewProgramBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
          savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewProgramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val programTypeId = arguments?.getInt("programTypeId") ?: -1
        if (programTypeId == -1) {
            Log.e("NewProgramFragment", "Failed to receive programTypeId")
        }

        // appProgramTypeId needed to create user_program, then pass user_program_id to
        // program_detail_fragment?:
        val appProgramTypeId = navigationArgs.programTypeId
        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_newProgramFragment_to_programTypeFragment)
        }

        binding.buttonSaveProgram.setOnClickListener{
            findNavController().navigate(R.id.action_newProgramFragment_to_programDetailsFragment)
        }

        binding.buttonMyExercises.setOnClickListener {
            findNavController().navigate(R.id.action_newProgramFragment_to_myExercisesFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}