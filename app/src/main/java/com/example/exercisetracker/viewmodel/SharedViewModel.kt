package com.example.exercisetracker.viewmodel
import android.util.Log
import androidx.lifecycle.*
import com.example.exercisetracker.db.User
import com.example.exercisetracker.db.UserJSON
import com.example.exercisetracker.repository.TrainingRepository
import kotlinx.coroutines.launch


class SharedViewModel(private val repository: TrainingRepository) : ViewModel() {

    var current_id = 1

    fun dbTest() {
        val user = User(id = current_id, phone = "47380174", email = "tri032@uit.no", name = "Martin", birth_year = 1993)
        insertUserToDb(user)
        current_id += 1
    }

    fun apiTest() {
        val id = 86
        viewModelScope.launch {
            val user = repository.getUser(id)
            Log.d("User", user.name)
        }
    }

    fun apiTestCreate() {
        val user = User(id = 0, phone = "12345678", email = "test@uit.no", name = "Testersen", birth_year = 2000)
        viewModelScope.launch {

            val result = repository.createUser(user)
            if (result.isSuccess) {
                val newUser = result.getOrNull()
                Log.d("userid", newUser?.id.toString())
            }
            else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e("TAG", "Error creating user: $errorMessage")

            }
        }
    }

    fun createUser(user: User) {
        viewModelScope.launch {

            val result = repository.createUser(user)
            if (result.isSuccess) {
                val newUser = result.getOrNull()
                Log.d("New userid", newUser?.id.toString())
            }
            else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e("TAG", "Error creating user: $errorMessage")

            }
        }
    }



    private fun insertUserToDb(user: User) {
        viewModelScope.launch {
            repository.insertUserToDatabase(user)
        }
    }





}
class SharedViewModelFactory(private val trainingRepository: TrainingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(trainingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}