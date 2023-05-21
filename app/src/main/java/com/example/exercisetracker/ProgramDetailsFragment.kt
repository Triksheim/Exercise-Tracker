package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.adapters.ExerciseItemAdapter
import com.example.exercisetracker.databinding.FragmentProgramDetailsBinding
import com.example.exercisetracker.db.UserProgramExercise
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.flow.filter

const val DETAIL_FRAGMENT_UPPER = "detailFragmentUpper"
const val DETAIL_FRAGMENT_BOTTOM = "detailFragmentBottom"

class ProgramDetailsFragment: Fragment() {
    private val navigationArgs: ProgramDetailsFragmentArgs by navArgs()
    private var _binding: FragmentProgramDetailsBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }
    private val binding get() = _binding!!
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

        val exerciseClickListener = object : ExerciseItemAdapter.ExerciseClickListener {
            override fun onEditButtonClick(exerciseId: Int) {
                Toast.makeText(context, "Exercise update done", Toast.LENGTH_SHORT).show()
                // navigate to "new exercise" fragment, populate fields with data from chosen exercise
            }

            override fun onAddButtonClick(exerciseId: Int) {
                Toast.makeText(context, "Exercise added to program", Toast.LENGTH_SHORT).show()

                val selectedExercise = sharedViewModel.userExercises.value.find { it.id == exerciseId }
                selectedExercise?.let { exercise ->
                    val newProgramExercise = UserProgramExercise(
                        id = 0, // Provide an appropriate ID if needed
                        user_program_id = sharedViewModel.currentProgram.value!!.id,
                        user_exercise_id = exercise.id
                    )
                    sharedViewModel.addUserExerciseToUserProgram(newProgramExercise)
                }
            }

            override fun onRemoveButtonClick(exerciseId: Int) {
                Toast.makeText(context, "Exercise Removed from program", Toast.LENGTH_SHORT).show()

                val selectedExercise = sharedViewModel.userExercises.value.find { it.id == exerciseId }
                selectedExercise?.let { exercise ->
                    val programExercise = sharedViewModel.userProgramExercises.value?.find { it.user_exercise_id == exercise.id }
                    programExercise?.let { userProgramExercise ->
                        sharedViewModel.deleteUserProgramExercise(userProgramExercise)
                    }
                }
            }
        }

        // From this fragment programId is set. Passing showAddButton to the adapter to show
        // addButton in the exerciseItems when accessed from this fragment
        val programExercisesAdapter =
            ExerciseItemAdapter(exerciseClickListener, DETAIL_FRAGMENT_UPPER)
        val otherExercisesAdapter =
            ExerciseItemAdapter(exerciseClickListener, DETAIL_FRAGMENT_BOTTOM)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            program = sharedViewModel.currentProgram.value
            programExerciseRecycler.adapter = programExercisesAdapter
            otherExerciseRecycler.adapter = otherExercisesAdapter
            buttonStart.setOnClickListener {
                findNavController().navigate(R.id.action_programDetailsFragment_to_ProgramSessionFragment)
            }
            binding.buttonAddExercises.setOnClickListener {
                findNavController().navigate(R.id.action_programDetailsFragment_to_newExerciseFragment)
            }

            programExercisesAdapter.notifyDataSetChanged()
            otherExercisesAdapter.notifyDataSetChanged()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedViewModel.userProgramExercises.observe(viewLifecycleOwner) { programExercises ->
                val userExercises = sharedViewModel.userExercises.value
                val convertedProgramExercises = programExercises.mapNotNull { programExercise ->
                    userExercises.find { it.id == programExercise.user_exercise_id }
                }
                val otherExercises = userExercises.filter { userExercise ->
                    convertedProgramExercises.none { it.id == userExercise.id }
                }

                programExercisesAdapter.submitList(convertedProgramExercises)
                otherExercisesAdapter.submitList(otherExercises)

                // Handle visibility of "no exercise" message
                binding.noExerciseMessage.isVisible = convertedProgramExercises.isEmpty() && otherExercises.isEmpty()
            }
        }
        // Fetch the user program exercises for the current program
        sharedViewModel.fetchExercisesForCurrentProgram()
    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


