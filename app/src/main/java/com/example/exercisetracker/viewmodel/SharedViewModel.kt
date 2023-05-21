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
import com.example.exercisetracker.utils.asDomainModel
import com.example.exercisetracker.utils.asEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext


class SharedViewModel(private val repository: TrainingRepository) : ViewModel() {
    val outsideColor = "789ABCD"
    val insideColor = "#123456"

    private val _networkConnectionOk = MutableLiveData<Boolean>()
    val networkConnectionOk: LiveData<Boolean> = _networkConnectionOk

    private val _activeUser = MutableLiveData<ActiveUserEntity>()
    val activeUser: LiveData<ActiveUserEntity> = _activeUser

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

    private var _currentUserExercise = MutableLiveData<UserExercise>()
    val currentUserExercise: LiveData<UserExercise> = _currentUserExercise

    private val _allSessions = MutableStateFlow<List<UserProgramSession>>(emptyList())
    val allSessions: StateFlow<List<UserProgramSession>> = _allSessions

    private var _currentProgram = MutableLiveData<UserProgram>()
    val currentProgram: LiveData<UserProgram> = _currentProgram

    private var _programExercises = MutableLiveData<List<UserExercise>>()
    val programExercises: LiveData<List<UserExercise>> = _programExercises

    private var _programSessions = MutableLiveData<List<UserProgramSession>>()
    val programSessions: LiveData<List<UserProgramSession>> = _programSessions

    private var _currentSession = MutableLiveData<UserProgramSession>()
    val currentSession: LiveData<UserProgramSession> = _currentSession

    private var _sessionData = MutableLiveData<List<UserProgramSessionData>>()
    val sessionData: LiveData<List<UserProgramSessionData>> = _sessionData

    private var _sessionPhotos = MutableLiveData<List<UserProgramSessionPhoto>>()
    val sessionPhotos: LiveData<List<UserProgramSessionPhoto>> = _sessionPhotos

    private val _userProgramExercises = MutableLiveData<List<UserProgramExercise>>()
    val userProgramExercises: LiveData<List<UserProgramExercise>> get() = _userProgramExercises


