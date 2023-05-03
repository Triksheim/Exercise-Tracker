package com.example.exercisetracker.network

import com.squareup.moshi.Json

data class AppProgramTypesJSON(
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
