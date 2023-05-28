package com.example.exercisetracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.adapters.ProgramTypeAdapter
import com.example.exercisetracker.databinding.FragmentProgramTypeBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.flow.collectLatest

const val INDOORCOLOR = "#7fe5ab"
const val OUTDOORCOLOR = "#ab7fe5"
class ProgramTypeFragment: Fragment() {

    private var _binding: FragmentProgramTypeBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentProgramTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.setToolbarTitle(getString(R.string.title_program_type))

        val backColor = when (arguments?.getString("type") ?: "") {
            "Indoor" -> INDOORCOLOR
            "Outdoor" -> OUTDOORCOLOR
            else -> ""
        }
        val adapter = ProgramTypeAdapter { programType ->
            sharedViewModel.setCurrentProgramType(programType)
            val action = ProgramTypeFragmentDirections.actionProgramTypeFragmentToNewProgramFragment(programType.id)
            findNavController().navigate(action)
        }


        lifecycleScope.launchWhenStarted {
            sharedViewModel.programTypes.collectLatest { programTypes ->
                val filteredProgramTypes = programTypes.filter { it.back_color == backColor }
                adapter.submitList(filteredProgramTypes)
            }
        }

        binding?.apply {
            programRecycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}