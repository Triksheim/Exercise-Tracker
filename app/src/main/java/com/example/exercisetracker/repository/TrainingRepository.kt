package com.example.exercisetracker.repository
import com.example.exercisetracker.db.ActiveUser
import com.example.exercisetracker.db.AppProgramType
import com.example.exercisetracker.db.User
import com.example.exercisetracker.network.AppProgramTypeJSON
import com.example.exercisetracker.network.UserJSON
import kotlinx.coroutines.flow.Flow


class TrainingRepository(private val localDataSource: LocalDataSource,
                         private val remoteDataSource: RemoteDataSource
) {
    // Local data source methods
    suspend fun getAllUsers(): Flow<List<User>> {
        return localDataSource.getAllUsers()
    }
    suspend fun insertUser(user: User): Long {
        return localDataSource.insertUser(user)
    }

    suspend fun deleteAllUsers() {
        return localDataSource.deleteAllUsers()
    }

    suspend fun addActiveUser(activeUser: ActiveUser) {
        return localDataSource.addActiveUser(activeUser)
    }

    suspend fun getActiveUser(): Result<ActiveUser> {
        return localDataSource.getActiveUser()
    }

    suspend fun removeActiveUser() {
        return localDataSource.removeActiveUser()
    }

    suspend fun insertProgramType(appProgramType: AppProgramType): Long {
        return localDataSource.insertProgramType(appProgramType)
    }

    fun getProgramTypes(): Flow<List<AppProgramType>> {
        return localDataSource.getProgramTypes()
    }

    suspend fun deleteProgramTypes() {
        return localDataSource.deleteProgramTypes()
    }




    // Remote data source methods
    suspend fun getUsersAPI(): Result<List<UserJSON>> {
        return remoteDataSource.getUsers()
    }
    suspend fun getUserAPI(id: Int): UserJSON {
        return remoteDataSource.getUser(id)
    }
    suspend fun createUserAPI(user: User): Result<UserJSON> {
        return remoteDataSource.createUser(user)
    }
    suspend fun getAppProgramTypesAPI(): Result<List<AppProgramTypeJSON>> {
        return remoteDataSource.getAppProgramTypes()
    }



}
