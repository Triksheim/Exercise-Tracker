package com.example.exercisetracker.db
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "active_user")
data class ActiveUser(
    @PrimaryKey()
    @ColumnInfo(name = "id", ) val id: Int,
    @ColumnInfo(name = "phone") val phone: String,
)


@Entity(tableName = "app_program_types")
data class AppProgramTypes(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "back_color") val back_color: String
)

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id", ) val id: Int,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "birth_year") val birth_year: Int

)

@Entity(tableName = "user_program")
data class UserProgram(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_id") val user_id: Int,
    @ColumnInfo(name = "app_program_type_id") val app_program_type_id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "use_timing") val use_timing: Int,
    @ColumnInfo(name = "icon") val icon: String
)

@Entity(tableName = "user_program_exercise")
data class  UserProgramExercise(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_program_id") val user_program_id: Int,
    @ColumnInfo(name = "user_exercise_id") val user_exercise_id: Int
)

@Entity(tableName = "user_exercise")
data class UserExercise(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_id") val user_id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "photo_url") val photo_url: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "infobox_color") val infobox_color: String
)

@Entity(tableName = "user_program_session")
data class UserProgramSession(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_program_id") val user_program_id: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "startTime") val startTime: String,
    @ColumnInfo(name = "time_spent") val time_spent: Int
)

@Entity(tableName = "user_program_session_data")
data class  UserProgramSessionData(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_program_session_id") val user_program_session_id: Int,
    @ColumnInfo(name = "floatData1") val floatData1: Float,
    @ColumnInfo(name = "floatData2") val floatData2: Float,
    @ColumnInfo(name = "floatData3") val floatData3: Float,
    @ColumnInfo(name = "stringData1") val stringData1: String
)

@Entity(tableName = "user_program_session_photo")
data class UserProgramSessionPhoto(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_program_session_id") val user_program_session_id: Int,
    @ColumnInfo(name = "photo_url") val photo_url: String
)

data class AppProgramType(
    val id: Int,
    val description: String,
    val icon: String,
    val back_color: String
)

data class Exercise(
    val id: Int,
    val user_id: Int,
    val name: String,
    val photo_url: String,
    val description: String,
    val icon: String,
    val infobox_color: String)

data class Program(
    val id: Int,
    val user_id: Int,
    val app_program_type_id: Int,
    val name: String,
    val description: String,
    val use_timing: Int,
    val icon: String)