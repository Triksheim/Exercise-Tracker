package com.example.exercisetracker.repository
import com.example.exercisetracker.db.*
import com.example.exercisetracker.network.*
import kotlinx.coroutines.flow.Flow


class TrainingRepository(private val localDataSource: LocalDataSource,
                         private val remoteDataSource: RemoteDataSource
) {
    // Local data source methods
    // User
    suspend fun getAllUsers(): Flow<List<User>> {
        return localDataSource.getAllUsers()
    }
    suspend fun insertUser(user: User): Long {
        return localDataSource.insertUser(user)
    }
    suspend fun deleteAllUsers() {
        return localDataSource.deleteAllUsers()
    }

    // ActiveUser
    suspend fun addActiveUser(activeUser: ActiveUser) {
        return localDataSource.addActiveUser(activeUser)
    }
    suspend fun getActiveUser(): Result<ActiveUser> {
        return localDataSource.getActiveUser()
    }
    suspend fun removeActiveUser() {
        return localDataSource.removeActiveUser()
    }

    // ProgramType
    suspend fun insertProgramType(appProgramType: AppProgramType): Long {
        return localDataSource.insertProgramType(appProgramType)
    }
    suspend fun getProgramTypes(): Flow<List<AppProgramType>> {
        return localDataSource.getProgramTypes()
    }
    suspend fun deleteProgramTypes() {
        return localDataSource.deleteProgramTypes()
    }


    // UserProgram
    suspend fun insertUserProgram(userProgram: UserProgram ): Long {
        return localDataSource.insertUserProgram(userProgram)
    }
    suspend fun getUserPrograms(): Flow<List<UserProgram>> {
        return localDataSource.getUserPrograms()
    }
    suspend fun deleteUserPrograms() {
        return localDataSource.deleteUserPrograms()
    }

    // UserExercise
    suspend fun insertUserExercise(userExercise: UserExercise): Long {
        return localDataSource.insertUserExercise(userExercise)
    }
    suspend fun getUserExercises():Flow<List<UserExercise>> {
        return localDataSource.getUserExercises()
    }
    suspend fun deleteUserExercises() {
        return localDataSource.deleteUserExercises()
    }

    // UserProgramSession
    suspend fun insertUserProgramSession(userProgramSession: UserProgramSession): Long {
        return localDataSource.insertUserProgramSession(userProgramSession)
    }
    suspend fun getUserProgramSessions(): Flow<List<UserProgramSession>> {
        return localDataSource.getUserProgramSessions()
    }
    suspend fun deleteUserProgramSessions() {
        return localDataSource.deleteUserProgramSessions()
    }

    // UserProgramSessionData
    suspend fun insertUserProgramSessionData(userProgramSessionData: UserProgramSessionData): Long {
        return localDataSource.insertUserProgramSessionData(userProgramSessionData)
    }
    suspend fun getAllUserProgramSessionData(): Flow<List<UserProgramSessionData>> {
        return localDataSource.getAllUserProgramSessionData()
    }
    suspend fun deleteAllUserProgramSessionData() {
        return localDataSource.deleteAllUserProgramSessionData()
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
    suspend fun createUserProgramAPI(userProgram: UserProgram): Result<UserProgramJSON> {
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

    // UserProgramSession
    suspend fun getUserProgramSessionsAPI(userProgramId: Int): Result<List<UserProgramSessionJSON>> {
        return remoteDataSource.getUserProgramSessions(userProgramId)
    }

    // UserProgramSessionData
    suspend fun getALlUserProgramSessionDataAPI(userId: Int): Result<List<UserProgramSessionDataJSON>> {
        return remoteDataSource.getAllUserProgramSessionData(userId)
    }


}
