package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.adapters.ExerciseItemAdapter
import com.example.exercisetracker.databinding.FragmentProgramDetailsBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

class ProgramDetailsFragment: Fragment() {
    private val navigationArgs: ProgramDetailsFragmentArgs by navArgs()
    private var _binding: FragmentProgramDetailsBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }
    private val binding get() = _binding!!
    private val addButton = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProgramDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val programId = navigationArgs.userProgramId

        val exerciseClickListener = object: ExerciseItemAdapter.ExerciseClickListener {
            override fun onEditButtonClick(exerciseId: Int) {
                Toast.makeText(context, "Exercise: Edit button clicked", Toast.LENGTH_SHORT).show()
                // navigate to "new exercise" fragment, populate fields with data from chosen exercise
            }

            override fun onAddButtonClick(exerciseId: Int) {
                Toast.makeText(context, "Exercise: Add button clicked", Toast.LENGTH_SHORT).show()
                // Add exercise to the current program.
            }
        }

        val adapter = ExerciseItemAdapter(exerciseClickListener, addButton)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            exerciseRecycler.adapter = adapter
            buttonBack.setOnClickListener {
                findNavController().navigate(R.id.action_programDetailsFragment_to_newProgramFragment)
            }
            binding.buttonAddExercises.setOnClickListener{
                findNavController().navigate(R.id.action_programDetailsFragment_to_newExerciseFragment)
            }

            adapter.notifyDataSetChanged()
        }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


