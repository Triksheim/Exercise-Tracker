package com.example.exercisetracker.utils

import android.content.Context
import com.example.exercisetracker.db.TrainingDatabase
import com.example.exercisetracker.repository.TrainingRepository

object ServiceLocator {


    var trainingRepository: TrainingRepository? = null

    fun provideTrainingRepository(context: Context): TrainingRepository {
        synchronized(this) {
            return trainingRepository ?: createTrainingRepository(context)
        }
    }

    private fun createTrainingRepository(context: Context): TrainingRepository {
        val localDatasource = createLocalDataSource(context)
        val remoteDataSource = createRemoteDataSource(context)
        val newRepository = TrainingRepository(localDatasource, remoteDataSource)
        trainingRepository = newRepository
        return newRepository
    }

    private fun createLocalDataSource(context: Context): LocalDataSource{
        val database: TrainingDatabase by lazy { TrainingDatabase.getDatabase(context)}
        return LocalDataSource(database.trainingDao())
    }

    private fun createRemoteDataSource(context: Context): RemoteDataSource{
        val database: TrainingDatabase by lazy { TrainingDatabase.getDatabase(context)}
        return RemoteDataSource()
    }

}
