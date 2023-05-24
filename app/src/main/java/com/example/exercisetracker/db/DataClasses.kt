package com.example.exercisetracker.db


data class ActiveUser(
    val id: Int,
    val phone: String,
    val email: String,
    val name: String,
    val birth_year: Int
)

data class AppProgramType(
    val id: Int,
    val description: String,
    val icon: String,
    val back_color: String
)

data class User(
    val id: Int,
    val phone: String,
    val email: String,
    val name: String,
    val birth_year: Int
)

data class UserProgram(
    val id: Int,
    val user_id: Int,
    val app_program_type_id: Int,
    val name: String,
    val description: String,
    val use_timing: Int,
    val icon: String
)

data class UserProgramExercise(
    val id: Int,
    val user_program_id: Int,
    val user_exercise_id: Int
)

data class UserExercise(
    val id: Int,
    val user_id: Int,
    val name: String,
    val photo_url: String,
    val description: String,
    val icon: String,
    val infobox_color: String
)

data class UserProgramSession(
    val id: Int,
    val user_program_id: Int,
    var description: String,
    val startTime: String,
    val time_spent: Int
)

data class UserProgramSessionData(
    val id: Int,
    var user_program_session_id: Int,
    val floatData1: Float,
    val floatData2: Float,
    val floatData3: Float,
    val textData1: String
)

data class UserProgramSessionPhoto(
    val id: Int,
    val user_program_session_id: Int,
    val photo_url: String
)

data class DisplayableSession(
    val sessionId: Int,
    val userProgramId: Int,
    val sessionDescription: String,
    val sessionStartTime: String,
    val sessionTimeSpent: Int,
    val userProgramName: String,
    val programTypeIcon: String
)
