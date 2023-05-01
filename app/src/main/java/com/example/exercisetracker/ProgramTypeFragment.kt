package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.adapters.ProgramTypeAdapter
import com.example.exercisetracker.databinding.FragmentProgramTypeBinding

class ProgramTypeFragment: Fragment() {

    private var _binding: FragmentProgramTypeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentProgramTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         // Midlertidig navigering. Skal videre til newProgramFragment!!
        val adapter = ProgramTypeAdapter {programType -> findNavController().navigate(R.id.action_programTypeFragment_to_SecondFragment)}

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_programTypeFragment_to_SecondFragment)
        }
        //TEMPORARY NAVIGATION TO NEW PROGRAM FRAGMENT
        binding.buttonTempNext.setOnClickListener {
            findNavController().navigate(R.id.action_programTypeFragment_to_newProgramFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}