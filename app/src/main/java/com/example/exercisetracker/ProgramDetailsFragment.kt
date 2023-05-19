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
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.adapters.ExerciseItemAdapter
import com.example.exercisetracker.databinding.FragmentProgramDetailsBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

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

        val exerciseClickListener = object: ExerciseItemAdapter.ExerciseClickListener {
            override fun onEditButtonClick(exerciseId: Int) {
                Toast.makeText(context, "Exercise update done", Toast.LENGTH_SHORT).show()
                // navigate to "new exercise" fragment, populate fields with data from chosen exercise
            }

            override fun onAddButtonClick(exerciseId: Int) {
                Toast.makeText(context, "Exercise added to program", Toast.LENGTH_SHORT).show()
                // Add exercise to the current program and create user_program_exercise object
            }

            override fun onRemoveButtonClick(exerciseId: Int) {
                Toast.makeText(context, "Exercise Removed from program", Toast.LENGTH_SHORT).show()
            }
        }

        // From this fragment programId is set. Passing showAddButton to the adapter to show
        // addButton in the exerciseItems when accessed from this fragment
        val adapterBottom = ExerciseItemAdapter(exerciseClickListener, DETAIL_FRAGMENT_BOTTOM)
        val adapterUpper = ExerciseItemAdapter(exerciseClickListener,  DETAIL_FRAGMENT_UPPER)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            program = sharedViewModel.currentProgram.value
            exerciseNameRecycler.adapter = adapterUpper
            exerciseRecycler.adapter = adapterBottom
            buttonStart.setOnClickListener {
                findNavController().navigate(R.id.action_programDetailsFragment_to_ProgramSessionFragment)
            }
            binding.buttonAddExercises.setOnClickListener{
                findNavController().navigate(R.id.action_programDetailsFragment_to_newExerciseFragment)
            }

            adapterBottom.notifyDataSetChanged()
            adapterUpper.notifyDataSetChanged()
        }

        sharedViewModel.activeUser.observe(viewLifecycleOwner, Observer { activeUser ->
            val userId = activeUser.id

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                sharedViewModel.userExercises.collect { userExercises ->
                    val filteredUserExercises = userExercises.filter { it.user_id == userId }
                    adapterBottom.submitList(filteredUserExercises)
                    adapterUpper.submitList(filteredUserExercises)
                }
            }
        })

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


