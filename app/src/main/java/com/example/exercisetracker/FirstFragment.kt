package com.example.exercisetracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.databinding.FragmentFirstBinding
import com.example.exercisetracker.db.UserEntity
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    val mainScope = MainScope()
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

        sharedViewModel.createUserStatus.observe(viewLifecycleOwner) { result ->
            if (result.isFailure) {
                if (!sharedViewModel.networkConnectionOk.value!!) {
                    Toast.makeText(
                        context,
                        "Ingen kontakt med server.\nSjekk internett forbindelsen.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(context, "Mobilnummer allerede i bruk", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

        sharedViewModel.activeUser.observe(viewLifecycleOwner, Observer {
            if (it.id != 0) {
                Toast.makeText(context, "Velkommen, ${it.name}", Toast.LENGTH_SHORT).show()
                navigateToNextFragment()
            }
        })


        binding?.buttonRegister?.setOnClickListener {
            val name =
                view.findViewById<TextInputEditText>(R.id.first_name_input).text.toString()
            var birthYear = 0
            val stringBirthYear =
                view.findViewById<TextInputEditText>(R.id.birth_year_input).text.toString()
            if (stringBirthYear != "") {
                birthYear = stringBirthYear.toInt()
            }
            val email =
                view.findViewById<TextInputEditText>(R.id.email_name_input).text.toString()
            val phone =
                view.findViewById<TextInputEditText>(R.id.phone_number_input).text.toString()

            if (name == "" || birthYear == 0 || email == "" || phone == "") {
                Toast.makeText(context, "Vennligst utfyll alle felter", Toast.LENGTH_SHORT).show()
            } else {
                val userEntity = UserEntity(0, phone, email, name, birthYear)
                sharedViewModel.createUser(userEntity)
            }
        }




        binding?.buttonLogin?.setOnClickListener {
            val phoneLoginInput: TextInputEditText = view.findViewById(R.id.phone_login_input)
            val phoneLogin = phoneLoginInput.text.toString()

            lifecycleScope.launch {
                val loginSuccess = sharedViewModel.login(phoneLogin)
                if (loginSuccess) {
                    //Toast.makeText(context, "Godkjent", Toast.LENGTH_SHORT).show()
                } else {
                    if (!sharedViewModel.networkConnectionOk.value!!) {
                        Toast.makeText(
                            context,
                            "Ingen kontakt med server.\nSjekk internett forbindelsen.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(context, "Ukjent mobilnummer", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    private fun navigateToNextFragment() {
        findNavController().navigate(R.id.action_FirstFragment_to_myStatisticsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}