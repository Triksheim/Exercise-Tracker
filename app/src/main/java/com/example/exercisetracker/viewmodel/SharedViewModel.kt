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
import com.example.exercisetracker.utils.Type


class SharedViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _activeUser = MutableLiveData<ActiveUser>()
    val activeUser: LiveData<ActiveUser> = _activeUser

    private val _users = MutableLiveData<List<UserJSON>>()
    val users: LiveData<List<UserJSON>> = _users

    private val _createUserStatus = MutableLiveData<Result<UserJSON>>()
    val createUserStatus: LiveData<Result<UserJSON>> = _createUserStatus

    private val _type = MutableLiveData<String>()
    val type: LiveData<String> = _type

    private val _programTypes = MutableLiveData<List<AppProgramTypeJSON>>()
    val programTypes: LiveData<List<AppProgramTypeJSON>> = _programTypes

    private val _backgroundColor = MutableLiveData<Int>()
    val backgroundColor: LiveData<Int> = _backgroundColor

    private val _programTypeId = MutableLiveData<Int>()
    val programTypeId: LiveData<Int> = _programTypeId




    init {
        restart()
    }

    fun createUser(user: User)   {
        viewModelScope.launch {

            val result = repository.createUser(user)
            if (result.isSuccess) {
                val newUser = result.getOrNull()
                if (newUser != null) {
                    setActiveUser(newUser)
                }
            }
            else {
                _createUserStatus.value = result
                Log.d("User creation", "Error creating user")
            }
        }
    }



    fun login(phone: String): Boolean {
        for (user in users.value!!) {
            if (user.phone == phone) {
                setActiveUser(user)
                return true
            }
        }
        return false
    }


    fun setActiveUser(user: UserJSON) {
        val activeUser = ActiveUser(user.id, user.phone, user.email, user.name, user.birth_year)
        _activeUser.value = activeUser
        viewModelScope.launch {
            repository.removeActiveUser()
            repository.addActiveUser(activeUser)
            Log.d("Active userid", user.id.toString())
        }
    }

    fun logout() {
        _activeUser.value = ActiveUser(0,"0","0","0", 0)
        _createUserStatus.value = Result.success(UserJSON(0,"0","0","0",0))
        viewModelScope.launch {
            repository.removeActiveUser()
        }
    }

    fun restart() {
        viewModelScope.launch {
            _users.value = repository.getUsers()
            val resultActiveUser = repository.getActiveUser()
            if (resultActiveUser.isSuccess) {
                resultActiveUser.getOrNull()?.let { login(it.phone) }
            }
        }
    }

    fun setTypeAndColor(type: Type) {
            _type.value = type.name
            _backgroundColor.value = type.rgb
    }

    fun onProgramTypeSelected(programType: ProgramType) {
        TODO()
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