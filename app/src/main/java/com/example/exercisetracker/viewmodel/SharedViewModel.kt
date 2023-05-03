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
import com.example.exercisetracker.db.AppProgramType
import com.example.exercisetracker.network.AppProgramTypesJSON
import com.example.exercisetracker.network.UserJSON
import com.example.exercisetracker.repository.ApiStatus
import com.example.exercisetracker.utils.Type


class SharedViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _activeUserId = MutableLiveData<Int>()
    val activeUserId: LiveData<Int> = _activeUserId

    private val _users = MutableLiveData<List<UserJSON>>()
    val users: LiveData<List<UserJSON>> = _users

    private val _createUserStatus = MutableLiveData<Result<UserJSON>>()
    val createUserStatus: LiveData<Result<UserJSON>> = _createUserStatus

    private val _type = MutableLiveData<String>()
    val type: LiveData<String> = _type

    private val _programTypes = MutableLiveData<List<AppProgramTypesJSON>>()
    val programTypes: LiveData<List<AppProgramTypesJSON>> = _programTypes

    private val _backgroundColor = MutableLiveData<Int>()
    val backgroundColor: LiveData<Int> = _backgroundColor

    private val _programTypeId = MutableLiveData<Int>()
    val programTypeId: LiveData<Int> = _programTypeId

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status



    init {
        restart()
    }

    fun createUser(user: User)   {
        viewModelScope.launch {

            val result = repository.createUser(user)
            if (result.isSuccess) {
                val newUser = result.getOrNull()
                if (newUser != null) {
                    setActiveUser(newUser.id, newUser.phone)
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
        _createUserStatus.value = Result.success(UserJSON(0,"1","1","1",1))
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

    fun setTypeAndColor(type: Type) {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                _programTypes.value = repository.getAllAppProgramTypes()
                _type.value = type.name
                _backgroundColor.value = type.rgb
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _type.value = ApiStatus.ERROR.toString()
            }
        }
    }

    fun onProgramTypeSelected(appProgramTypesJSON: com.example.exercisetracker.network.AppProgramTypesJSON) {
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