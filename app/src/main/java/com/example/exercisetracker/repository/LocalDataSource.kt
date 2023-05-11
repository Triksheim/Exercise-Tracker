package com.example.exercisetracker.repository
import com.example.exercisetracker.db.*
import kotlinx.coroutines.flow.Flow


class LocalDataSource internal constructor (private val trainingDao: TrainingDao) {
    // User
    fun getAllUsers(): Flow<List<User>> {
        return trainingDao.getAllUsers()
    }
    suspend fun insertUser(user: User): Long {
        return trainingDao.insertUser(user)
    }
    suspend fun deleteAllUsers() {
        return trainingDao.deleteAllUsers()

    }

    // ActiveUser
    suspend fun addActiveUser(activeUser: ActiveUser) {
        return trainingDao.addActiveUser(activeUser)
    }
    suspend fun getActiveUser(): Result<ActiveUser> {
        return try {
            val activeUser = trainingDao.getActiveUser()
            Result.success(activeUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun removeActiveUser() {
        return trainingDao.removeActiveUser()
    }

    // ProgramTypes
    suspend fun insertProgramType(appProgramType: AppProgramType): Long {
        return trainingDao.insertAppProgramTypes(appProgramType)
    }
    fun getProgramTypes(): Flow<List<AppProgramType>> {
        return trainingDao.getAppProgramTypes()
    }
    suspend fun deleteProgramTypes() {
        return trainingDao.deleteProgramTypes()
    }

    // UserProgram
    suspend fun insertUserProgram(userProgram: UserProgram): Long {
        return trainingDao.insertUserProgram(userProgram)
    }
    suspend fun getUserProgram(id: Int): Flow<UserProgram> {
        return trainingDao.getUserProgramById(id)
    }
    suspend fun getUserPrograms(): Flow<List<UserProgram>> {
        return trainingDao.getAllUserPrograms()
    }
    suspend fun deleteUserPrograms() {
        return trainingDao.deleteAllUserPrograms()
    }

    // UserExercise
    suspend fun insertUserExercise(userExercise: UserExercise): Long {
        return trainingDao.insertUserExercise(userExercise)
    }
    suspend fun getUserExercises(): Flow<List<UserExercise>> {
        return trainingDao.getAllUserExercises()
    }
    suspend fun deleteUserExercises() {
        return trainingDao.deleteAllUserExercises()
    }

    // UserProgramSession
    suspend fun insertUserProgramSession(userProgramSession: UserProgramSession): Long {
        return trainingDao.insertUserProgramSession(userProgramSession)
    }
    suspend fun getUserProgramSessions(): Flow<List<UserProgramSession>> {
        return trainingDao.getAllUserProgramSessions()
    }
    suspend fun deleteUserProgramSessions() {
        return trainingDao.deleteAllUserProgramSessions()
    }

    // UserProgramSessionData
    suspend fun insertUserProgramSessionData(userProgramSessionData: UserProgramSessionData): Long {
        return trainingDao.insertUserProgramSessionData(userProgramSessionData)
    }
    suspend fun getAllUserProgramSessionData(): Flow<List<UserProgramSessionData>> {
        return trainingDao.getAllUserProgramSessionData()
    }
    suspend fun deleteAllUserProgramSessionData() {
        return trainingDao.deleteAllUserProgramSessionData()
    }
}





