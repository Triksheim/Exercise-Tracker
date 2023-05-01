package com.example.exercisetracker.utils

import android.app.Application
import com.example.exercisetracker.repository.TrainingRepository

class TrainingApplication: Application() {
    val trainingRepository: TrainingRepository
        get() = ServiceLocator.provideTrainingRepository(this)
}