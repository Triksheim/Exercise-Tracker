package com.example.exercisetracker.db
import androidx.room.*
import kotlinx.coroutines.flow.Flow



@Dao
interface TrainingDao {

    // ActiveUser
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addActiveUser(activeUserEntity: ActiveUserEntity)

    @Query("SELECT * FROM active_user")
    suspend fun getActiveUser(): ActiveUserEntity

    @Query("DELETE FROM active_user")
    suspend fun removeActiveUser()



    // AppProgramType
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAppProgramTypes(appProgramTypes: AppProgramTypeEntity): Long

    @Query("SELECT * FROM app_program_type")
    fun getAppProgramTypes(): Flow<List<AppProgramTypeEntity>>


    @Query("SELECT * FROM app_program_type WHERE id = :id")
    fun getAppProgramTypeById(id: Int): Flow<AppProgramTypeEntity>

    @Query("DELETE FROM app_program_type")
    suspend fun deleteProgramTypes()


    // User
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(userEntity: UserEntity): Long

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE id = :id")
    fun getUserById(id: Int): Flow<UserEntity>

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()


    // UserProgram
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProgram(userProgramEntity: UserProgramEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateUserProgram(userProgramEntity: UserProgramEntity)

    @Query("SELECT * FROM user_program")
    fun getAllUserPrograms(): Flow<List<UserProgramEntity>>

    @Query("SELECT * FROM user_program WHERE id = :id")
    fun getUserProgramById(id: Int): Flow<UserProgramEntity>

    @Query("DELETE FROM user_program")
    suspend fun deleteAllUserPrograms()

    @Delete
    suspend fun deleteUserProgram(userProgramEntity: UserProgramEntity)


    // UserProgramExercise
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserProgramExercise(userProgramExerciseEntity: UserProgramExerciseEntity): Long

    @Query("SELECT * FROM user_program_exercise")
    fun getAllUserProgramExercises(): Flow<List<UserProgramExerciseEntity>>

    @Query("SELECT * FROM user_program_exercise WHERE user_program_id = :id")
    fun getUserProgramExerciseById(id: Int): Flow<List<UserProgramExerciseEntity>>

    @Query("SELECT user_exercise_id FROM user_program_exercise WHERE user_program_id = :id")
    suspend fun getUserExerciseIdsForProgramId(id: Int): List<Int>

    @Query("DELETE FROM user_program_exercise")
    suspend fun deleteAllUserProgramExercises()

    @Delete
    suspend fun deleteUserProgramExercise(userProgramExerciseEntity: UserProgramExerciseEntity)


    // UserExercise
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserExercise(userExerciseEntity: UserExerciseEntity): Long

    @Query("SELECT * FROM user_exercise")
    fun getAllUserExercises(): Flow<List<UserExerciseEntity>>

    @Query("SELECT * FROM user_exercise WHERE id = :id")
    fun getUserExerciseById(id: Int): UserExerciseEntity

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateUserExercise(userExerciseEntity: UserExerciseEntity)

    @Query("DELETE FROM user_exercise")
    suspend fun deleteAllUserExercises()

    @Delete
    suspend fun deleteUserExercise(userExerciseEntity: UserExerciseEntity)


    // UserProgramSession
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity): Long

    @Query("SELECT * FROM user_program_session")
    fun getAllUserProgramSessions(): Flow<List<UserProgramSessionEntity>>

    @Query("SELECT * FROM user_program_session WHERE id = :id")
    fun getUserProgramSessionById(id: Int): Flow<UserProgramSessionEntity>

    @Query("SELECT * FROM user_program_session WHERE user_program_id = :id")
    fun getSessionsForProgramId(id: Int): List<UserProgramSessionEntity>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity)

    @Query("DELETE FROM user_program_session")
    suspend fun deleteAllUserProgramSessions()

    @Delete
    suspend fun deleteUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity)


    // UserProgramSessionData
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity): Long

    @Query("SELECT * FROM user_program_session_data")
    fun getAllUserProgramSessionData(): Flow<List<UserProgramSessionDataEntity>>

    @Query("DELETE FROM user_program_session_data")
    suspend fun deleteAllUserProgramSessionData()

    @Query("SELECT * FROM user_program_session_data WHERE id = :id")
    fun getUserProgramSessionDataById(id: Int): Flow<UserProgramSessionDataEntity>

    @Query("SELECT * FROM user_program_session_data WHERE user_program_session_id = :id")
    fun getDataForSessionId(id: Int): List<UserProgramSessionDataEntity>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity)

    @Delete
    suspend fun deleteUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity)


    // UserProgramSessionPhoto
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity): Long

    @Query("SELECT * FROM user_program_session_photo")
    fun getAllUserProgramSessionPhotos(): Flow<List<UserProgramSessionPhotoEntity>>

    @Query("SELECT * FROM user_program_session_photo WHERE id = :id")
    fun getUserProgramSessionPhotoById(id: Int): Flow<UserProgramSessionPhotoEntity>

    @Query("SELECT * FROM user_program_session_photo WHERE user_program_session_id = :id")
    fun getPhotosForSessionId(id: Int): List<UserProgramSessionPhotoEntity>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity)

    @Delete
    suspend fun deleteUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity)


    // UserStats
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(userStatsEntity: UserStatsEntity): Long

    @Query("SELECT * FROM user_stats")
    fun getUserStats(): Flow<UserStatsEntity>

    @Query("DELETE FROM user_stats")
    suspend fun deleteUserStats()
}

