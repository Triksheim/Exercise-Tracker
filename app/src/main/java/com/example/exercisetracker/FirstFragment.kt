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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.exercisetracker.databinding.FragmentFirstBinding
import com.example.exercisetracker.db.User
import com.example.exercisetracker.repository.TrainingApplication
import com.example.exercisetracker.viewmodel.SharedViewModel
import com.example.exercisetracker.viewmodel.SharedViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import retrofit2.HttpException
import retrofit2.http.HTTP

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

        sharedViewModel.createUserStatus.observe(viewLifecycleOwner) { result ->
            if (result.isFailure) {
                Toast.makeText(context, "Mobilnummer allerede i bruk", Toast.LENGTH_SHORT).show()
            }
        }

        sharedViewModel.activeUserId.observe(viewLifecycleOwner, Observer {
            Log.d("Active user", it.toString())
            if (it != 0) {
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
            val user = User(0, phone, email, name, birthYear)

            if (name == "" || birthYear == 0 || email == "" || phone == "") {
                Toast.makeText(context, "Vennligst utfyll alle felter", Toast.LENGTH_SHORT).show()
            }
            else {
                val user = User(0, phone, email, name, birthYear)
                sharedViewModel.createUser(user)
            }
        }




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