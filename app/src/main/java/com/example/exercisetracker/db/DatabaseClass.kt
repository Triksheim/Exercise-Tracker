package com.example.exercisetracker.db
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ActiveUser::class,
        AppProgramType::class,
        User::class,
        UserProgram::class,
        UserProgramExercise::class,
        UserExercise::class,
        UserProgramSession::class,
        UserProgramSessionData::class,
        UserProgramSessionPhoto::class,
    ],
    version = 6,
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
                    "item"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}

