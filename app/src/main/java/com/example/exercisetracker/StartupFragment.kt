package com.example.exercisetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.databinding.FragmentStartupBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

class StartupFragment : Fragment() {
    private var binding: FragmentStartupBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        val fragmentBinding = FragmentStartupBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            startupFragment = this@StartupFragment
        }

        sharedViewModel.startupDone.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (sharedViewModel.activeUser.value != null ) {
                findNavController().navigate(R.id.action_startupFragment_to_myStatisticsFragment)
            }
                else {
                    findNavController().navigate(R.id.action_startupFragment_to_LoginFragment)
                }
            }
        })


    }





    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}