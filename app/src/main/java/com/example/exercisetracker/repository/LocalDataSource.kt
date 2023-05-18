package com.example.exercisetracker.repository
import com.example.exercisetracker.db.*
import kotlinx.coroutines.flow.Flow


class LocalDataSource internal constructor (private val trainingDao: TrainingDao) {
    // User
    fun getAllUsers(): Flow<List<UserEntity>> {
        return trainingDao.getAllUsers()
    }
    suspend fun insertUser(userEntity: UserEntity): Long {
        return trainingDao.insertUser(userEntity)
    }
    suspend fun deleteAllUsers() {
        return trainingDao.deleteAllUsers()

    }

    // ActiveUser
    suspend fun addActiveUser(activeUserEntity: ActiveUserEntity) {
        return trainingDao.addActiveUser(activeUserEntity)
    }
    suspend fun getActiveUser(): Result<ActiveUserEntity> {
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
    suspend fun insertProgramType(appProgramTypeEntity: AppProgramTypeEntity): Long {
        return trainingDao.insertAppProgramTypes(appProgramTypeEntity)
    }
    fun getProgramTypes(): Flow<List<AppProgramTypeEntity>> {
        return trainingDao.getAppProgramTypes()
    }
    suspend fun deleteProgramTypes() {
        return trainingDao.deleteProgramTypes()
    }

    // UserProgram
    suspend fun insertUserProgram(userProgramEntity: UserProgramEntity): Long {
        return trainingDao.insertUserProgram(userProgramEntity)
    }
    suspend fun getUserProgram(id: Int): Flow<UserProgramEntity> {
        return trainingDao.getUserProgramById(id)
    }
    suspend fun getUserPrograms(): Flow<List<UserProgramEntity>> {
        return trainingDao.getAllUserPrograms()
    }
    suspend fun deleteUserPrograms() {
        return trainingDao.deleteAllUserPrograms()
    }

    // UserExercise
    suspend fun insertUserExercise(userExerciseEntity: UserExerciseEntity): Long {
        return trainingDao.insertUserExercise(userExerciseEntity)
    }
    suspend fun getUserExercises(): Flow<List<UserExerciseEntity>> {
        return trainingDao.getAllUserExercises()
    }
    suspend fun deleteUserExercises() {
        return trainingDao.deleteAllUserExercises()
    }

    // UserProgramExercise
    suspend fun insertUserProgramExercise(userProgramExerciseEntity: UserProgramExerciseEntity): Long {
        return trainingDao.insertUserProgramExercise(userProgramExerciseEntity)
    }

    // UserProgramSession
    suspend fun insertUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity): Long {
        return trainingDao.insertUserProgramSession(userProgramSessionEntity)
    }
    suspend fun getUserProgramSessions(): Flow<List<UserProgramSessionEntity>> {
        return trainingDao.getAllUserProgramSessions()
    }
    suspend fun deleteUserProgramSessions() {
        return trainingDao.deleteAllUserProgramSessions()
    }

    // UserProgramSessionData
    suspend fun insertUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity): Long {
        return trainingDao.insertUserProgramSessionData(userProgramSessionDataEntity)
    }
    suspend fun getAllUserProgramSessionData(): Flow<List<UserProgramSessionDataEntity>> {
        return trainingDao.getAllUserProgramSessionData()
    }
    suspend fun deleteAllUserProgramSessionData() {
        return trainingDao.deleteAllUserProgramSessionData()
    }

    // UserProgramSessionPhoto
    suspend fun insertUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity): Long {
        return trainingDao.insertUserProgramSessionPhoto(userProgramSessionPhotoEntity)
    }

}





