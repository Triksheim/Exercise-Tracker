package com.example.exercisetracker.viewmodel
import androidx.lifecycle.*
import com.example.exercisetracker.db.User
import com.example.exercisetracker.repository.TrainingRepository
import kotlinx.coroutines.launch


class SharedViewModel(private val repository: TrainingRepository) : ViewModel() {

    var current_id = 1

    fun test() {
        val user = User(id = current_id, phone = "47380174", email = "tri032@uit.no", name = "Martin", birth_year = 1993)
        insertUserToDb(user)
        current_id += 1
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