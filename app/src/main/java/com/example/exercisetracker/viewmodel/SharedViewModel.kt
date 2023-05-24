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
import com.example.exercisetracker.network.UserStatsJSON
import com.example.exercisetracker.utils.asDomainModel
import com.example.exercisetracker.utils.asEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext


class SharedViewModel(private val repository: TrainingRepository) : ViewModel() {

    private val _startupDone = MutableLiveData<Boolean>()
    val startupDone: LiveData<Boolean> = _startupDone

    private val _networkConnectionOk = MutableLiveData<Boolean>()
    val networkConnectionOk: LiveData<Boolean> = _networkConnectionOk

    private val _activeUser = MutableLiveData<ActiveUserEntity?>()
    val activeUser: LiveData<ActiveUserEntity?> = _activeUser

    private val _createUserStatus = MutableLiveData<Result<UserJSON>>()
    val createUserStatus: LiveData<Result<UserJSON>> = _createUserStatus

    private val _users = MutableLiveData<List<UserJSON>>()
    val users: LiveData<List<UserJSON>> = _users

    private val _programTypes = MutableStateFlow<List<AppProgramType>>(emptyList())
    val programTypes: StateFlow<List<AppProgramType>> = _programTypes

    private var _currentProgramType = MutableLiveData<AppProgramType>()
    val currentProgramType: LiveData<AppProgramType> = _currentProgramType

    private val _userPrograms = MutableStateFlow<List<UserProgram>>(emptyList())
    val userPrograms: StateFlow<List<UserProgram>> = _userPrograms

    private val _userProgramType = MutableStateFlow<AppProgramType?>(null)
    val userProgramType: StateFlow<AppProgramType?> = _userProgramType

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

    val displayableSessions: Flow<List<DisplayableSession>> = allSessions.map { sessions ->
            sessions.map { session ->
                val userProgram = repository.getUserProgram(session.user_program_id).first()
                val programType = repository.getProgramTypeById(userProgram.app_program_type_id).first()
                DisplayableSession(
                    sessionId = session.id,
                    userProgramId = session.user_program_id,
                    sessionStartTime = session.startTime,
                    sessionTimeSpent = session.time_spent,
                    sessionDescription = session.description,
                    userProgramName = userProgram.name,
                    programTypeIcon = programType.icon
                )
            }
        }

    private val _userStats = MutableLiveData<UserStatsJSON>()
    val userStats: LiveData<UserStatsJSON> = _userStats




    private var _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String> = _toolbarTitle

    init {
        _startupDone.value = false
        _networkConnectionOk.value = true
        viewModelScope.launch {
            restart()
        }
        flowAppProgramTypes()
        flowUserPrograms()
        flowUserExercises()
        flowUserProgramSessions()
    }

    fun formatSeconds(timeSpent: String?): String {
        if (timeSpent == null) {
            return "00:00:00"
        }
        else {
            val seconds = timeSpent.toInt()
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val remainingSeconds = seconds % 60

            return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
        }
    }


    private fun flowAppProgramTypes() {
        viewModelScope.launch {
            repository.getProgramTypes()
                .flowOn(Dispatchers.IO)
                .map { appProgramTypesList -> appProgramTypesList.map { it.asDomainModel() } }
                .collect { appProgramTypesList ->
                    _programTypes.value = appProgramTypesList
                }
        }
    }
    private fun flowUserPrograms() {
        viewModelScope.launch {
            repository.getUserPrograms()
                .flowOn(Dispatchers.IO)
                .map { userProgramsList -> userProgramsList.map { it.asDomainModel() } }
                .collect { userProgramsList ->
                    _userPrograms.value = userProgramsList
                }
        }
    }
    private fun flowUserExercises() {
        viewModelScope.launch {
            repository.getUserExercises()
                .flowOn(Dispatchers.IO)
                .map { userExerciseList -> userExerciseList.map { it.asDomainModel() } }
                .collect { userExerciseList ->
                    _userExercises.value = userExerciseList
                }
        }
    }
    private fun flowUserProgramSessions() {
        viewModelScope.launch {
            repository.getUserProgramSessions()
                .flowOn(Dispatchers.IO)
                .map { sessionsList -> sessionsList.map { it.asDomainModel() } }
                .collect { sessionsList ->
                    _allSessions.value = sessionsList
                }
        }
    }

