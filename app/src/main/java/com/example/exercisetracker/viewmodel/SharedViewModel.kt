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
import com.example.exercisetracker.network.UserJSON
import com.example.exercisetracker.repository.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn


class SharedViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _activeUser = MutableLiveData<ActiveUser>()
    val activeUser: LiveData<ActiveUser> = _activeUser

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _createUserStatus = MutableLiveData<Result<UserJSON>>()
    val createUserStatus: LiveData<Result<UserJSON>> = _createUserStatus

    private val _programTypes = MutableStateFlow<List<AppProgramType>>(emptyList())
    val programTypes: StateFlow<List<AppProgramType>> = _programTypes




    private val _type = MutableLiveData<String>()
    val type: LiveData<String> = _type


    private val _backgroundColor = MutableLiveData<Int>()
    val backgroundColor: LiveData<Int> = _backgroundColor

    private val _programTypeId = MutableLiveData<Int>()
    val programTypeId: LiveData<Int> = _programTypeId

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status



    init {
        restart()
        fetchAppProgramTypes()
        fetchUsers()
    }


    private fun fetchUsers() {
        viewModelScope.launch {
            repository.getAllUsers()
                .flowOn(Dispatchers.IO)
                .collect { usersList ->
                    _users.value = usersList
                }
        }
    }
    private fun fetchAppProgramTypes() {
        viewModelScope.launch {
            repository.getProgramTypes()
                .flowOn(Dispatchers.IO)
                .collect { appProgramTypesList ->
                    _programTypes.value = appProgramTypesList
                }
        }
    }


    fun createUser(user: User)   {
        viewModelScope.launch {

            val result = repository.createUserAPI(user)
            if (result.isSuccess) {
                val newUser = result.getOrNull()
                if (newUser != null) {
                    setActiveUser(User(newUser.id, newUser.phone, newUser.email, newUser.name, newUser.birth_year))
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


    fun setActiveUser(user: User) {
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
            repository.deleteAllUsers()
            restart()
        }
    }

    fun restart() {
        viewModelScope.launch {
            val resultUsers = repository.getUsersAPI()
            if (resultUsers.isSuccess) {
                Log.d("RESULT USERS API", "SUCCESS" )
                repository.deleteAllUsers()
                val users = resultUsers.getOrNull()
                for (user in users!!) {
                    repository.insertUser(User(user.id, user.phone, user.email,user.name, user.birth_year))
                }
            }
            else {
                Log.e("FETCH ERROR USERS API", "Unable to fetch Users" )
            }

            val resultActiveUser = repository.getActiveUser()
            if (resultActiveUser.isSuccess) {
                resultActiveUser.getOrNull()?.let { login(it.phone) }
            }

            getProgramTypesAPI()
        }
    }



    fun getProgramTypesAPI() {
        viewModelScope.launch {
            val result = repository.getAppProgramTypesAPI()
            if (result.isSuccess) {
                Log.d("RESULT PROGRAM TYPES API", "SUCCESS" )
                repository.deleteProgramTypes()
                val appProgramTypes = result.getOrNull()
                for (type in appProgramTypes!!) {
                    repository.insertProgramType(AppProgramType
                        (type.id, type.description, type.icon, type.back_color))
                }
            }
            else {
                Log.e("FETCH ERROR PROGRAM TYPES API", "Unable to fetch Program Types" )
            }
        }
    }

    fun onProgramTypeSelected(appProgramTypeJSON: com.example.exercisetracker.network.AppProgramTypeJSON) {
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