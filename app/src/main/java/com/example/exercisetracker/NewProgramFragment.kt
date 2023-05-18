package com.example.exercisetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        val programTypeId = navigationArgs.programTypeId
        if (programTypeId == -1) {
            Log.e("NewProgramFragment", "Failed to receive programTypeId")
        }

        // ProgramID is set from navargs if user navigates via edit-button on a userprogram
        val programId = navigationArgs.programId
        if (programId > 0) {
            sharedViewModel.currentProgram.observe(this.viewLifecycleOwner) {selectedProgram ->
                userProgram = selectedProgram
                bindUserProgram(userProgram)
            }
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

    private fun bindUserProgram(userProgram: UserProgram) {
        binding.apply{
            programNameInput.setText(userProgram.name, TextView.BufferType.SPANNABLE)
            programDescriptInput.setText(userProgram.description, TextView.BufferType.SPANNABLE)
            rbOptionYes.isChecked = userProgram.use_timing == 1
            rbOptionNo.isChecked = userProgram.use_timing == 0
            buttonSaveProgram.setOnClickListener{
                updateUserProgram()
            }
        }
    }

    private fun updateUserProgram(){
        if (isValidProgramEntry()) {
            sharedViewModel.updateUserProgram(
                UserProgram(
                    userProgram.id,
                    userProgram.user_id,
                    userProgram.app_program_type_id,
                    binding.programNameInput.text.toString(),
                    binding.programDescriptInput.text.toString(),
                    useTimer,
                    "@drawable/runner")
            )
        }
    }

    private fun addUserProgram() { // Holder det å sjekke innlogging ved hamburgermenyvalg + FrontPage? evt hamburger viser ingenting når ikke innlogget
        if(isValidProgramEntry()) {
            userProgram = createUserProgram()
            sharedViewModel.addUserProgram(userProgram)
            sharedViewModel.setCurrentUserProgram(userProgram)
        }
        val action = NewProgramFragmentDirections.actionNewProgramFragmentToProgramDetailsFragment(userProgram.id)
        findNavController().navigate(action)
    }

    private fun isValidProgramEntry() = sharedViewModel.isValidProgramEntry(
        binding.programNameInput.text.toString(),
        binding.programDescriptInput.text.toString()
    )

    private fun createUserProgram(): UserProgram {
        useTimer = if (binding.rbOptionYes.isChecked) {
            1 } else { 0 }
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