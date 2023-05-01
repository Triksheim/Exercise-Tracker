package com.example.exercisetracker.db
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.exercisetracker.db.AppProgramType
import com.example.exercisetracker.db.User
import com.example.exercisetracker.db.UserProgram
import com.example.exercisetracker.db.UserProgramExercise
import com.example.exercisetracker.db.UserExercise
import com.example.exercisetracker.db.UserProgramSession
import com.example.exercisetracker.db.UserProgramSessionData
import com.example.exercisetracker.db.UserProgramSessionPhoto

@Database(
    entities = [
        AppProgramType::class,
        User::class,
        UserProgram::class,
        UserProgramExercise::class,
        UserExercise::class,
        UserProgramSession::class,
        UserProgramSessionData::class,
        UserProgramSessionPhoto::class,
    ],
    version = 3,
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

