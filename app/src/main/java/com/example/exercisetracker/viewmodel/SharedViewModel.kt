package com.example.exercisetracker.viewmodel
import android.util.Log
import androidx.lifecycle.*
import com.example.exercisetracker.repository.TrainingRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.exercisetracker.db.*
import com.example.exercisetracker.network.UserJSON
import com.example.exercisetracker.utils.asEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn


class SharedViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _activeUser = MutableLiveData<ActiveUser>()
    val activeUser: LiveData<ActiveUser> = _activeUser

    private val _createUserStatus = MutableLiveData<Result<UserJSON>>()
    val createUserStatus: LiveData<Result<UserJSON>> = _createUserStatus

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _programTypes = MutableStateFlow<List<AppProgramType>>(emptyList())
    val programTypes: StateFlow<List<AppProgramType>> = _programTypes

    private val _userPrograms = MutableStateFlow<List<UserProgram>>(emptyList())
    val userPrograms: StateFlow<List<UserProgram>> = _userPrograms

    private val _userExercises = MutableStateFlow<List<UserExercise>>(emptyList())
    val userExercise: StateFlow<List<UserExercise>> = _userExercises



    init {
        _activeUser.value = ActiveUser(0,"0","0","0", 0)
        restart()
        fetchAppProgramTypes()
        fetchUsers()
        fetchUserPrograms()
        fetchUserExercises()
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

    private fun fetchUserPrograms() {
        viewModelScope.launch {
            repository.getUserPrograms()
                .flowOn(Dispatchers.IO)
                .collect { userProgramsList ->
                    _userPrograms.value = userProgramsList
                }
        }
    }

    private fun fetchUserExercises() {
        viewModelScope.launch {
            repository.getUserExercises()
                .flowOn(Dispatchers.IO)
                .collect { userExerciseList ->
                    _userExercises.value = userExerciseList
                }
        }
    }


    fun createUser(user: User)   {
        viewModelScope.launch {

            val result = repository.createUserAPI(user)
            if (result.isSuccess) {
                val newUser = result.getOrNull()
                setActiveUser(newUser!!.asEntity())
                restart()

            }
            else {
                _createUserStatus.value = result
                Log.e("ERROR USER CREATION", "Creating user failed")
            }
        }
    }



    fun login(phone: String): Boolean {
        for (user in users.value!!) {
            if (user.phone == phone) {
                viewModelScope.launch {
                    setActiveUser(user)
                    restart()
                }
                Log.d("LOGIN SUCCESS", "ID: ${user.id}")
                return true
            }
        }
        return false
    }


    suspend fun setActiveUser(user: User) {
        val activeUser = ActiveUser(user.id, user.phone, user.email, user.name, user.birth_year)
        _activeUser.value = activeUser
        repository.removeActiveUser()
        repository.addActiveUser(activeUser)
        Log.d("ACTIVE USER SET", "ID: ${user.id}")

    }

    fun logout() {
        viewModelScope.launch {
            _activeUser.value = ActiveUser(0,"0","0","0", 0)
            _createUserStatus.value = Result.success(UserJSON(0,"0","0","0",0))
            repository.removeActiveUser()
            repository.deleteAllUsers()
            restart()
        }
    }

    fun checkIsActiveUser(): Boolean {
        return activeUser.value?.id != 0
    }

    fun restart() {
        viewModelScope.launch {
            if (activeUser.value?.id == 0) {
                getAllUsers()
                val resultActiveUser = repository.getActiveUser()
                if (resultActiveUser.isSuccess) {
                    resultActiveUser.getOrNull()?.let { login(it.phone) }
                }
            }
            else if (checkIsActiveUser()) {
                getAllProgramTypes()
                getAllUserPrograms()
                getAllUserExcercises()
            }
        }
    }



    suspend fun getAllUsers() {
        val resultUsers = repository.getUsersAPI()
        if (resultUsers.isSuccess) {
            Log.d("RESULT USERS API", "SUCCESS" )
            repository.deleteAllUsers()
            val users = resultUsers.getOrNull()
            for (user in users!!) {
                repository.insertUser(user.asEntity())
            }
        }
        else {
            Log.e("ERROR USERS API", "Unable to fetch" )
        }

    }

    suspend fun getAllProgramTypes() {
            val result = repository.getAppProgramTypesAPI()
            if (result.isSuccess) {
                Log.d("RESULT PROGRAM TYPES API", "SUCCESS" )
                repository.deleteProgramTypes()
                val appProgramTypes = result.getOrNull()
                for (programType in appProgramTypes!!) {
                    repository.insertProgramType(programType.asEntity())
                }
            }
            else {
                Log.e("ERROR PROGRAM TYPES API", "Unable to fetch" )
            }
    }

    suspend fun getAllUserPrograms() {
        val result = repository.getUserProgramsAPI(activeUser.value!!.id)
        if (result.isSuccess) {
            Log.d("RESULT USER PROGRAMS API", "SUCCESS" )
            repository.deleteUserPrograms()
            val userPrograms = result.getOrNull()
            for(userProgram in userPrograms!!) {
                repository.insertUserProgram(userProgram.asEntity())
            }
        }
        else {
            Log.d("USER PROGRAMS CURRENTUSER", "${activeUser.value!!.id}" )
            Log.e("ERROR USER PROGRAM API", "Unable to fetch / no data" )
        }
    }


    suspend fun getAllUserExcercises() {
        val result = repository.getUserExercisesAPI(activeUser.value!!.id)
        if (result.isSuccess) {
            Log.d("RESULT USER EXERCISES API", "SUCCESS" )
            repository.deleteUserExercises()
            val userExercises = result.getOrNull()
            for (userExercise in userExercises!!) {
                repository.insertUserExercise(userExercise.asEntity())
            }
        }
        else {
            Log.d("USEREXERCISES CURRENTUSER", "${activeUser.value!!.id}")
            Log.e("ERROR USER EXERCISES API", "Unable to fetch / no data" )
        }
    }



    fun onProgramTypeSelected(appProgramType: com.example.exercisetracker.db.AppProgramType) {
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