package com.example.exercisetracker.utils

import com.example.exercisetracker.db.*
import com.example.exercisetracker.network.*


fun AppProgramTypeJSON.asEntity(): AppProgramTypeEntity {
    return AppProgramTypeEntity(
        id = this.id,
        description = this.description,
        icon = this.icon,
        back_color = this.back_color
    )
}

fun UserProgramJSON.asEntity(): UserProgramEntity {
    return UserProgramEntity(
        id = this.id,
        user_id = this.user_id,
        app_program_type_id = this.app_program_type_id,
        name = this.name,
        description = this.description,
        use_timing = this.use_timing,
        icon = this.icon
    )
}

fun UserProgramExerciseJSON.asEntity(): UserProgramExerciseEntity {
    return UserProgramExerciseEntity(
        id = this.id,
        user_program_id = this.user_program_id,
        user_exercise_id = this.user_exercise_id
    )
}

fun UserExerciseJSON.asEntity(): UserExerciseEntity {
    return UserExerciseEntity(
        id = this.id,
        user_id = this.user_id,
        name = this.name,
        photo_url = this.photo_url,
        description = this.description,
        icon = this.icon,
        infobox_color = this.infobox_color
    )
}

fun UserProgramSessionJSON.asEntity(): UserProgramSessionEntity {
    return UserProgramSessionEntity(
        id = this.id,
        user_program_id = this.user_program_id,
        description = this.description,
        startTime = this.startTime,
        time_spent = this.time_spent
    )
}

fun UserProgramSessionDataJSON.asEntity(): UserProgramSessionDataEntity {
    return UserProgramSessionDataEntity(
        id = this.id,
        user_program_session_id = this.user_program_session_id,
        floatData1 = this.floatData1,
        floatData2 = this.floatData2,
        floatData3 = this.floatData3,
        textData1 = this.textData1
    )
}

fun UserProgramSessionPhotoJSON.asEntity(): UserProgramSessionPhotoEntity {
    return UserProgramSessionPhotoEntity(
        id = this.id,
        user_program_session_id = this.user_program_session_id,
        photo_url = this.photo_url
    )
}

fun UserStatsJSON?.asEntity(): UserStatsEntity {
    return UserStatsEntity(
        userId = this?.id ?: "",
        name = this?.name ?: "",
        last7DaysSessionCount = this?.last7Days?.sessionCount ?: "0",
        last7DaysTimeSpent = this?.last7Days?.timeSpent ?: "0",
        currentWeekSessionCount = this?.currentWeek?.sessionCount ?: "0",
        currentWeekTimeSpent = this?.currentWeek?.timeSpent ?: "0",
        currentMonthSessionCount = this?.currentMonth?.sessionCount ?: "0",
        currentMonthTimeSpent = this?.currentMonth?.timeSpent ?: "0",
        last30DaysSessionCount = this?.last30days?.sessionCount ?: "0",
        last30DaysTimeSpent = this?.last30days?.timeSpent ?: "0",
        currentYearSessionCount = this?.currentYear?.sessionCount ?: "0",
        currentYearTimeSpent = this?.currentYear?.timeSpent ?: "0"
    )
}


fun UserStatsEntity?.asDomainModel(): UserStats {
    return UserStats(
        userId = this?.userId ?: "",
        name = this?.name ?: "",
        last7Days = Stats(
            sessionCount = this?.last7DaysSessionCount ?: "0",
            timeSpent = this?.last7DaysTimeSpent ?: "0"
        ),
        currentWeek = Stats(
            sessionCount = this?.currentWeekSessionCount ?: "0",
            timeSpent = this?.currentWeekTimeSpent ?: "0"
        ),
        currentMonth = Stats(
            sessionCount = this?.currentMonthSessionCount ?: "0",
            timeSpent = this?.currentMonthTimeSpent ?: "0"
        ),
        last30Days = Stats(
            sessionCount = this?.last30DaysSessionCount ?: "0",
            timeSpent = this?.last30DaysTimeSpent ?: "0"
        ),
        currentYear = Stats(
            sessionCount = this?.currentYearSessionCount ?: "0",
            timeSpent = this?.currentYearTimeSpent ?: "0"
        )
    )
}


fun UserJSON.asDomainModel(): User {
    return User(
        id = this.id,
        phone = this.phone,
        email = this.email,
        name = this.name,
        birth_year = this.birth_year
    )
}



fun ActiveUserEntity.asDomainModel(): ActiveUser {
    return ActiveUser(
        id = this.id,
        phone = this.phone,
        email = this.email,
        name = this.name,
        birth_year = this.birth_year
    )
}

fun AppProgramTypeEntity.asDomainModel(): AppProgramType {
    return AppProgramType(
        id = this.id,
        description = this.description,
        icon = this.icon,
        back_color = this.back_color
    )
}


fun UserProgramEntity.asDomainModel(): UserProgram {
    return UserProgram(
        id = this.id,
        user_id = this.user_id,
        app_program_type_id = this.app_program_type_id,
        name = this.name,
        description = this.description,
        use_timing = this.use_timing,
        icon = this.icon
    )
}

