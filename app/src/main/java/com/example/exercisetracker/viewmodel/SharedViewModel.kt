package com.example.exercisetracker.viewmodel
import android.util.Log
import androidx.lifecycle.*
import com.example.exercisetracker.db.User
import com.example.exercisetracker.repository.TrainingRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.exercisetracker.db.ActiveUser
import com.example.exercisetracker.db.ProgramType
import com.example.exercisetracker.network.AppProgramTypeJSON
import com.example.exercisetracker.network.UserJSON


class SharedViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _activeUserId = MutableLiveData<Int>()
    val activeUserId: LiveData<Int> = _activeUserId

    private val _users = MutableLiveData<List<UserJSON>>()
    val users: LiveData<List<UserJSON>> = _users

    private val _programTypes = MutableLiveData<List<AppProgramTypeJSON>>()
    val programTypes: LiveData<List<AppProgramTypeJSON>> = _programTypes




    init {
        restart()
    }

    fun createUser(user: User)  {
        viewModelScope.launch {

            val result = repository.createUser(user)
            if (result.isSuccess) {
                val newUser = result.getOrNull()
                if (newUser != null) {
                    setActiveUser(newUser.id, newUser.phone)
                }
            }
            else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e("TAG", "Error creating user: $errorMessage")
            }
        }
    }



    fun login(phone: String): Boolean {
        for (user in users.value!!) {
            if (user.phone == phone) {
                setActiveUser(user.id, user.phone)
                return true
            }
        }
        return false
    }


    fun setActiveUser(id: Int, phone: String) {
        val activeUser = ActiveUser(id, phone)
        _activeUserId.value = id
        viewModelScope.launch {
            repository.removeActiveUser()
            repository.addActiveUser(activeUser)
            Log.d("Active userid", id.toString())
        }
    }

    fun logout() {
        _activeUserId.value = 0
        viewModelScope.launch {
            repository.removeActiveUser()
        }
    }

    fun restart() {
        _activeUserId.value = 0
        viewModelScope.launch {
            _users.value = repository.getUsers()
            val resultActiveUser = repository.getActiveUser()
            if (resultActiveUser.isSuccess) {
                resultActiveUser.getOrNull()?.let { login(it.phone) }
            }
        }
    }

    fun setProgramType(programType: ProgramType) {
        viewModelScope.launch {
            TODO()
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