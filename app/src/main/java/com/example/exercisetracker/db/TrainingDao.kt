package com.example.exercisetracker.db
import androidx.room.*
import kotlinx.coroutines.flow.Flow



@Dao
interface TrainingDao {

    // ActiveUser
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addActiveUser(activeUser: ActiveUser)

    @Query("SELECT * FROM active_user")
    suspend fun getActiveUser(): ActiveUser

    @Query("DELETE FROM active_user")
    suspend fun removeActiveUser()



    // AppProgramType
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAppProgramTypes(appProgramTypes: AppProgramType): Long

    @Query("SELECT * FROM app_program_type")
    fun getAppProgramTypes(): Flow<List<AppProgramType>>


    @Query("SELECT * FROM app_program_type WHERE id = :id")
    fun getAppProgramTypesById(id: Int): Flow<AppProgramType>

    @Query("DELETE FROM app_program_type")
    suspend fun deleteProgramTypes()


    // User
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM user WHERE id = :id")
    fun getUserById(id: Int): Flow<User>

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()


    // UserProgram
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProgram(userProgram: UserProgram): Long

    @Query("SELECT * FROM user_program")
    fun getAllUserPrograms(): Flow<List<UserProgram>>

    @Query("SELECT * FROM user_program WHERE id = :id")
    fun getUserProgramById(id: Int): Flow<UserProgram>

    @Query("DELETE FROM user_program")
    suspend fun deleteAllUserPrograms()

    // UserProgramExercise
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserProgramExercise(userProgramExercise: UserProgramExercise): Long

    @Query("SELECT * FROM user_program_exercise")
    fun getAllUserProgramExercises(): Flow<List<UserProgramExercise>>

    @Query("SELECT * FROM user_program_exercise WHERE id = :id")
    fun getUserProgramExerciseById(id: Int): Flow<UserProgramExercise>




    // UserExercise
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserExercise(userExercise: UserExercise): Long

    @Query("SELECT * FROM user_exercise")
    fun getAllUserExercises(): Flow<List<UserExercise>>

    @Query("SELECT * FROM user_exercise WHERE id = :id")
    fun getUserExerciseById(id: Int): Flow<UserExercise>




    // UserProgramSession
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserProgramSession(userProgramSession: UserProgramSession): Long

    @Query("SELECT * FROM user_program_session")
    fun getAllUserProgramSessions(): Flow<List<UserProgramSession>>

    @Query("SELECT * FROM user_program_session WHERE id = :id")
    fun getUserProgramSessionById(id: Int): Flow<UserProgramSession>

    // UserProgramSessionData
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserProgramSessionData(userProgramSessionData: UserProgramSessionData): Long

    @Query("SELECT * FROM user_program_session_data")
    fun getAllUserProgramSessionData(): Flow<List<UserProgramSessionData>>

    @Query("SELECT * FROM user_program_session_data WHERE id = :id")
    fun getUserProgramSessionDataById(id: Int): Flow<UserProgramSessionData>





    // UserProgramSessionPhoto
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserProgramSessionPhoto(userProgramSessionPhoto: UserProgramSessionPhoto): Long

    @Query("SELECT * FROM user_program_session_photo")
    fun getAllUserProgramSessionPhotos(): Flow<List<UserProgramSessionPhoto>>

    @Query("SELECT * FROM user_program_session_photo WHERE id = :id")
    fun getUserProgramSessionPhotoById(id: Int): Flow<UserProgramSessionPhoto>
}

