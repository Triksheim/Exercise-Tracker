package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.adapters.ExerciseItemAdapter
import com.example.exercisetracker.databinding.FragmentMyExercisesBinding
import com.example.exercisetracker.db.UserExercise
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.flow.collectLatest

const val EXERCISES_FRAGMENT = "exercisesFragment"

class MyExercisesFragment: Fragment() {

    private var _binding: FragmentMyExercisesBinding? = null
    private val binding get() = _binding!!

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
            override fun onEditButtonClick(userExercise: UserExercise) {
                sharedViewModel.setCurrentUserExercise(userExercise)
                val actionEditExercise = MyExercisesFragmentDirections.actionMyExercisesFragmentToNewExerciseFragment(userExercise.id)
                findNavController().navigate(actionEditExercise)
            }
            override fun onAddButtonClick(userExercise: UserExercise) {
                // Not visible in this fragment
            }

            override fun onRemoveButtonClick(userExercise: UserExercise) {
                // Not Visible in this fragment
            }

        }
        // From this fragment ExerciseItemAdapter is used, passing the recyclerLocation to set views
        val adapter = ExerciseItemAdapter(exerciseClickListener, EXERCISES_FRAGMENT)

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