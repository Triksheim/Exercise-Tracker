package com.example.exercisetracker.repository
import com.example.exercisetracker.db.ActiveUser
import com.example.exercisetracker.db.User
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

}
