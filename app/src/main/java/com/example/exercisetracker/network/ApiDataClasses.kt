package com.example.exercisetracker.network

import com.squareup.moshi.Json

data class AppProgramTypeJSON(
    @Json(name = "id") val id: Int=0,
    @Json(name = "description") val description: String="undefined",
    @Json(name = "icon") val icon: String="undefined",
    @Json(name = "back_color") val back_color: String="undefined",
)

data class UserJSON(
    @Json(name = "id") val id: Int=0,
    @Json(name = "phone") val phone: String="undefined",
    @Json(name = "email") val email: String="undefined",
    @Json(name = "name") val name: String="undefined",
    @Json(name = "birth_year") val birth_year: Int=-1,
)

data class UserProgramJSON(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "user_id") val user_id: Int = 0,
    @Json(name = "app_program_type_id") val app_program_type_id: Int = 0,
    @Json(name = "name") val name: String = "undefined",
    @Json(name = "description") val description: String = "undefined",
    @Json(name = "use_timing") val use_timing: Int = -1,
    @Json(name = "icon") val icon: String = "undefined"
)

data class UserProgramExerciseJSON(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "user_program_id") val user_program_id: Int = 0,
    @Json(name = "user_exercise_id") val user_exercise_id: Int = 0
)

data class UserExerciseJSON(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "user_id") val user_id: Int = 0,
    @Json(name = "name") val name: String = "undefined",
    @Json(name = "photo_url") val photo_url: String = "undefined",
    @Json(name = "description") val description: String = "undefined",
    @Json(name = "icon") val icon: String = "undefined",
    @Json(name = "infobox_color") val infobox_color: String = "undefined"
)

data class UserProgramSessionJSON(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "user_program_id") val user_program_id: Int = 0,
    @Json(name = "description") val description: String = "undefined",
    @Json(name = "startTime") val startTime: String = "undefined",
    @Json(name = "time_spent") val time_spent: Int = 0
)

data class UserProgramSessionDataJSON(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "user_program_session_id") val user_program_session_id: Int = 0,
    @Json(name = "floatData1") val floatData1: Float = 0f,
    @Json(name = "floatData2") val floatData2: Float = 0f,
    @Json(name = "floatData3") val floatData3: Float = 0f,
    @Json(name = "stringData1") val stringData1: String = "undefined"
)

data class UserProgramSessionPhotoJSON(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "user_program_session_id") val user_program_session_id: Int = 0,
    @Json(name = "photo_url") val photo_url: String = "undefined"
)