    init {
        _networkConnectionOk.value = true
        _activeUser.value = ActiveUserEntity(0,"0","0","0", 0)
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
                .map { usersList -> usersList.map { it.asDomainModel() } }
                .collect { usersList ->
                    _users.value = usersList
                }
        }
    }
    private fun fetchAppProgramTypes() {
        viewModelScope.launch {
            repository.getProgramTypes()
                .flowOn(Dispatchers.IO)
                .map { appProgramTypesList -> appProgramTypesList.map { it.asDomainModel() } }
                .collect { appProgramTypesList ->
                    _programTypes.value = appProgramTypesList
                }
        }
    }
    private fun fetchUserPrograms() {
        viewModelScope.launch {
            repository.getUserPrograms()
                .flowOn(Dispatchers.IO)
                .map { userProgramsList -> userProgramsList.map { it.asDomainModel() } }
                .collect { userProgramsList ->
                    _userPrograms.value = userProgramsList
                }
        }
    }
    private fun fetchUserExercises() {
        viewModelScope.launch {
            repository.getUserExercises()
                .flowOn(Dispatchers.IO)
                .map { userExerciseList -> userExerciseList.map { it.asDomainModel() } }
                .collect { userExerciseList ->
                    _userExercises.value = userExerciseList
                }
        }
    }
    private fun fetchUserProgramSessions() {
        viewModelScope.launch {
            repository.getUserProgramSessions()
                .flowOn(Dispatchers.IO)
                .map { sessionsList -> sessionsList.map { it.asDomainModel() } }
                .collect { sessionsList ->
                    _allSessions.value = sessionsList
                }
        }
    }

    fun fetchExercisesForCurrentProgram() {
        viewModelScope.launch {
            repository.getUserProgramExercisesById(currentProgram.value!!.id)
                .flowOn(Dispatchers.IO)
                .map { userProgramExercisesList -> userProgramExercisesList.map { it.asDomainModel() } }
                .collect { userProgramExercisesList ->
                    _userProgramExercises.value = userProgramExercisesList
                }
        }
    }

    fun getSessionsForCurrentProgram() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getSessionsForProgramId(currentProgram.value!!.id).map { it.asDomainModel() }
            _programSessions.postValue(result)
            }
        }

    fun getDataForCurrentSession() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getDataForSessionId(currentSession.value!!.id).map { it.asDomainModel() }
            _sessionData.postValue(result)
        }
    }

    fun getPhotosForCurrentSession() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getPhotosForSessionId(currentSession.value!!.id).map { it.asDomainModel() }
            _sessionPhotos.postValue(result)
        }
    }

    fun setCurrentProgram(userProgram: UserProgram) {
        _currentProgram.value = userProgram
    }

    fun setCurrentSession(userProgramSession: UserProgramSession) {
        _currentSession.value = userProgramSession
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
            val activeUserEntity = ActiveUserEntity(user.id, user.phone, user.email, user.name, user.birth_year)
            _activeUser.postValue(activeUserEntity)
            repository.removeActiveUser()
            repository.addActiveUser(activeUserEntity)
            Log.d("ACTIVE USER SET", "ID: ${user.id}")
        }
    }

    fun logout() {
        viewModelScope.launch {
            _activeUser.value = ActiveUserEntity(0,"0","0","0", 0)
            _createUserStatus.value = Result.success(UserJSON(0,"0","0","0",0))
            clearDb()
            restart()
        }
    }

    fun checkIsActiveUser(): Boolean {
        return activeUser.value?.id != 0
    }

    fun isValidProgramEntry(name: String, description: String): Boolean{
        return name.isNotBlank() && description.isNotBlank()
    }

    fun setCurrentUserProgram(userProgram: UserProgram){
        _currentProgram.value = userProgram
    }

    fun isUserLoggedIn(): Boolean {
        return (activeUser.value!!.id != 0)
    }

    fun setCurrentUserExercise(userExercise: UserExercise) {
        _currentUserExercise.value = userExercise
    }

    fun isValidExerciseEntry(name: String, description: String): Boolean{
        return name.isNotBlank() && description.isNotBlank()
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
                    getAllUserProgramSessionData()
                    for (userProgram in userPrograms.value) {
                        getAllUserExercisesForUserProgram(userProgram.id)
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
                repository.deleteUserProgramExercises()
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

    private suspend fun getAllUserExercisesForUserProgram(userProgramId: Int) {
        withContext(Dispatchers.IO) {
            val result = repository.getUserProgramExercisesAPI(userProgramId)
            if (result.isSuccess) {
                Log.d("RESULT USER PROGRAM EXERCISES", "SUCCESS PROGRAM ID: $userProgramId")
                val userProgramExercises = result.getOrNull()
                for (userProgramExercise in userProgramExercises!!) {
                    repository.insertUserProgramExercise(userProgramExercise.asEntity())
                }
            } else {
                Log.e(
                    "ERROR USER PROGRAM EXERCISES",
                    "Unable to fetch (or no exercises for UserProgramID:$userProgramId)"
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


    private suspend fun getAllUserProgramSessionData() {
        withContext(Dispatchers.IO) {
            val result = repository.getALlUserProgramSessionDataAPI(activeUser.value!!.id)
            if (result.isSuccess) {
                Log.d("RESULT USER PROGRAM SESSIONS DATA", "SUCCESS")
                val sessionsData = result.getOrNull()
                for (sessionData in sessionsData!!) {
                    repository.insertUserProgramSessionData(sessionData.asEntity())
                }
            } else {
                Log.e(
                    "ERROR USER PROGRAM SESSION DATA API",
                    "Unable to fetch (or no session data for UserID:${activeUser.value?.id})"
                )
            }
        }
    }

    // User
    fun createUser(user: User)   {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.createUserAPI(user)
            if (result.isSuccess) {
                _networkConnectionOk.postValue(true)
                val newUser = result.getOrNull()
                setActiveUser(newUser!!.asEntity().asDomainModel())
                restart()
            }
            else {
                _createUserStatus.postValue(result)
                Log.e("ERROR USER CREATION", "Creating user failed")
            }
        }
    }


    // UserProgram
    suspend fun createUserProgram(userProgram: UserProgram) {
        viewModelScope.launch(Dispatchers.IO){
            val result = repository.createUserProgramAPI(userProgram = userProgram)
            if (result.isSuccess) {
                val newUserProgram = result.getOrNull()
                newUserProgram?.let { repository.insertUserProgram(it.asEntity())}
                Log.d("CREATE USER PROGRAM", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM", "Creating user program failed")
            }
    }}

    fun updateUserProgram(userProgram: UserProgram){
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateUserProgramAPI(userProgram = userProgram)
            if (result.isSuccess) {
                val updatedUserProgram = result.getOrNull()
                updatedUserProgram?.let {repository.updateUserProgram(it.asEntity())}
                Log.d("UPDATE USER PROGRAM", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM", "Updating user program failed")
            }
        }
    }

    suspend fun deleteUserProgram(userProgram: UserProgram) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteUserExerciseAPI(userProgram.id)
            if (result.isSuccess) {
                repository.deleteUserProgram(userProgram.asEntity())
                Log.d("DELETE USER PROGRAM", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM", "Deleting user program failed")
            }
        }
    }


    suspend fun createUserExercise(userExercise: UserExercise) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.createUserExerciseAPI(userExercise)
            if (result.isSuccess) {
                val newUserExercise = result.getOrNull()
                newUserExercise?.let { repository.insertUserExercise(it.asEntity()) }
                Log.d("CREATE USER EXERCISE", "SUCCESS")
            }
            else {
                Log.e("ERROR USER EXERCISE", "Creating user exercise failed")
            }
        }
    }

    suspend fun updateUserExercise(userExercise: UserExercise) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateUserExerciseAPI(userExercise)
            if (result.isSuccess) {
                val updatedUserExercise = result.getOrNull()
                updatedUserExercise?.let { repository.updateUserExercise(it.asEntity()) }
                Log.d("UPDATE USER EXERCISE", "SUCCESS")
            }
            else {
                Log.e("ERROR USER EXERCISE", "Updating user exercise failed")
            }
        }
    }

    suspend fun deleteUserExercise(userExercise: UserExercise) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteUserExerciseAPI(userExercise.id)
            if (result.isSuccess) {
                repository.deleteUserExercise(userExercise.asEntity())
                Log.d("DELETE USER EXERCISE", "SUCCESS")
            }
            else {
                Log.e("ERROR USER EXERCISE", "Deleting user exercise failed")
            }
        }
    }


    // UserProgramExercise
    fun addUserExerciseToUserProgram(userProgramExercise: UserProgramExercise) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.createUserProgramExerciseAPI(userProgramExercise)
            if (result.isSuccess) {
                val newUserProgramExercise = result.getOrNull()
                newUserProgramExercise?.let { repository.insertUserProgramExercise(it.asEntity()) }
                Log.d("ADDED EXERCISE TO USER PROGRAM", "SUCCESS")
            } else {
                Log.e("ERROR USER PROGRAM EXERCISE", "Adding exercise failed")
            }
        }
    }

    fun deleteUserProgramExercise(userProgramExercise: UserProgramExercise) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteUserProgramExerciseAPI(userProgramExercise.id)
            if (result.isSuccess) {
                repository.deleteUserProgramExercise(userProgramExercise.asEntity())
                Log.d("DELETE USER PROGRAM EXERCISE", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM EXERCISE", "Deleting user program exercise failed")
            }
        }
    }


    // UserProgramSession
    suspend fun createUserProgramSession(userProgramSession: UserProgramSession) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.createUserProgramSessionAPI(userProgramSession)
            if (result.isSuccess) {
                val newUserProgramSession = result.getOrNull()
                newUserProgramSession?.let { repository.insertUserProgramSession(it.asEntity()) }
                Log.d("CREATE USER PROGRAM SESSION", "SUCCESS")

            }
            else {
                Log.e("ERROR USER PROGRAM SESSION", "Creating user program session failed")
            }
        }
    }
    suspend fun updateUserProgramSession(userProgramSession: UserProgramSession) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateUserProgramSessionAPI(userProgramSession)
            if (result.isSuccess) {
                val updatedUserProgramSession = result.getOrNull()
                updatedUserProgramSession?.let { repository.updateUserProgramSession(it.asEntity()) }
                Log.d("UPDATE USER PROGRAM SESSION", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM SESSION", "Updating user program session failed")
            }
        }
    }

    suspend fun deleteUserProgramSession(userProgramSession: UserProgramSession) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteUserProgramSessionAPI(userProgramSession.id)
            if (result.isSuccess) {
                repository.deleteUserProgramSession(userProgramSession.asEntity())
                Log.d("DELETE USER PROGRAM SESSION", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM SESSION", "Deleting user program session failed")
            }
        }
    }


    // UserProgramSessionData
    suspend fun createUserProgramSessionData(userProgramSessionData: UserProgramSessionData) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.createUserProgramSessionDataAPI(userProgramSessionData)
            if (result.isSuccess) {
                val newUserProgramSessionData = result.getOrNull()
                newUserProgramSessionData?.let { repository.insertUserProgramSessionData(it.asEntity()) }
                Log.d("CREATE USER PROGRAM SESSION DATA", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM SESSION DATA", "Creating user program session data failed")
            }
        }
    }

    suspend fun updateUserProgramSessionData(userProgramSessionData: UserProgramSessionData) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateUserProgramSessionDataAPI(userProgramSessionData)
            if (result.isSuccess) {
                val updatedUserProgramSessionData = result.getOrNull()
                updatedUserProgramSessionData?.let { repository.updateUserProgramSessionData(it.asEntity()) }
                Log.d("UPDATE USER PROGRAM SESSION DATA", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM SESSION DATA", "Updating user program session data failed")
            }
        }
    }

    suspend fun deleteUserProgramSessionData(userProgramSessionData: UserProgramSessionData) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteUserProgramSessionDataAPI(userProgramSessionData.id)
            if (result.isSuccess) {
                repository.deleteUserProgramSessionData(userProgramSessionData.asEntity())
                Log.d("DELETE USER PROGRAM SESSION DATA", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM SESSION DATA", "Deleting user program session data failed")
            }
        }
    }


    // UserProgramSessionPhoto
    suspend fun createUserProgramSessionPhoto(userProgramSessionPhoto: UserProgramSessionPhoto) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.createUserProgramSessionPhotoAPI(userProgramSessionPhoto)
            if (result.isSuccess) {
                val newUserProgramSessionPhoto = result.getOrNull()
                newUserProgramSessionPhoto?.let { repository.insertUserProgramSessionPhoto(it.asEntity()) }
                Log.d("CREATE USER PROGRAM SESSION PHOTO", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM SESSION PHOTO", "Creating user program session photo failed")
            }
        }
    }

    suspend fun updateUserProgramSessionPhoto(userProgramSessionPhoto: UserProgramSessionPhoto) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateUserProgramSessionPhotoAPI(userProgramSessionPhoto)
            if (result.isSuccess) {
                val updatedUserProgramSessionPhoto = result.getOrNull()
                updatedUserProgramSessionPhoto?.let { repository.updateUserProgramSessionPhoto(it.asEntity()) }
                Log.d("UPDATE USER PROGRAM SESSION PHOTO", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM SESSION PHOTO", "Updating user program session photo failed")
            }
        }
    }

    suspend fun deleteUserProgramSessionPhoto(userProgramSessionPhoto: UserProgramSessionPhoto) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteUserProgramSessionPhotoAPI(userProgramSessionPhoto.id)
            if (result.isSuccess) {
                repository.deleteUserProgramSessionPhoto(userProgramSessionPhoto.asEntity())
                Log.d("DELETE USER PROGRAM SESSION PHOTO", "SUCCESS")
            }
            else {
                Log.e("ERROR USER PROGRAM SESSION PHOTO", "Deleting user program session photo failed")
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