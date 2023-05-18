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
    fun getAppProgramTypesById(id: Int): Flow<AppProgramTypeEntity>

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

    @Query("SELECT * FROM user_program")
    fun getAllUserPrograms(): Flow<List<UserProgramEntity>>

    @Query("SELECT * FROM user_program WHERE id = :id")
    fun getUserProgramById(id: Int): Flow<UserProgramEntity>

    @Query("DELETE FROM user_program")
    suspend fun deleteAllUserPrograms()


    // UserProgramExercise
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserProgramExercise(userProgramExerciseEntity: UserProgramExerciseEntity): Long

    @Query("SELECT * FROM user_program_exercise")
    fun getAllUserProgramExercises(): Flow<List<UserProgramExerciseEntity>>

    @Query("SELECT * FROM user_program_exercise WHERE id = :id")
    fun getUserProgramExerciseById(id: Int): Flow<UserProgramExerciseEntity>

    @Query("DELETE FROM user_program_exercise")
    suspend fun deleteAllUserProgramExercises()


    // UserExercise
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserExercise(userExerciseEntity: UserExerciseEntity): Long

    @Query("SELECT * FROM user_exercise")
    fun getAllUserExercises(): Flow<List<UserExerciseEntity>>

    @Query("SELECT * FROM user_exercise WHERE id = :id")
    fun getUserExerciseById(id: Int): Flow<UserExerciseEntity>

    @Query("DELETE FROM user_exercise")
    suspend fun deleteAllUserExercises()


    // UserProgramSession
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProgramSession(userProgramSessionEntity: UserProgramSessionEntity): Long

    @Query("SELECT * FROM user_program_session")
    fun getAllUserProgramSessions(): Flow<List<UserProgramSessionEntity>>

    @Query("SELECT * FROM user_program_session WHERE id = :id")
    fun getUserProgramSessionById(id: Int): Flow<UserProgramSessionEntity>

    @Query("DELETE FROM user_program_session")
    suspend fun deleteAllUserProgramSessions()

    // UserProgramSessionData
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProgramSessionData(userProgramSessionDataEntity: UserProgramSessionDataEntity): Long

    @Query("SELECT * FROM user_program_session_data")
    fun getAllUserProgramSessionData(): Flow<List<UserProgramSessionDataEntity>>

    @Query("DELETE FROM user_program_session_data")
    suspend fun deleteAllUserProgramSessionData()

    @Query("SELECT * FROM user_program_session_data WHERE id = :id")
    fun getUserProgramSessionDataById(id: Int): Flow<UserProgramSessionDataEntity>





    // UserProgramSessionPhoto
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProgramSessionPhoto(userProgramSessionPhotoEntity: UserProgramSessionPhotoEntity): Long

    @Query("SELECT * FROM user_program_session_photo")
    fun getAllUserProgramSessionPhotos(): Flow<List<UserProgramSessionPhotoEntity>>

    @Query("SELECT * FROM user_program_session_photo WHERE id = :id")
    fun getUserProgramSessionPhotoById(id: Int): Flow<UserProgramSessionPhotoEntity>
}

