package com.example.exercisetracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.adapters.ProgramTypeAdapter
import com.example.exercisetracker.databinding.FragmentProgramTypeBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

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
         // Midlertidig navigering. Skal videre til newProgramFragment!!
        val adapter = ProgramTypeAdapter {programType ->
            sharedViewModel.onProgramTypeSelected(programType)
            findNavController().navigate(R.id.action_programTypeFragment_to_SecondFragment)}

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_programTypeFragment_to_SecondFragment)
        }
        //TEMPORARY NAVIGATION TO NEW PROGRAM FRAGMENT
        binding.buttonTempNext.setOnClickListener {
            findNavController().navigate(R.id.action_programTypeFragment_to_newProgramFragment)
        }

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            programTypeFragment = this@ProgramTypeFragment
            // listRecycler er recyclerView i fragment_program_type.xml
            listRecycler.adapter = adapter
        }

            //adapter.submitList(sharedViewModel.programTypes)
            adapter.notifyDataSetChanged()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}