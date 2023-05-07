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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext


class SharedViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _networkConnectionOk = MutableLiveData<Boolean>()
    val networkConnectionOk: LiveData<Boolean> = _networkConnectionOk

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
    val userExercises: StateFlow<List<UserExercise>> = _userExercises

    private val _allSessions = MutableStateFlow<List<UserProgramSession>>(emptyList())
    val allSessions: StateFlow<List<UserProgramSession>> = _allSessions


    init {
        _networkConnectionOk.value = true
        _activeUser.value = ActiveUser(0,"0","0","0", 0)
        viewModelScope.launch {
            restart()
        }
        fetchAppProgramTypes()
        fetchUsers()
        fetchUserPrograms()
        fetchUserExercises()
        fetchUserProgramSessions()
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
    private fun fetchUserProgramSessions() {
        viewModelScope.launch {
            repository.getUserProgramSessions()
                .flowOn(Dispatchers.IO)
                .collect { sessionsList ->
                    _allSessions.value = sessionsList
                }
        }
    }



    fun createUser(user: User)   {
        viewModelScope.launch {
            val result = repository.createUserAPI(user)
            if (result.isSuccess) {
                _networkConnectionOk.value = true
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


    suspend fun login(phone: String): Boolean {
        return viewModelScope.async {
        if (users.value.isEmpty()) {
            getAllUsers()
        }
        for (user in users.value) {
            if (user.phone == phone) {
                Log.d("LOGIN SUCCESS", "ID: ${user.id}")
                setActiveUser(user)
                restart()
                return@async true
            }
        }
            return@async false
        }.await()
    }


    private suspend fun setActiveUser(user: User) {
        withContext(Dispatchers.IO) {
            val activeUser = ActiveUser(user.id, user.phone, user.email, user.name, user.birth_year)
            _activeUser.postValue(activeUser)
            repository.removeActiveUser()
            repository.addActiveUser(activeUser)
            Log.d("ACTIVE USER SET", "ID: ${user.id}")
        }
    }

    fun logout() {
        viewModelScope.launch {
            _activeUser.value = ActiveUser(0,"0","0","0", 0)
            _createUserStatus.value = Result.success(UserJSON(0,"0","0","0",0))
            clearDb()
            restart()
        }
    }

    fun checkIsActiveUser(): Boolean {
        return activeUser.value?.id != 0
    }

    private suspend fun clearDb() {
        withContext(Dispatchers.IO) {
            repository.removeActiveUser()
            repository.deleteAllUsers()
            repository.deleteProgramTypes()
            repository.deleteUserExercises()
            repository.deleteUserPrograms()
            repository.deleteUserProgramSessions()
        }
    }

    private suspend fun restart() {
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
                getAllUserExercises()
                if (userPrograms.value.isNotEmpty()) {
                    for (userProgram in userPrograms.value) {
                        getAllSessionsForUserProgram(userProgram.id)
                    }
                }
            }
        }
    }


    private suspend fun getAllUsers() {
        withContext(Dispatchers.IO) {
            val resultUsers = repository.getUsersAPI()
            if (resultUsers.isSuccess) {
                _networkConnectionOk.postValue(true)
                Log.d("RESULT USERS API", "SUCCESS")
                repository.deleteAllUsers()
                val users = resultUsers.getOrNull()
                for (user in users!!) {
                    repository.insertUser(user.asEntity())
                }
            } else {
                _networkConnectionOk.postValue(false)
                Log.e("ERROR USERS API", "Unable to fetch")
            }
        }
    }

    private suspend fun getAllProgramTypes() {
        withContext(Dispatchers.IO) {
            val result = repository.getAppProgramTypesAPI()
            if (result.isSuccess) {
                Log.d("RESULT PROGRAM TYPES API", "SUCCESS")
                repository.deleteProgramTypes()
                val appProgramTypes = result.getOrNull()
                for (programType in appProgramTypes!!) {
                    repository.insertProgramType(programType.asEntity())
                }
            } else {
                Log.e("ERROR PROGRAM TYPES API", "Unable to fetch")
            }
        }
    }

    private suspend fun getAllUserPrograms() {
        withContext(Dispatchers.IO) {
            val result = repository.getUserProgramsAPI(activeUser.value!!.id)
            if (result.isSuccess) {
                Log.d("RESULT USER PROGRAMS API", "SUCCESS")
                repository.deleteUserPrograms()
                repository.deleteUserProgramSessions()
                val userPrograms = result.getOrNull()
                for (userProgram in userPrograms!!) {
                    repository.insertUserProgram(userProgram.asEntity())
                }
            } else {
                Log.e(
                    "ERROR USER PROGRAM API",
                    "Unable to fetch (or no programs for ID:${activeUser.value!!.id})"
                )
            }
        }
    }

    private suspend fun getAllUserExercises() {
        withContext(Dispatchers.IO) {
            val result = repository.getUserExercisesAPI(activeUser.value!!.id)
            if (result.isSuccess) {
                Log.d("RESULT USER EXERCISES API", "SUCCESS")
                repository.deleteUserExercises()
                val userExercises = result.getOrNull()
                for (userExercise in userExercises!!) {
                    repository.insertUserExercise(userExercise.asEntity())
                }
            } else {
                Log.e(
                    "ERROR USER EXERCISES API",
                    "Unable to fetch (or no exercises for UserID:${activeUser.value!!.id})"
                )
            }
        }
    }

    private suspend fun getAllSessionsForUserProgram(userProgramId: Int) {
        withContext(Dispatchers.IO) {
            val result = repository.getUserProgramSessionsAPI(userProgramId)
            if (result.isSuccess) {
                Log.d("RESULT USER PROGRAM SESSION", "SUCCESS ID:${userProgramId}")
                val sessions = result.getOrNull()
                for (session in sessions!!) {
                    repository.insertUserProgramSession(session.asEntity())
                }
            } else {
                Log.e(
                    "ERROR USER PROGRAM SESSION API",
                    "Unable to fetch (or no session data for ProgramID:${userProgramId})"
                )
            }
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