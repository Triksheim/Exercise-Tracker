package com.example.exercisetracker

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.databinding.FragmentNewExerciseBinding
import com.example.exercisetracker.db.UserExercise
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.launch

class NewExerciseFragment: Fragment() {

    private val navigationArgs: NewExerciseFragmentArgs by navArgs()

    private var _binding: FragmentNewExerciseBinding? = null
    private val binding get() = _binding!!

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize takePictureLauncher
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as Bitmap
                binding.imageviewExercise.setImageBitmap(bitmap)
            } else {
                Toast.makeText(requireContext(), "Feil ved visning av bilde", Toast.LENGTH_SHORT).show()
            }
        }
        // Initialize galleryLauncher
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                binding.imageviewExercise.setImageURI(result.data?.data)
            } else {
                Toast.makeText(requireContext(), "Feil ved visning av bilde", Toast.LENGTH_SHORT).show()
            }
        }


        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_newExerciseFragment_to_programDetailsFragment)
        }

        val buttonCamera = binding.buttonCamera
        buttonCamera.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(takePictureIntent)
        }

        val buttonGallery = binding.buttonGallery
        buttonGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }

        sharedViewModel.activeUser.observe(viewLifecycleOwner, Observer { activeUser ->
            val userId = activeUser.id
            val exerciseName = binding.exerciseNameInput.text.toString()
            val description = binding.exerciseDescriptInput.text.toString()


            // Make sure userId is valid before using it
            if (userId != -1) {
                // In NewExerciseFragment
                binding.buttonSaveExercise.setOnClickListener {

                    val newUserExercise = UserExercise(
                        id = 0, // This will be auto-generated by the database
                        user_id = userId,
                        name = exerciseName,
                        photo_url = "https://wfa-media.com/exercise23/img/exercise1.png", // Set a default value or get it from the UI
                        description = description,
                        icon = "ic_launcher_foreground", // Set a default value or get it from the UI
                        infobox_color = "#FE0980" // Set a default value or get it from the UI
                    )

                    // Save the exercise to the database
                    viewLifecycleOwner.lifecycleScope.launch {
                        sharedViewModel.addExercise(newUserExercise)
                    }

                    // Navigate to the ProgramDetailsFragment
                    val action = NewExerciseFragmentDirections
                        .actionNewExerciseFragmentToProgramDetailsFragment(userId)
                    findNavController().navigate(action)
                }

            }
        })

        if (navigationArgs.userExerciseId == null) { println("dette må fixes") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
