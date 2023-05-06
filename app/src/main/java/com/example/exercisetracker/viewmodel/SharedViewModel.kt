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
import kotlinx.coroutines.flow.*


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
        restart()
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



    fun login(phone: String): Boolean {
        if (users.value.isEmpty()) {
            restart()
        }


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
                if (userPrograms.value.isNotEmpty()) {
                    for (userProgram in userPrograms.value) {
                        getAllSessionsForUserProgram(userProgram.id)
                    }
                }
            }
        }
    }



    suspend fun getAllUsers() {
        val resultUsers = repository.getUsersAPI()
        if (resultUsers.isSuccess) {
            _networkConnectionOk.value = true
            Log.d("RESULT USERS API", "SUCCESS" )
            repository.deleteAllUsers()
            val users = resultUsers.getOrNull()
            for (user in users!!) {
                repository.insertUser(user.asEntity())
            }
        }
        else {
            _networkConnectionOk.value = false
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
            repository.deleteUserProgramSessions()
            val userPrograms = result.getOrNull()
            for(userProgram in userPrograms!!) {
                repository.insertUserProgram(userProgram.asEntity())
            }
        }
        else {
            Log.e("ERROR USER PROGRAM API", "Unable to fetch (or no data for ID:${activeUser.value!!.id})" )
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

            Log.e("ERROR USER EXERCISES API", "Unable to fetch (or no data for ID:${activeUser.value!!.id})" )
        }

    }

    suspend fun getAllSessionsForUserProgram(userProgramId: Int) {
        val result = repository.getUserProgramSessionsAPI(userProgramId)
        if (result.isSuccess) {
            Log.d("RESULT USER PROGRAM SESSION", "SUCCESS ID:${userProgramId}" )
            val sessions = result.getOrNull()
            for (session in sessions!!) {
                repository.insertUserProgramSession(session.asEntity())
            }
        }
        else {
            Log.e("ERROR USER PROGRAM SESSION API", "Unable to fetch (or no data for ProgramID:${userProgramId})" )
        }
    }
    fun getFilteredProgramTypes(){
        // todo
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