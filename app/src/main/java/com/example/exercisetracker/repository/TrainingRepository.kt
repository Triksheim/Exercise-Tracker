package com.example.exercisetracker.repository
import com.example.exercisetracker.db.*
import com.example.exercisetracker.network.*
import kotlinx.coroutines.flow.Flow


class TrainingRepository(private val localDataSource: LocalDataSource, // database
                         private val remoteDataSource: RemoteDataSource // network
) {
    // Local data source methods
    // User
    suspend fun getAllUsers(): Flow<List<UserEntity>> {
        return localDataSource.getAllUsers()
    }
    suspend fun insertUser(userEntity: UserEntity): Long {
        return localDataSource.insertUser(userEntity)
    }
    suspend fun deleteAllUsers() {
        return localDataSource.deleteAllUsers()
    }

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
    suspend fun deleteProgramTypes() {
        return localDataSource.deleteProgramTypes()
    }


    // UserProgram
    suspend fun insertUserProgram(userProgramEntity: UserProgramEntity ): Long {
        return localDataSource.insertUserProgram(userProgramEntity)
    }
    suspend fun getUserPrograms(): Flow<List<UserProgramEntity>> {
        return localDataSource.getUserPrograms()
    }
    suspend fun deleteUserPrograms() {
        return localDataSource.deleteUserPrograms()
    }

    // UserExercise
    suspend fun insertUserExercise(userExerciseEntity: UserExerciseEntity): Long {
        return localDataSource.insertUserExercise(userExerciseEntity)
    }
    suspend fun getUserExercises():Flow<List<UserExerciseEntity>> {
        return localDataSource.getUserExercises()
    }
    suspend fun deleteUserExercises() {
        return localDataSource.deleteUserExercises()
    }

    // UserProgramExercise
    suspend fun insertUserProgramExercise(userProgramExerciseEntity: UserProgramExerciseEntity): Long {
        return localDataSource.insertUserProgramExercise(userProgramExerciseEntity)
    }
    suspend fun deleteUserProgramExercises() {
        return localDataSource.deleteUserProgramExercises()
    }

    // UserProgramSession
    suspend fun insertUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity): Long {
        return localDataSource.insertUserProgramSession(userProgramSessionEntity)
    }
    suspend fun getUserProgramSessions(): Flow<List<UserProgramSessionEntity>> {
        return localDataSource.getUserProgramSessions()
    }
    suspend fun deleteUserProgramSessions() {
        return localDataSource.deleteUserProgramSessions()
    }

    // UserProgramSessionData
    suspend fun insertUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity): Long {
        return localDataSource.insertUserProgramSessionData(userProgramSessionDataEntity)
    }
    suspend fun getAllUserProgramSessionData(): Flow<List<UserProgramSessionDataEntity>> {
        return localDataSource.getAllUserProgramSessionData()
    }
    suspend fun deleteAllUserProgramSessionData() {
        return localDataSource.deleteAllUserProgramSessionData()
    }

    // UserProgramSessionPhoto
    suspend fun insertUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity): Long {
        return localDataSource.insertUserProgramSessionPhoto(userProgramSessionPhotoEntity)
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
    suspend fun getUserProgramsAPI(userId: Int): Result<List<UserProgramJSON>> {
        return remoteDataSource.getUserPrograms(userId)
    }
    suspend fun getUserProgramAPI(id: Int): Result<UserProgramJSON> {
        return remoteDataSource.getUserProgram(id)
    }

    // UserExercise
    suspend fun createUserExerciseAPI(userExercise: UserExercise): Result<UserExerciseJSON> {
        return remoteDataSource.createUserExercise(userExercise)
    }
    suspend fun getUserExercisesAPI(userId: Int): Result<List<UserExerciseJSON>> {
        return remoteDataSource.getUserExercises(userId)
    }

    // UserProgramExercise
    suspend fun createUserProgramExerciseAPI(userProgramExercise: UserProgramExercise): Result<UserProgramExerciseJSON> {
        return remoteDataSource.createUserProgramExercise(userProgramExercise)
    }
    suspend fun getUserProgramExercisesAPI(userProgramId: Int): Result<List<UserProgramExerciseJSON>> {
        return remoteDataSource.getUserProgramExercises(userProgramId)
    }

    // UserProgramSession
    suspend fun createUserProgramSessionAPI(userProgramSession: UserProgramSession): Result<UserProgramSessionJSON> {
        return remoteDataSource.createUserProgramSession(userProgramSession)
    }

    suspend fun getUserProgramSessionsAPI(userProgramId: Int): Result<List<UserProgramSessionJSON>> {
        return remoteDataSource.getUserProgramSessions(userProgramId)
    }

    // UserProgramSessionData
    suspend fun createUserProgramSessionDataAPI(userProgramSessionData: UserProgramSessionData): Result<UserProgramSessionDataJSON> {
        return remoteDataSource.createUserProgramSessionData(userProgramSessionData)
    }
    suspend fun getALlUserProgramSessionDataAPI(userId: Int): Result<List<UserProgramSessionDataJSON>> {
        return remoteDataSource.getAllUserProgramSessionData(userId)
    }

    // UserProgramSessionPhoto
    suspend fun createUserProgramSessionPhotoAPI(userProgramSessionPhoto: UserProgramSessionPhoto): Result<UserProgramSessionPhotoJSON> {
        return remoteDataSource.createUserProgramSessionPhoto(userProgramSessionPhoto)
    }

}
