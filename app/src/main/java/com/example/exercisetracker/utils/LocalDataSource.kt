package com.example.exercisetracker.utils
import com.example.exercisetracker.db.TrainingDao
import com.example.exercisetracker.db.User
import kotlinx.coroutines.flow.Flow


class LocalDataSource internal constructor (private val trainingDao: TrainingDao) {
    fun getAllUsers(): Flow<List<User>> {
        return trainingDao.getAllUsers()
    }

    suspend fun insertUser(user: User): Long {
        return trainingDao.insertUser(user)
    }
}
