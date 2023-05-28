package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
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
import com.example.exercisetracker.db.UserExercise
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

const val DETAIL_FRAGMENT_UPPER = "detailFragmentUpper"
const val DETAIL_FRAGMENT_BOTTOM = "detailFragmentBottom"

class ProgramDetailsFragment: Fragment() {
    private val args: ProgramDetailsFragmentArgs by navArgs()
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
        sharedViewModel.setToolbarTitle(getString(R.string.title_program_details))

        // Buttons on exercise items:
        val exerciseClickListener = object: ExerciseItemAdapter.ExerciseClickListener {
            override fun onEditButtonClick(userExercise: UserExercise) {
                sharedViewModel.setCurrentUserExercise(userExercise)
                val actionEditExercise =
                    ProgramDetailsFragmentDirections.actionProgramDetailsFragmentToNewExerciseFragment(
                        userExercise.id
                    )
                findNavController().navigate(actionEditExercise)
            }

            override fun onAddButtonClick(userExercise: UserExercise) {

                val selectedExercise = sharedViewModel.userExercises.value.find {
                    it.id == userExercise.id
                }
                selectedExercise?.let { exercise ->
                    val newProgramExercise = UserProgramExercise(
                        id = 0,
                        user_program_id = args.userProgramId,
                        user_exercise_id = exercise.id
                    )
                    sharedViewModel.addUserExerciseToUserProgram(newProgramExercise)
                }
            }

            override fun onRemoveButtonClick(userExercise: UserExercise) {
                Toast.makeText(context, "Exercise Removed from program", Toast.LENGTH_SHORT).show()

                val selectedExercise =
                    sharedViewModel.userExercises.value.find { it.id == userExercise.id }
                selectedExercise?.let { exercise ->
                    val programExercise =
                        sharedViewModel.userProgramExercises.value?.find { it.user_exercise_id == exercise.id }
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

        sharedViewModel.currentProgram.observe(this.viewLifecycleOwner) {currentProgram ->
            binding.apply {
                lifecycleOwner = viewLifecycleOwner
                program = currentProgram
                buttonStart.setOnClickListener {
                    findNavController().navigate(R.id.action_programDetailsFragment_to_ProgramSessionFragment)
                }
            }
        }

        sharedViewModel.currentProgramType.observe(viewLifecycleOwner, Observer {currentType ->
            binding.programType = currentType
            val resourceId = resources.getIdentifier(
                currentType.icon,
                "drawable",
                requireContext().packageName)
            binding.programTypeImage.setImageResource(resourceId)


            when (currentType?.back_color) {
                INDOORCOLOR -> binding.apply {
                    programExerciseRecycler.adapter = programExercisesAdapter
                    otherExerciseRecycler.adapter = otherExercisesAdapter
                    binding.buttonAddExercises.setOnClickListener {
                        findNavController().navigate(R.id.action_programDetailsFragment_to_newExerciseFragment)
                    }

                    programExercisesAdapter.notifyDataSetChanged()
                    otherExercisesAdapter.notifyDataSetChanged()
                }

                OUTDOORCOLOR -> binding.apply{
                    tvProgramExercises.visibility = View.GONE
                    programExerciseRecycler.visibility = View.GONE
                    otherExerciseRecycler.visibility = View.GONE
                    tvChooseExercises.visibility = View.GONE
                    buttonAddExercises.visibility = View.GONE
                }
            }
        })



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
                binding.noExerciseMessage.isGone = convertedProgramExercises.isEmpty() && otherExercises.isEmpty()
            }
        }
        // Fetch the user program exercises for the current program
        sharedViewModel.flowExercisesForCurrentProgram()
    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
