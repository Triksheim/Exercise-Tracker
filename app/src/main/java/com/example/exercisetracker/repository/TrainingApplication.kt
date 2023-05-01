package com.example.exercisetracker.repository

import android.app.Application
import com.example.exercisetracker.repository.ServiceLocator
import com.example.exercisetracker.repository.TrainingRepository


class TrainingApplication: Application() {
    val trainingRepository: TrainingRepository
        get() = ServiceLocator.provideTrainingRepository(this)
}