fun UserProgramExerciseEntity.asDomainModel(): UserProgramExercise {
    return UserProgramExercise(
        id = this.id,
        user_program_id = this.user_program_id,
        user_exercise_id = this.user_exercise_id
    )
}

fun UserExerciseEntity.asDomainModel(): UserExercise {
    return UserExercise(
        id = this.id,
        user_id = this.user_id,
        name = this.name,
        photo_url = this.photo_url,
        description = this.description,
        icon = this.icon,
        infobox_color = this.infobox_color
    )
}

fun UserProgramSessionEntity.asDomainModel(): UserProgramSession {
    return UserProgramSession(
        id = this.id,
        user_program_id = this.user_program_id,
        description = this.description,
        startTime = this.startTime,
        time_spent = this.time_spent
    )
}

fun UserProgramSessionDataEntity.asDomainModel(): UserProgramSessionData {
    return UserProgramSessionData(
        id = this.id,
        user_program_session_id = this.user_program_session_id,
        floatData1 = this.floatData1,
        floatData2 = this.floatData2,
        floatData3 = this.floatData3,
        textData1 = this.textData1
    )
}

fun UserProgramSessionPhotoEntity.asDomainModel(): UserProgramSessionPhoto {
    return UserProgramSessionPhoto(
        id = this.id,
        user_program_session_id = this.user_program_session_id,
        photo_url = this.photo_url
    )
}


fun UserProgram.asEntity(): UserProgramEntity {
    return UserProgramEntity(
        id = this.id,
        user_id = this.user_id,
        app_program_type_id = this.app_program_type_id,
        name = this.name,
        description = this.description,
        use_timing = this.use_timing,
        icon = this.icon
    )
}

fun UserProgramExercise.asEntity(): UserProgramExerciseEntity {
    return UserProgramExerciseEntity(
        id = this.id,
        user_program_id = this.user_program_id,
        user_exercise_id = this.user_exercise_id
    )
}

fun UserExercise.asEntity(): UserExerciseEntity {
    return UserExerciseEntity(
        id = this.id,
        user_id = this.user_id,
        name = this.name,
        photo_url = this.photo_url,
        description = this.description,
        icon = this.icon,
        infobox_color = this.infobox_color
    )
}

fun UserProgramSession.asEntity(): UserProgramSessionEntity {
    return UserProgramSessionEntity(
        id = this.id,
        user_program_id = this.user_program_id,
        description = this.description,
        startTime = this.startTime,
        time_spent = this.time_spent
    )
}

fun UserProgramSessionData.asEntity(): UserProgramSessionDataEntity {
    return UserProgramSessionDataEntity(
        id = this.id,
        user_program_session_id = this.user_program_session_id,
        floatData1 = this.floatData1,
        floatData2 = this.floatData2,
        floatData3 = this.floatData3,
        textData1 = this.textData1
    )
}

fun UserProgramSessionPhoto.asEntity(): UserProgramSessionPhotoEntity {
    return UserProgramSessionPhotoEntity(
        id = this.id,
        user_program_session_id = this.user_program_session_id,
        photo_url = this.photo_url
    )
}

fun UserProgramSession.asOfflineEntity(): UserProgramSessionEntityOffline {
    return UserProgramSessionEntityOffline(
        id = this.id,
        user_program_id = this.user_program_id,
        description = this.description,
        startTime = this.startTime,
        time_spent = this.time_spent
    )
}

fun UserProgramSessionData.asOfflineEntity(): UserProgramSessionDataEntityOffline {
    return UserProgramSessionDataEntityOffline(
        id = this.id,
        user_program_session_id = this.user_program_session_id,
        floatData1 = this.floatData1,
        floatData2 = this.floatData2,
        floatData3 = this.floatData3,
        textData1 = this.textData1
    )
}

fun UserProgramSessionPhoto.asOfflineEntity(): UserProgramSessionPhotoEntityOffline {
    return UserProgramSessionPhotoEntityOffline(
        id = this.id,
        user_program_session_id = this.user_program_session_id,
        photo_url = this.photo_url
    )
}

fun UserProgramSessionEntityOffline.asDomainModel(): UserProgramSession {
    return UserProgramSession(
        id = this.id,
        user_program_id = this.user_program_id,
        description = this.description,
        startTime = this.startTime,
        time_spent = this.time_spent
    )
}

fun UserProgramSessionDataEntityOffline.asDomainModel(): UserProgramSessionData {
    return UserProgramSessionData(
        id = this.id,
        user_program_session_id = this.user_program_session_id,
        floatData1 = this.floatData1,
        floatData2 = this.floatData2,
        floatData3 = this.floatData3,
        textData1 = this.textData1
    )
}

fun UserProgramSessionPhotoEntityOffline.asDomainModel(): UserProgramSessionPhoto {
    return UserProgramSessionPhoto(
        id = this.id,
        user_program_session_id = this.user_program_session_id,
        photo_url = this.photo_url
    )
}
