package com.example.exercisetracker.repository
import com.example.exercisetracker.db.ActiveUser
import com.example.exercisetracker.db.AppProgramTypes
import com.example.exercisetracker.db.User
import com.example.exercisetracker.network.AppProgramTypesJSON
import com.example.exercisetracker.network.UserJSON
import kotlinx.coroutines.flow.Flow


class TrainingRepository(private val localDataSource: LocalDataSource,
                         private val remoteDataSource: RemoteDataSource
) {
    // Local data source methods
    fun getAllUsersFromDatabase(): Flow<List<User>> {
        return localDataSource.getAllUsers()
    }
    suspend fun insertUserToDatabase(user: User): Long {
        return localDataSource.insertUser(user)
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



    // Remote data source methods
    suspend fun getUsers(): List<UserJSON> {
        return remoteDataSource.getUsers()
    }
    suspend fun getUser(id: Int): UserJSON {
        return remoteDataSource.getUser(id)
    }
    suspend fun createUser(user: User): Result<UserJSON> {
        return remoteDataSource.createUser(user)
    }
    suspend fun getAllAppProgramTypes(): List<AppProgramTypesJSON> {
        return remoteDataSource.getAllAppProgramTypes()
    }
    suspend fun getAppProgramTypes(back_color: String): List<AppProgramTypes> {
        return remoteDataSource.getAppProgramTypes(back_color)
    }

}
