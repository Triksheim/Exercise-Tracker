package com.example.exercisetracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.databinding.FragmentFirstBinding
import com.example.exercisetracker.db.User
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import org.w3c.dom.Text

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private var binding: FragmentFirstBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (activity?.application as TrainingApplication).trainingRepository
        )
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentFirstBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            firstFragment = this@FirstFragment

        }


        binding?.buttonRegister?.setOnClickListener {
            val nameInput: TextInputEditText = view.findViewById(R.id.first_name_input)
            val emailInput: TextInputEditText = view.findViewById(R.id.email_name_input)
            val phoneInput: TextInputEditText = view.findViewById(R.id.phone_number_input)
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val phone = phoneInput.text.toString()
            val birthYear = 2000
            val user = User(0, phone, email, name, birthYear)
            sharedViewModel.createUser(user)


        }

        sharedViewModel.activeUserId.observe(viewLifecycleOwner, Observer {
            Log.d("Active user", it.toString())
            if (it != 0) {
                navigateToNextFragment()
            }
        })



        binding?.buttonLogin?.setOnClickListener {
            val phoneLoginInput: TextInputEditText = view.findViewById(R.id.phone_login_input)
            val phoneLogin = phoneLoginInput.text.toString()
            if (sharedViewModel.login(phoneLogin)) {
                Toast.makeText(context, "Velkommen tilbake", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "Ukjent mobilnummer", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToNextFragment() {
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}