    fun flowExercisesForCurrentProgram() {
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

    // Find ProgramType for a UserProgram and set programType to userProgramType
    suspend fun getProgramTypeForProgram(userProgram: UserProgram) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getProgramTypeById(userProgram.app_program_type_id)
                .map { it.asDomainModel() }
                .firstOrNull()
            _userProgramType.value = result
        }
    }

    fun setProgramTypeByUserProgram(userProgram: UserProgram) {
        val programTypes = programTypes.value
        _currentProgramType.value = programTypes.find { it.id == userProgram.app_program_type_id }
    }

    fun setCurrentProgramType(programType: AppProgramType) {
        _currentProgramType.value = programType
    }

    fun setCurrentUserProgram(userProgram: UserProgram){
        _currentProgram.value = userProgram
    }

    fun setCurrentSession(userProgramSession: UserProgramSession) {
        _currentSession.value = userProgramSession
    }

    suspend fun login(phone: String): Boolean {
        return viewModelScope.async {
        getAllUsers()
        for (user in users.value!!) {
            if (user.phone == phone) {
                Log.d("LOGIN SUCCESS", "ID: ${user.id}")
                setActiveUser(user.asEntity().asDomainModel())
                _users.postValue(emptyList())
                restart()
                return@async true
            }
        }
            _users.postValue(emptyList())
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
            _activeUser.postValue(null)
            _createUserStatus.value = Result.success(UserJSON(0,"0","0","0",0))
            clearDb()
            restart()
        }
    }

    fun isActiveUser(): Boolean {
        return activeUser.value != null
    }

    fun isValidProgramEntry(name: String, description: String): Boolean{
        return name.isNotBlank() && description.isNotBlank()
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

    fun setToolbarTitle(title:String) {
        _toolbarTitle.value =  title
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
            if (!isActiveUser()) {
                val resultActiveUser = repository.getActiveUser()
                if (resultActiveUser.isSuccess) {
                    val user = resultActiveUser.getOrNull()
                    if (user != null) {
                        _activeUser.value = user!!
                    }
                }
            }
            else if (isActiveUser()) {
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
            _startupDone.postValue(true)
        }
    }


    private suspend fun getAllUsers() {
        withContext(Dispatchers.IO) {
            val resultUsers = repository.getUsersAPI()
            if (resultUsers.isSuccess) {
                _networkConnectionOk.postValue(true)
                Log.d("RESULT USERS API", "SUCCESS")
                val users = resultUsers.getOrNull()
                _users.postValue(users!!)
            } else {
                _networkConnectionOk.postValue(false)
                _users.postValue(emptyList())
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

    suspend fun getUserStats() {
        withContext(Dispatchers.IO) {
            val result = repository.getUserStatsAPI(activeUser.value!!.id)
            if (result.isSuccess) {
                _networkConnectionOk.postValue(true)
                Log.d("RESULT USER STATS", "SUCCESS")
                val stats = result.getOrNull()
                _userStats.postValue(stats!!)
            } else {
                _networkConnectionOk.postValue(false)
                Log.e("ERROR USER STATS API", "Unable to fetch")
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
        }
    }

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
    suspend fun createUserProgramSession(userProgramSession: UserProgramSession): Int {
        return viewModelScope.async(Dispatchers.IO) {
            val result = repository.createUserProgramSessionAPI(userProgramSession)
            if (result.isSuccess) {
                val newUserProgramSession = result.getOrNull()
                newUserProgramSession?.let {
                    repository.insertUserProgramSession(it.asEntity())
                    Log.d("CREATE USER PROGRAM SESSION", "SUCCESS")
                    return@async it.id // Return the ID
                }
            }
            throw Exception("Creating user program session failed") // Throw an exception if the ID cannot be retrieved
        }.await() // Wait for the coroutine to complete and return the result
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
    // Inert a list of UserProgramSessionData with async to run inserts operation in parallel
    suspend fun insertUserProgramSessionDataList(userProgramSessionDataList: List<UserProgramSessionData>) {
        viewModelScope.launch(Dispatchers.IO) {
            userProgramSessionDataList.map { userProgramSessionData ->
                async { createUserProgramSessionData(userProgramSessionData) }
            }.awaitAll()
        }
    }

    private suspend fun createUserProgramSessionData(userProgramSessionData: UserProgramSessionData) {
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