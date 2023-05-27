package com.example.exercisetracker.db
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ActiveUserEntity::class,
        AppProgramTypeEntity::class,
        UserEntity::class,
        UserProgramEntity::class,
        UserProgramExerciseEntity::class,
        UserExerciseEntity::class,
        UserProgramSessionEntity::class,
        UserProgramSessionDataEntity::class,
        UserProgramSessionPhotoEntity::class,
        UserStatsEntity::class,
        UserProgramSessionEntityOffline::class,
        UserProgramSessionDataEntityOffline::class,
        UserProgramSessionPhotoEntityOffline::class,

    ],
    version = 12,
    exportSchema = false
)
abstract class TrainingDatabase : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao

    companion object {
        @Volatile
        private var INSTANCE: TrainingDatabase? = null
        fun getDatabase(context : Context): TrainingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrainingDatabase:: class.java,
                    "training_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}

