package com.example.exercisetracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.databinding.FragmentSecondBinding
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.utils.Type
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

const val OUTDOOR_TYPE_ID = -1
class SecondFragment : Fragment() {
    private var _binding: FragmentSecondBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.setToolbarTitle(getString(R.string.title_second_fragment))


        // Dersom indoor image er trykket, må innendørs program-typer vises i recyclerView
        binding.indoorImage.setOnClickListener {
            val bundle = Bundle().apply {
                putString("type", "Indoor")
            }
            findNavController().navigate(R.id.action_SecondFragment_to_programTypeFragment, bundle)
        }
        // Dersom outdoor image er trykket, må utendørs program-typer vises i recyclerView
        binding.outdoorImage.setOnClickListener {
            val bundle = Bundle().apply {
                putString("type", "Outdoor")
            }
            findNavController().navigate(R.id.action_SecondFragment_to_programTypeFragment, bundle)
        }



        // No network alert
        if (!sharedViewModel.networkConnectionOk.value!!) {
            Toast.makeText(context, "Ingen kontakt med server.\nSjekk internett forbindelsen.", Toast.LENGTH_LONG).show()
            Toast.makeText(context, "Innlogget på nødløsning.\nTreningsdata er ikke oppdatert.", Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}