package com.example.exercisetracker.db
import androidx.room.*
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "active_user")
data class ActiveUserEntity(
    @PrimaryKey()
    @ColumnInfo(name = "id", ) val id: Int,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "birth_year") val birth_year: Int
)


@Entity(tableName = "app_program_type")
data class AppProgramTypeEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "back_color") val back_color: String
)

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id", ) val id: Int,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "birth_year") val birth_year: Int

)

@Entity(tableName = "user_program")
data class UserProgramEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_id") val user_id: Int,
    @ColumnInfo(name = "app_program_type_id") val app_program_type_id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "use_timing") val use_timing: Int,
    @ColumnInfo(name = "icon") val icon: String
)

@Entity(tableName = "user_program_exercise")
data class  UserProgramExerciseEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_program_id") val user_program_id: Int,
    @ColumnInfo(name = "user_exercise_id") val user_exercise_id: Int
)

@Entity(tableName = "user_exercise")
data class UserExerciseEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_id") val user_id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "photo_url") val photo_url: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "infobox_color") val infobox_color: String
)

@Entity(tableName = "user_program_session")
data class UserProgramSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "user_program_id") val user_program_id: Int,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "startTime") val startTime: String,
    @ColumnInfo(name = "time_spent") val time_spent: Int
)

@Entity(tableName = "user_program_session_data")
data class  UserProgramSessionDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "user_program_session_id") val user_program_session_id: Int,
    @ColumnInfo(name = "floatData1") val floatData1: Float,
    @ColumnInfo(name = "floatData2") val floatData2: Float,
    @ColumnInfo(name = "floatData3") val floatData3: Float,
    @ColumnInfo(name = "textData1") val textData1: String
)

@Entity(tableName = "user_program_session_photo")
data class UserProgramSessionPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "user_program_session_id") val user_program_session_id: Int,
    @ColumnInfo(name = "photo_url") val photo_url: String
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "last_7_days_session_count") val last7DaysSessionCount: String,
    @ColumnInfo(name = "last_7_days_time_spent") val last7DaysTimeSpent: String,
    @ColumnInfo(name = "current_week_session_count") val currentWeekSessionCount: String,
    @ColumnInfo(name = "current_week_time_spent") val currentWeekTimeSpent: String,
    @ColumnInfo(name = "current_month_session_count") val currentMonthSessionCount: String,
    @ColumnInfo(name = "current_month_time_spent") val currentMonthTimeSpent: String,
    @ColumnInfo(name = "last_30_days_session_count") val last30DaysSessionCount: String,
    @ColumnInfo(name = "last_30_days_time_spent") val last30DaysTimeSpent: String,
    @ColumnInfo(name = "current_year_session_count") val currentYearSessionCount: String,
    @ColumnInfo(name = "current_year_time_spent") val currentYearTimeSpent: String
)

@Entity(tableName = "user_program_session_offline")
data class UserProgramSessionEntityOffline(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "user_program_id") val user_program_id: Int,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "startTime") val startTime: String,
    @ColumnInfo(name = "time_spent") val time_spent: Int
)

@Entity(tableName = "user_program_session_data_offline")
data class  UserProgramSessionDataEntityOffline(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "user_program_session_id") var user_program_session_id: Int,
    @ColumnInfo(name = "floatData1") val floatData1: Float,
    @ColumnInfo(name = "floatData2") val floatData2: Float,
    @ColumnInfo(name = "floatData3") val floatData3: Float,
    @ColumnInfo(name = "textData1") val textData1: String
)

@Entity(tableName = "user_program_session_photo_offline")
data class UserProgramSessionPhotoEntityOffline(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "user_program_session_id") val user_program_session_id: Int,
    @ColumnInfo(name = "photo_url") val photo_url: String
)
