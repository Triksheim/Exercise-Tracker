package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.adapters.ExerciseItemAdapter
import com.example.exercisetracker.databinding.FragmentMyExercisesBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyExercisesFragment: Fragment() {

    private var _binding: FragmentMyExercisesBinding? = null
    private val binding get() = _binding!!
    private val addButton = 0

    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyExercisesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exerciseClickListener = object: ExerciseItemAdapter.ExerciseClickListener {
            override fun onEditButtonClick(exerciseId: Int) {
                Toast.makeText(context, "Exercise: Edit button clicked", Toast.LENGTH_SHORT).show()
                // navigate to "new exercise" fragment, populate fields with data from chosen exercise
            }

            override fun onAddButtonClick(exerciseId: Int) {
                Toast.makeText(context, "Exercise: Add button clicked", Toast.LENGTH_SHORT).show()
                // Not visible
            }
        }

        val adapter = ExerciseItemAdapter(exerciseClickListener, addButton)

        lifecycleScope.launchWhenStarted {
            sharedViewModel.userExercises.collectLatest { userExercises -> adapter.submitList(userExercises)
                if (userExercises.isEmpty()) {
                    binding.tvNoExercises.visibility = View.VISIBLE
                }else{ binding.tvNoExercises.visibility = View.INVISIBLE }
            }
        }

        binding.apply {
            exerciseRecycler.adapter = adapter
            adapter.notifyDataSetChanged()
            btNewExercise.setOnClickListener {
                // Navigate to NewExerciseFragment
                findNavController().navigate(R.id.action_myExercisesFragment_to_newExerciseFragment)
            }
            btMyPrograms.setOnClickListener {
                // Navigate to MyProgramsFragment
                findNavController().navigate(R.id.action_myExercisesFragment_to_myProgramsFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}