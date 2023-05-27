package com.example.exercisetracker.repository
import com.example.exercisetracker.db.*
import com.example.exercisetracker.network.*
import kotlinx.coroutines.flow.Flow


class TrainingRepository(private val localDataSource: LocalDataSource, // database
                         private val remoteDataSource: RemoteDataSource // network
) {
    // Local data source methods -------------------------------------------------------------

    // ActiveUser
    suspend fun addActiveUser(activeUserEntity: ActiveUserEntity) {
        return localDataSource.addActiveUser(activeUserEntity)
    }
    suspend fun getActiveUser(): Result<ActiveUserEntity> {
        return localDataSource.getActiveUser()
    }
    suspend fun removeActiveUser() {
        return localDataSource.removeActiveUser()
    }


    // ProgramType
    suspend fun insertProgramType(appProgramTypeEntity: AppProgramTypeEntity): Long {
        return localDataSource.insertProgramType(appProgramTypeEntity)
    }
    suspend fun getProgramTypes(): Flow<List<AppProgramTypeEntity>> {
        return localDataSource.getProgramTypes()
    }

    suspend fun getProgramTypeById(id: Int): Flow<AppProgramTypeEntity> {
        return localDataSource.getProgramTypeById(id)
    }
    suspend fun deleteProgramTypes() {
        return localDataSource.deleteProgramTypes()
    }


    // UserProgram
    suspend fun insertUserProgram(userProgramEntity: UserProgramEntity ): Long {
        return localDataSource.insertUserProgram(userProgramEntity)
    }
    suspend fun updateUserProgram(userProgramEntity: UserProgramEntity) {
        return localDataSource.updateUserProgram(userProgramEntity)
    }
    suspend fun getUserPrograms(): Flow<List<UserProgramEntity>> {
        return localDataSource.getUserPrograms()
    }
    suspend fun getUserProgram(id: Int): Flow<UserProgramEntity> {
        return localDataSource.getUserProgram(id)
    }
    suspend fun deleteUserPrograms() {
        return localDataSource.deleteUserPrograms()
    }
    suspend fun deleteUserProgram(userProgramEntity: UserProgramEntity) {
        return localDataSource.deleteUserProgram(userProgramEntity)
    }


    // UserExercise
    suspend fun insertUserExercise(userExerciseEntity: UserExerciseEntity): Long {
        return localDataSource.insertUserExercise(userExerciseEntity)
    }
    suspend fun getUserExerciseById(id: Int): UserExerciseEntity {
        return localDataSource.getUserExerciseById(id)
    }
    suspend fun getUserExercises():Flow<List<UserExerciseEntity>> {
        return localDataSource.getUserExercises()
    }
    suspend fun updateUserExercise(userExerciseEntity: UserExerciseEntity) {
        return localDataSource.updateUserExercise(userExerciseEntity)
    }
    suspend fun deleteUserExercises() {
        return localDataSource.deleteUserExercises()
    }
    suspend fun deleteUserExercise(userExerciseEntity: UserExerciseEntity) {
        return localDataSource.deleteUserExercise(userExerciseEntity)
    }


    // UserProgramExercise
    suspend fun insertUserProgramExercise(userProgramExerciseEntity: UserProgramExerciseEntity): Long {
        return localDataSource.insertUserProgramExercise(userProgramExerciseEntity)
    }
    suspend fun getUserExerciseIdsForProgramId(id: Int): List<Int> {
        return localDataSource.getUserExerciseIdsForProgramId(id)
    }
    suspend fun deleteUserProgramExercises() {
        return localDataSource.deleteUserProgramExercises()
    }
    suspend fun deleteUserProgramExercise(userProgramExerciseEntity: UserProgramExerciseEntity) {
        return localDataSource.deleteUserProgramExercise(userProgramExerciseEntity)
    }
    suspend fun getUserProgramExercisesById(id: Int): Flow<List<UserProgramExerciseEntity>> {
        return localDataSource.getUserProgramExercisesById(id)
    }


    // UserProgramSession
    suspend fun insertUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity): Long {
        return localDataSource.insertUserProgramSession(userProgramSessionEntity)
    }
    suspend fun getUserProgramSessions(): Flow<List<UserProgramSessionEntity>> {
        return localDataSource.getUserProgramSessions()
    }
    suspend fun getSessionsForProgramId(id: Int): List<UserProgramSessionEntity> {
        return localDataSource.getSessionsforProgramId(id)
    }
    suspend fun updateUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity) {
        return localDataSource.updateUserProgramSession(userProgramSessionEntity)
    }
    suspend fun deleteUserProgramSessions() {
        return localDataSource.deleteUserProgramSessions()
    }
    suspend fun deleteUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity) {
        return localDataSource.deleteUserProgramSession(userProgramSessionEntity)
    }


    // UserProgramSessionData
    suspend fun insertUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity): Long {
        return localDataSource.insertUserProgramSessionData(userProgramSessionDataEntity)
    }
    suspend fun getAllUserProgramSessionData(): Flow<List<UserProgramSessionDataEntity>> {
        return localDataSource.getAllUserProgramSessionData()
    }
    suspend fun getDataForSessionId(id: Int): List<UserProgramSessionDataEntity> {
        return localDataSource.getDataForSessionId(id)
    }
    suspend fun updateUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity) {
        return localDataSource.updateUserProgramSessionData(userProgramSessionDataEntity)
    }
    suspend fun deleteAllUserProgramSessionData() {
        return localDataSource.deleteAllUserProgramSessionData()
    }
    suspend fun deleteUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity) {
        return localDataSource.deleteUserProgramSessionData(userProgramSessionDataEntity)
    }


    // UserProgramSessionPhoto
    suspend fun insertUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity): Long {
        return localDataSource.insertUserProgramSessionPhoto(userProgramSessionPhotoEntity)
    }
    suspend fun getPhotosForSessionId(id: Int): List<UserProgramSessionPhotoEntity> {
        return localDataSource.getPhotosForSessionId(id)
    }
    suspend fun updateUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity) {
        return localDataSource.updateUserProgramSessionPhoto(userProgramSessionPhotoEntity)
    }
    suspend fun deleteUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity) {
        return localDataSource.deleteUserProgramSessionPhoto(userProgramSessionPhotoEntity)
    }

    // UserStats
    suspend fun insertUserStats(userStatsEntity: UserStatsEntity): Long {
        return localDataSource.insertUserStats(userStatsEntity)
    }
    fun getUserStats(): Flow<UserStatsEntity> {
        return localDataSource.getUserStats()
    }
    suspend fun deleteUserStats() {
        return localDataSource.deleteUserStats()
    }

    // UserProgramSessionOffline
    suspend fun insertUserProgramSessionOffline(userProgramSessionEntityOffline: UserProgramSessionEntityOffline): Long {
        return localDataSource.insertUserProgramSessionOffline(userProgramSessionEntityOffline)
    }

    suspend fun getAllUserProgramSessionsOffline(): List<UserProgramSessionEntityOffline> {
        return localDataSource.getAllUserProgramSessionsOffline()
    }

    suspend fun updateUserProgramSessionOffline(userProgramSessionEntityOffline: UserProgramSessionEntityOffline) {
        return localDataSource.updateUserProgramSessionOffline(userProgramSessionEntityOffline)
    }

    suspend fun deleteAllUserProgramSessionsOffline() {
        return localDataSource.deleteAllUserProgramSessionsOffline()
    }

    suspend fun deleteUserProgramSessionOffline(userProgramSessionEntityOffline: UserProgramSessionEntityOffline) {
        return localDataSource.deleteUserProgramSessionOffline(userProgramSessionEntityOffline)
    }


    // UserProgramSessionDataOffline
    suspend fun insertUserProgramSessionDataOffline(userProgramSessionDataEntityOffline: UserProgramSessionDataEntityOffline): Long {
        return localDataSource.insertUserProgramSessionDataOffline(userProgramSessionDataEntityOffline)
    }

    suspend fun getSessionDataForSessionIdOffline(id: Int): List<UserProgramSessionDataEntityOffline> {
        return localDataSource.getSessionDataForSessionIdOffline(id)
    }

    suspend fun deleteAllUserProgramSessionDataOffline() {
        return localDataSource.deleteAllUserProgramSessionDataOffline()
    }


    // UserProgramSessionPhotoOffline
    suspend fun insertUserProgramSessionPhotoOffline(userProgramSessionPhotoEntityOffline: UserProgramSessionPhotoEntityOffline): Long {
        return localDataSource.insertUserProgramSessionPhotoOffline(userProgramSessionPhotoEntityOffline)
    }

    suspend fun getAllUserProgramSessionPhotosOffline(): List<UserProgramSessionPhotoEntityOffline> {
        return localDataSource.getAllUserProgramSessionPhotosOffline()
    }

    suspend fun deleteAllUserProgramSessionPhotosOffline() {
        return localDataSource.deleteAllUserProgramSessionPhotosOffline()
    }



    // Remote data source methods ----------------------------------------------------------------


    // User
    suspend fun getUsersAPI(): Result<List<UserJSON>> {
        return remoteDataSource.getUsers()
    }
    suspend fun getUserAPI(id: Int): Result<UserJSON> {
        return remoteDataSource.getUser(id)
    }
    suspend fun createUserAPI(user: User): Result<UserJSON> {
        return remoteDataSource.createUser(user)
    }

    // AppProgramType
    suspend fun getAppProgramTypesAPI(): Result<List<AppProgramTypeJSON>> {
        return remoteDataSource.getAppProgramTypes()
    }

    // UserProgram
    suspend fun createUserProgramAPI(userProgram: UserProgram):  Result<UserProgramJSON>{
        return remoteDataSource.createUserProgram(userProgram)
    }
    suspend fun updateUserProgramAPI(userProgram: UserProgram): Result<UserProgramJSON> {
        return remoteDataSource.updateUserProgram(userProgram)
    }
    suspend fun getUserProgramsAPI(userId: Int): Result<List<UserProgramJSON>> {
        return remoteDataSource.getUserPrograms(userId)
    }
    suspend fun getUserProgramAPI(id: Int): Result<UserProgramJSON> {
        return remoteDataSource.getUserProgram(id)
    }
    suspend fun deleteUserProgramAPI(id: Int): Result<Unit> {
        return remoteDataSource.deleteUserProgram(id)
    }

    // UserExercise
    suspend fun createUserExerciseAPI(userExercise: UserExercise): Result<UserExerciseJSON> {
        return remoteDataSource.createUserExercise(userExercise)
    }
    suspend fun getUserExercisesAPI(userId: Int): Result<List<UserExerciseJSON>> {
        return remoteDataSource.getUserExercises(userId)
    }
    suspend fun updateUserExerciseAPI(userExercise: UserExercise): Result<UserExerciseJSON> {
        return remoteDataSource.updateUserExercise(userExercise)
    }
    suspend fun deleteUserExerciseAPI(id: Int): Result<Unit> {
        return remoteDataSource.deleteUserExercise(id)
    }

    // UserProgramExercise
    suspend fun createUserProgramExerciseAPI(userProgramExercise: UserProgramExercise): Result<UserProgramExerciseJSON> {
        return remoteDataSource.createUserProgramExercise(userProgramExercise)
    }
    suspend fun getUserProgramExercisesAPI(userProgramId: Int): Result<List<UserProgramExerciseJSON>> {
        return remoteDataSource.getUserProgramExercises(userProgramId)
    }
    suspend fun deleteUserProgramExerciseAPI(id: Int): Result<Unit> {
        return remoteDataSource.deleteUserProgramExercise(id)
    }

    // UserProgramSession
    suspend fun createUserProgramSessionAPI(userProgramSession: UserProgramSession): Result<UserProgramSessionJSON> {
        return remoteDataSource.createUserProgramSession(userProgramSession)
    }
    suspend fun getUserProgramSessionsAPI(userProgramId: Int): Result<List<UserProgramSessionJSON>> {
        return remoteDataSource.getUserProgramSessions(userProgramId)
    }
    suspend fun updateUserProgramSessionAPI(userProgramSession: UserProgramSession): Result<UserProgramSessionJSON> {
        return remoteDataSource.updateUserProgramSession(userProgramSession)
    }
    suspend fun deleteUserProgramSessionAPI(id: Int): Result<Unit> {
        return remoteDataSource.deleteUserProgramSession(id)
    }

    // UserProgramSessionData
    suspend fun createUserProgramSessionDataAPI(userProgramSessionData: UserProgramSessionData): Result<UserProgramSessionDataJSON> {
        return remoteDataSource.createUserProgramSessionData(userProgramSessionData)
    }
    suspend fun getALlUserProgramSessionDataAPI(sessionId: Int): Result<List<UserProgramSessionDataJSON>> {
        return remoteDataSource.getAllUserProgramSessionData(sessionId)
    }
    suspend fun updateUserProgramSessionDataAPI(userProgramSessionData: UserProgramSessionData): Result<UserProgramSessionDataJSON> {
        return remoteDataSource.updateUserProgramSessionData(userProgramSessionData)
    }
    suspend fun deleteUserProgramSessionDataAPI(id: Int): Result<Unit> {
        return remoteDataSource.deleteUserProgramSessionData(id)
    }

    // UserProgramSessionPhoto
    suspend fun createUserProgramSessionPhotoAPI(userProgramSessionPhoto: UserProgramSessionPhoto): Result<UserProgramSessionPhotoJSON> {
        return remoteDataSource.createUserProgramSessionPhoto(userProgramSessionPhoto)
    }
    suspend fun updateUserProgramSessionPhotoAPI(userProgramSessionPhoto: UserProgramSessionPhoto): Result<UserProgramSessionPhotoJSON> {
        return remoteDataSource.updateUserProgramSessionPhoto(userProgramSessionPhoto)
    }
    suspend fun deleteUserProgramSessionPhotoAPI(id: Int): Result<Unit> {
        return remoteDataSource.deleteUserProgramSessionPhoto(id)
    }

    // UserStats
    suspend fun getUserStatsAPI(id: Int): Result<UserStatsJSON> {
        return remoteDataSource.getUserStats(id)
    }
}
