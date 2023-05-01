package com.example.exercisetracker.repository
import com.example.exercisetracker.db.ActiveUser
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

    suspend fun addActiveUser(activeUser: ActiveUser) {
        return trainingDao.addActiveUser(activeUser)
    }

    suspend fun getActiveUser(): Result<ActiveUser> {
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

}





