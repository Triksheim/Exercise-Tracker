package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.adapters.ExerciseItemAdapter
import com.example.exercisetracker.databinding.FragmentProgramDetailsBinding


class ProgramDetailsFragment: Fragment() {


    private var _binding: FragmentProgramDetailsBinding? = null
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
        val adapter = ExerciseItemAdapter

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_programDetailsFragment_to_newProgramFragment)
        }

        binding.buttonAddExercises.setOnClickListener{
            findNavController().navigate(R.id.action_programDetailsFragment_to_newExerciseFragment)
        }

    }
}