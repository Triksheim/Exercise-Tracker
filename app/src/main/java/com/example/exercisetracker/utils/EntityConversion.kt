package com.example.exercisetracker.utils

import com.example.exercisetracker.db.*
import com.example.exercisetracker.network.*

fun UserJSON.asEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        phone = this.phone,
        email = this.email,
        name = this.name,
        birth_year = this.birth_year
    )
}

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

fun UserStatsJSON.asEntity(): UserStatsEntity {
    return UserStatsEntity(
        userId = this.id,
        name = this.name,
        last7DaysSessionCount = this.last7Days.sessionCount,
        last7DaysTimeSpent = this.last7Days.timeSpent,
        currentWeekSessionCount = this.currentWeek.sessionCount,
        currentWeekTimeSpent = this.currentWeek.timeSpent,
        currentMonthSessionCount = this.currentMonth.sessionCount,
        currentMonthTimeSpent = this.currentMonth.timeSpent,
        last30DaysSessionCount = this.last30days.sessionCount,
        last30DaysTimeSpent = this.last30days.timeSpent,
        currentYearSessionCount = this.currentYear.sessionCount,
        currentYearTimeSpent = this.currentYear.timeSpent
    )
}

fun UserStatsEntity?.asDomainModel(): UserStats? {
    return this?.let {
        UserStats(
            userId = it.userId,
            name = it.name,
            last7Days = Stats(
                sessionCount = it.last7DaysSessionCount,
                timeSpent = it.last7DaysTimeSpent
            ),
            currentWeek = Stats(
                sessionCount = it.currentWeekSessionCount,
                timeSpent = it.currentWeekTimeSpent
            ),
            currentMonth = Stats(
                sessionCount = it.currentMonthSessionCount,
                timeSpent = it.currentMonthTimeSpent
            ),
            last30Days = Stats(
                sessionCount = it.last30DaysSessionCount,
                timeSpent = it.last30DaysTimeSpent
            ),
            currentYear = Stats(
                sessionCount = it.currentYearSessionCount,
                timeSpent = it.currentYearTimeSpent
            )
        )
    }
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

fun UserEntity.asDomainModel(): User {
    return User(
        id = this.id,
        phone = this.phone,
        email = this.email,
        name = this.name,
        birth_year = this.birth_year
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

fun User.asEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        phone = this.phone,
        email = this.email,
        name = this.name,
        birth_year = this.birth_year
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
