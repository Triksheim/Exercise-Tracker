package com.example.exercisetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.databinding.FragmentNewProgramBinding
import com.example.exercisetracker.db.UserProgram
import com.example.exercisetracker.db.UserProgramEntity
import com.example.exercisetracker.network.UserProgramJSON
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

class NewProgramFragment: Fragment() {

    private val navigationArgs: NewProgramFragmentArgs by navArgs()
    private var _binding: FragmentNewProgramBinding? = null
    private val binding get() = _binding!!

    private lateinit var userProgram: UserProgram
    private var useTimer = 1

    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
          savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewProgramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activityLocation = navigationArgs.activityLocation

        val programTypeId = navigationArgs.programTypeId ?: -1
        if (programTypeId == -1) {
            Log.e("NewProgramFragment", "Failed to receive programTypeId")
        }

        binding.apply {
            timerOptions.setOnCheckedChangeListener{ group, _ ->
                useTimer = if (rbOptionYes.isChecked ) 1 else 0
            }

            buttonBack.setOnClickListener {
                findNavController().navigate(R.id.action_newProgramFragment_to_programTypeFragment)
            }
            buttonMyPrograms.setOnClickListener {
                findNavController().navigate(R.id.action_newProgramFragment_to_myProgramsFragment)
            }
            buttonSaveProgram.setOnClickListener {
                if (sharedViewModel.isUserLoggedIn()) {
                    addUserProgram()
                }else {}
            }
        }
    }

    private fun addUserProgram() { // Holder det å sjekke innlogging ved hamburgermenyvalg + FrontPage? evt hamburger viser ingenting når ikke innlogget
        if(isValidProgramEntry()) {
            userProgram = createUserProgram()
            sharedViewModel.addUserProgram(userProgram)
        }
        val action = NewProgramFragmentDirections.actionNewProgramFragmentToProgramDetailsFragment(userProgram.id)
        findNavController().navigate(action)
    }

    private fun isValidProgramEntry() = sharedViewModel.isValidProgramEntry(
        binding.programNameInput.text.toString(),
        binding.programDescriptInput.text.toString()
    )

    private fun createUserProgram(): UserProgram {
        return UserProgram(
        id = 0,
        user_id = sharedViewModel.activeUser.value!!.id,
        app_program_type_id = navigationArgs.programTypeId,
        name = binding.programNameInput.text.toString(),
        description = binding.programDescriptInput.text.toString(),
        use_timing = useTimer,
        icon = "@drawable/runner")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}