package com.example.exercisetracker.repository
import com.example.exercisetracker.db.TrainingDao
import com.example.exercisetracker.db.User
import com.example.exercisetracker.utils.LocalDataSource
import com.example.exercisetracker.utils.RemoteDataSource
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

    // Remote data source methods
    //suspend fun getAllUsersFromApi(): List<User>? {
      //  return remoteDataSource.getUsers()

    //}
}
