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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.databinding.FragmentStartupBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        sharedViewModel.setToolbarTitle(getString(R.string.title_login))

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            startupFragment = this@StartupFragment
        }

        sharedViewModel.startupDone.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (sharedViewModel.activeUser.value != null ) {
                    lifecycleScope.launch {
                        delay(2500) // Small delay to get user data before navigating
                        findNavController().navigate(R.id.mySessionsFragment)
                    }
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