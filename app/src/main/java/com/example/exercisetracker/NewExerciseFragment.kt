package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.databinding.FragmentNewExerciseBinding
import okhttp3.internal.ignoreIoExceptions

class NewExerciseFragment: Fragment() {

    private val navigationArgs: NewExerciseFragmentArgs by navArgs()

    private var _binding: FragmentNewExerciseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_newExerciseFragment_to_programDetailsFragment)
        }
        //MIDLERTIDIG NAVIGERING TILBAKE. SKAL NAVIGERE TIL "MINE ØVELSER" FRAGMENTET
        binding.buttonSaveExercise.setOnClickListener{
            findNavController().navigate(R.id.action_newExerciseFragment_to_programDetailsFragment)
        }

        if (navigationArgs.userExerciseId == null) { println("dette må fixes") }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
