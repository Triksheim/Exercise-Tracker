package com.example.exercisetracker

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.databinding.FragmentNewProgramBinding
import com.example.exercisetracker.db.AppProgramType
import com.example.exercisetracker.db.UserProgram
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

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
        sharedViewModel.setToolbarTitle(getString(R.string.title_new_program))

        // Set programType for the new program
        val programTypeId = navigationArgs.programTypeId
        if (programTypeId == -1) {
            Log.e("NewProgramFragment", "Failed to receive programTypeId")
        }


        // ProgramID is set from navargs only if user navigates via edit-button on a userprogram
        // Bind current program to edit it
        val programId = navigationArgs.programId
        if (programId > 0) {
            sharedViewModel.setToolbarTitle(getString(R.string.update_program))
            sharedViewModel.currentProgram.observe(this.viewLifecycleOwner) {selectedProgram ->
                userProgram = selectedProgram
                val resourceId = resources.getIdentifier(
                    userProgram.icon,
                    "drawable", requireContext().packageName
                )
                binding.programTypeImage.setImageResource(resourceId)
                binding.tvCreateNewProgram.visibility = View.INVISIBLE
                bindUserProgram(userProgram)
            }
        } else {
            // Else,user has navigated via create new program, and current programtype is set.
            sharedViewModel.currentProgramType.observe(this.viewLifecycleOwner){ programType ->
                binding.apply {
                    // Bind variable programtype in layout to chosen programType
                    programtype = programType
                    timerOptions.setOnCheckedChangeListener{ group, _ ->
                        useTimer = if (rbOptionYes.isChecked ) 1 else 0
                    }
                    binding.buttonDelete.visibility = View.GONE
                    buttonSaveProgram.setOnClickListener {
                        if (sharedViewModel.isUserLoggedIn()) {
                            addUserProgram(programType!!)
                        }else {
                            showNotLoggedInMessage()
                        }
                    }
                }
            }
        }
    }

    private fun bindUserProgram(userProgram: UserProgram) {
        // Creating dialog-interfaces to ask user for confirmation when the delete-button is clicked
        // on a program.
        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            sharedViewModel.deleteProgram(userProgram)
            findNavController().navigate(R.id.action_newProgramFragment_to_myProgramsFragment)
        }
        val negativeButtonClick =  { dialog: DialogInterface, which: Int ->
            Toast.makeText(requireContext(), getString(R.string.canceled), Toast.LENGTH_SHORT).show()
        }

        binding.apply{
            programNameInput.setText(userProgram.name, TextView.BufferType.SPANNABLE)
            programDescriptInput.setText(userProgram.description, TextView.BufferType.SPANNABLE)
            rbOptionYes.isChecked = userProgram.use_timing == 1
            rbOptionNo.isChecked = userProgram.use_timing == 0
            buttonSaveProgram.visibility = View.VISIBLE
            buttonSaveProgram.setText(getString(R.string.button_update_program))
            buttonSaveProgram.setOnClickListener{
                updateUserProgram()
                navigateToProgramDetails()
            }
            buttonDelete.visibility = View.VISIBLE
            buttonDelete.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.delete_program))
                    .setMessage(getString(R.string.confirmation_delete_program))
                    .setPositiveButton(getString(R.string.delete_program), positiveButtonClick)
                    .setNegativeButton(getString(R.string.cancel), negativeButtonClick)
                    .show()
            }
        }
    }

    private fun updateUserProgram(){

        if (isValidProgramEntry()) {
            val updatedProgram = UserProgram(
                userProgram.id,
                userProgram.user_id,
                userProgram.app_program_type_id,
                binding.programNameInput.text.toString(),
                binding.programDescriptInput.text.toString(),
                useTimer,
                userProgram.icon)

            sharedViewModel.updateUserProgram(updatedProgram)
            sharedViewModel.setCurrentUserProgram(updatedProgram)
            sharedViewModel.setProgramTypeByUserProgram(updatedProgram)
        }
    }

    private fun addUserProgram(programType: AppProgramType) {
        if(isValidProgramEntry()) {
            userProgram = createUserProgram(programType)

            // Save user program via viewModel
            viewLifecycleOwner.lifecycleScope.launch {
                sharedViewModel.createUserProgram(userProgram)
            }
            // Set currentUserProgram
            sharedViewModel.setCurrentUserProgram(userProgram)
        }
        // Navigate to ProgramDetails for current program
        navigateToProgramDetails()
    }

    private fun isValidProgramEntry() = sharedViewModel.isValidProgramEntry(
        binding.programNameInput.text.toString(),
        binding.programDescriptInput.text.toString()
    )

    private fun createUserProgram(programType: AppProgramType): UserProgram {
        useTimer = if (binding.rbOptionYes.isChecked) {
            1 } else { 0 }
        return UserProgram(
        id = 0,
        user_id = sharedViewModel.activeUser.value!!.id,
        app_program_type_id = navigationArgs.programTypeId,
        name = binding.programNameInput.text.toString(),
        description = binding.programDescriptInput.text.toString(),
        use_timing = useTimer,
        icon = programType.icon)
    }

    private fun navigateToProgramDetails(){
        val action = NewProgramFragmentDirections.actionNewProgramFragmentToProgramDetailsFragment(userProgram.id)
        findNavController().navigate(action)
    }

    private fun showNotLoggedInMessage(){
        val text ="Logg inn, eller registrer deg for å komme i gang med treningsprogram!"
        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}