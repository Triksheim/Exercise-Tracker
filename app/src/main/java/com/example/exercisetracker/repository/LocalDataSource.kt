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

    suspend fun updateUserProgram(userProgramEntity: UserProgramEntity) {
        return trainingDao.updateUserProgram(userProgramEntity)
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
    suspend fun deleteUserProgram(userProgramEntity: UserProgramEntity) {
        return trainingDao.deleteUserProgram(userProgramEntity)
    }


    // UserExercise
    suspend fun insertUserExercise(userExerciseEntity: UserExerciseEntity): Long {
        return trainingDao.insertUserExercise(userExerciseEntity)
    }
    suspend fun getUserExercises(): Flow<List<UserExerciseEntity>> {
        return trainingDao.getAllUserExercises()
    }
    suspend fun updateUserExercise(userExerciseEntity: UserExerciseEntity) {
        return trainingDao.updateUserExercise(userExerciseEntity)
    }
    suspend fun deleteUserExercises() {
        return trainingDao.deleteAllUserExercises()
    }
    suspend fun deleteUserExercise(userExerciseEntity: UserExerciseEntity) {
        return trainingDao.deleteUserExercise(userExerciseEntity)
    }


    // UserProgramExercise
    suspend fun insertUserProgramExercise(userProgramExerciseEntity: UserProgramExerciseEntity): Long {
        return trainingDao.insertUserProgramExercise(userProgramExerciseEntity)
    }
    suspend fun deleteUserProgramExercises() {
        return trainingDao.deleteAllUserProgramExercises()
    }
    suspend fun deleteUserProgramExercise(userProgramExerciseEntity: UserProgramExerciseEntity) {
        return trainingDao.deleteUserProgramExercise(userProgramExerciseEntity)
    }
    suspend fun getUserProgramExercisesById(id: Int): Flow<List<UserProgramExerciseEntity>> {
        return trainingDao.getUserProgramExerciseById(id)
    }


    // UserProgramSession
    suspend fun insertUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity): Long {
        return trainingDao.insertUserProgramSession(userProgramSessionEntity)
    }
    suspend fun getUserProgramSessions(): Flow<List<UserProgramSessionEntity>> {
        return trainingDao.getAllUserProgramSessions()
    }
    suspend fun getSessionsforProgramId(id: Int): List<UserProgramSessionEntity> {
        return trainingDao.getSessionsForProgramId(id)
    }
    suspend fun updateUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity) {
        return trainingDao.updateUserProgramSession(userProgramSessionEntity)
    }
    suspend fun deleteUserProgramSessions() {
        return trainingDao.deleteAllUserProgramSessions()
    }
    suspend fun deleteUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity) {
        return trainingDao.deleteUserProgramSession(userProgramSessionEntity)
    }


    // UserProgramSessionData
    suspend fun insertUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity): Long {
        return trainingDao.insertUserProgramSessionData(userProgramSessionDataEntity)
    }
    suspend fun getAllUserProgramSessionData(): Flow<List<UserProgramSessionDataEntity>> {
        return trainingDao.getAllUserProgramSessionData()
    }
    suspend fun getDataForSessionId(id: Int): List<UserProgramSessionDataEntity> {
        return trainingDao.getDataForSessionId(id)
    }
    suspend fun updateUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity) {
        return trainingDao.updateUserProgramSessionData(userProgramSessionDataEntity)
    }
    suspend fun deleteAllUserProgramSessionData() {
        return trainingDao.deleteAllUserProgramSessionData()
    }
    suspend fun deleteUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity) {
        return trainingDao.deleteUserProgramSessionData(userProgramSessionDataEntity)
    }


    // UserProgramSessionPhoto
    suspend fun insertUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity): Long {
        return trainingDao.insertUserProgramSessionPhoto(userProgramSessionPhotoEntity)
    }
    suspend fun getPhotosForSessionId(id: Int): List<UserProgramSessionPhotoEntity> {
        return trainingDao.getPhotosForSessionId(id)
    }
    suspend fun updateUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity) {
        return trainingDao.updateUserProgramSessionPhoto(userProgramSessionPhotoEntity)
    }
    suspend fun deleteUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity) {
        return trainingDao.deleteUserProgramSessionPhoto(userProgramSessionPhotoEntity)
    }

}





