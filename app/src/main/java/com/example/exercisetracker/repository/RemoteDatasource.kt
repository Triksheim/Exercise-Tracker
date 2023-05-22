package com.example.exercisetracker.repository



import com.example.exercisetracker.db.*
import com.example.exercisetracker.network.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.Result
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.*
import java.lang.Exception

enum class ApiStatus { LOADING, ERROR, DONE}

private const val BASE_URL = "https://wfa-media.com/exercise23/v3/api.php/"
private const val API_KEY = "004E06B1-E02"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(ApiKeyInterceptor())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

private class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val requestWithApiKey = originalRequest.newBuilder()
            .header("Authorization", API_KEY)
            .build()
        return chain.proceed(requestWithApiKey)
    }
}


interface ApiService {
    // User
    @GET("users")
    suspend fun getUsers(): List<UserJSON>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): UserJSON

    @POST("users")
    suspend fun createUser(@Body user: User): Response<UserJSON>


    // AppProgramType
    @GET("app_program_types")
    suspend fun getAppProgramTypes(): List<AppProgramTypeJSON>

    @GET("app_program_types/{id}")
    suspend fun getAppProgramType(@Path("id") id: Int): AppProgramTypeJSON

    @POST("app_program_types")
    suspend fun createAppProgramType(@Body appProgramType: AppProgramType): Response<AppProgramTypeJSON>


    // UserProgram
    @GET("user_programs")
    suspend fun getUserPrograms(@Query("user_id") userId: Int): List<UserProgramJSON>

    @GET("user_programs/{id}")
    suspend fun getUserProgram(@Path("id") id: Int): UserProgramJSON

    @POST("user_programs")
    suspend fun createUserProgram(@Body userProgram: UserProgram): Response<UserProgramJSON>

    @PUT("user_programs/{id}")
    suspend fun updateUserProgram(@Path("id") id: Int, @Body userProgram: UserProgram): Response<UserProgramJSON>

    @DELETE("user_programs/{id}")
    suspend fun deleteUserProgram(@Path("id") id: Int): Response<Unit>


    // UserProgramExercise
    @GET("user_program_exercises")
    suspend fun getUserProgramExercises(@Query("user_program_id")userProgramId: Int): List<UserProgramExerciseJSON>

    @GET("user_program_exercises/{id}")
    suspend fun getUserProgramExercise(@Path("id") id: Int): UserProgramExerciseJSON

    @POST("user_program_exercises")
    suspend fun createUserProgramExercise(@Body userProgramExercise: UserProgramExercise): Response<UserProgramExerciseJSON>

    @DELETE("user_program_exercises/{id}")
    suspend fun deleteUserProgramExercise(@Path("id") id: Int): Response<Unit>


    // UserExercise
    @GET("user_exercises")
    suspend fun getUserExercises(@Query("user_id")userId: Int): List<UserExerciseJSON>

    @GET("user_exercises/{id}")
    suspend fun getUserExercise(@Path("id") id: Int): UserExerciseJSON

    @POST("user_exercises")
    suspend fun createUserExercise(@Body userExercise: UserExercise): Response<UserExerciseJSON>

    @PUT("user_exercises/{id}")
    suspend fun updateUserExercise(@Path("id") id: Int, @Body userExercise: UserExercise): Response<UserExerciseJSON>

    @DELETE("user_exercises/{id}")
    suspend fun deleteUserExercise(@Path("id") id: Int): Response<Unit>


    // UserProgramSession
    @GET("user_program_sessions")
    suspend fun getUserProgramSessions(@Query("user_program_id") userProgramId: Int): List<UserProgramSessionJSON>

    @GET("user_program_sessions/{id}")
    suspend fun getUserProgramSession(@Path("id") id: Int): UserProgramSessionJSON

    @POST("user_program_sessions")
    suspend fun createUserProgramSession(@Body userProgramSession: UserProgramSession): Response<UserProgramSessionJSON>

    @PUT("user_program_sessions/{id}")
    suspend fun updateUserProgramSession(@Path("id") id: Int, @Body userProgramSession: UserProgramSession): Response<UserProgramSessionJSON>

    @DELETE("user_program_sessions/{id}")
    suspend fun deleteUserProgramSession(@Path("id") id: Int): Response<Unit>


    // UserProgramSessionData
    @GET("user_program_session_data")
    suspend fun getUserProgramSessionDataList(@Query("user_id")userId: Int): List<UserProgramSessionDataJSON>

    @GET("user_program_session_data/{id}")
    suspend fun getUserProgramSessionData(@Path("id") id: Int): UserProgramSessionDataJSON

    @POST("user_program_session_data")
    suspend fun createUserProgramSessionData(@Body userProgramSessionData: UserProgramSessionData): Response<UserProgramSessionDataJSON>

    @POST("user_program_session_data")
    suspend fun insertUserProgramSessionDataList(@Body userProgramSessionDataList: List<UserProgramSessionData>): Response<List<UserProgramSessionDataJSON>>

    @PUT("user_program_session_data/{id}")
    suspend fun updateUserProgramSessionData(@Path("id") id: Int, @Body userProgramSessionData: UserProgramSessionData): Response<UserProgramSessionDataJSON>

    @DELETE("user_program_session_data/{id}")
    suspend fun deleteUserProgramSessionData(@Path("id") id: Int): Response<Unit>


    // UserProgramSessionPhoto
    @GET("user_program_session_photos")
    suspend fun getUserProgramSessionPhotos(): List<UserProgramSessionPhotoJSON>

    @GET("user_program_session_photos/{id}")
    suspend fun getUserProgramSessionPhoto(@Path("id") id: Int): UserProgramSessionPhotoJSON

    @POST("user_program_session_photos")
    suspend fun createUserProgramSessionPhoto(@Body userProgramSessionPhoto: UserProgramSessionPhoto): Response<UserProgramSessionPhotoJSON>

    @PUT("user_program_session_photos/{id}")
    suspend fun updateUserProgramSessionPhoto(@Path("id") id: Int, @Body userProgramSessionPhoto: UserProgramSessionPhoto): Response<UserProgramSessionPhotoJSON>

    @DELETE("user_program_session_photos/{id}")
    suspend fun deleteUserProgramSessionPhoto(@Path("id") id: Int): Response<Unit>
}






class RemoteDataSource(private val apiService: ApiService) {

    private suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> {
        return try {
            Result.success(call())
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error ${e.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun <T> Response<T>.bodyOrThrow(): T {
        if (isSuccessful) {
            return body()!!
        } else {
            throw Exception("HTTP error ${code()}")
        }
    }




    // User
    suspend fun createUser(user: User) = safeApiCall { apiService.createUser(user).bodyOrThrow() }

    suspend fun getUsers() = safeApiCall { apiService.getUsers() }

    suspend fun getUser(id: Int) = safeApiCall { apiService.getUser(id) }

    // AppProgramType
    suspend fun getAppProgramTypes() = safeApiCall { apiService.getAppProgramTypes() }

    // UserProgram
    suspend fun createUserProgram(userProgram: UserProgram)
        = safeApiCall { apiService.createUserProgram(userProgram).bodyOrThrow() }
    suspend fun updateUserProgram(userProgram: UserProgram)
        = safeApiCall { apiService.updateUserProgram(userProgram.id, userProgram).bodyOrThrow() }
    suspend fun getUserPrograms(userId: Int)
        = safeApiCall { apiService.getUserPrograms(userId) }
    suspend fun getUserProgram(id: Int)
        = safeApiCall { apiService.getUserProgram(id) }
    suspend fun deleteUserProgram(id: Int)
        = safeApiCall { apiService.deleteUserProgram(id).bodyOrThrow() }


    // UserExercise
    suspend fun createUserExercise(userExercise: UserExercise)
        = safeApiCall { apiService.createUserExercise(userExercise).bodyOrThrow() }
    suspend fun getUserExercises(userId: Int)
        = safeApiCall { apiService.getUserExercises(userId) }
    suspend fun updateUserExercise(userExercise: UserExercise)
        = safeApiCall { apiService.updateUserExercise(userExercise.id, userExercise).bodyOrThrow() }
    suspend fun deleteUserExercise(id: Int)
        = safeApiCall { apiService.deleteUserExercise(id).bodyOrThrow() }


    // UserProgramExercise
    suspend fun createUserProgramExercise(userProgramExercise: UserProgramExercise)
        = safeApiCall { apiService.createUserProgramExercise(userProgramExercise).bodyOrThrow() }
    suspend fun getUserProgramExercises(userProgramId: Int)
        = safeApiCall { apiService.getUserProgramExercises(userProgramId) }
    suspend fun deleteUserProgramExercise(id: Int)
        = safeApiCall { apiService.deleteUserProgramExercise(id).bodyOrThrow() }


    // UserProgramSession
    suspend fun createUserProgramSession(userProgramSession: UserProgramSession)
        = safeApiCall { apiService.createUserProgramSession(userProgramSession).bodyOrThrow() }
    suspend fun getUserProgramSessions(userProgramId: Int)
        = safeApiCall { apiService.getUserProgramSessions(userProgramId) }
    suspend fun updateUserProgramSession(userProgramSession: UserProgramSession)
        = safeApiCall { apiService.updateUserProgramSession(userProgramSession.id, userProgramSession).bodyOrThrow() }
    suspend fun deleteUserProgramSession(id: Int)
        = safeApiCall { apiService.deleteUserProgramSession(id).bodyOrThrow() }


    // UserProgramSessionData
    suspend fun createUserProgramSessionData(userProgramSessionData: UserProgramSessionData)
        = safeApiCall { apiService.createUserProgramSessionData(userProgramSessionData).bodyOrThrow() }
    suspend fun insertUserProgramSessionDataList(userProgramSessionDataList: List<UserProgramSessionData>)
        = safeApiCall { apiService.insertUserProgramSessionDataList(userProgramSessionDataList).bodyOrThrow() }
    suspend fun getAllUserProgramSessionData(userId: Int)
        = safeApiCall { apiService.getUserProgramSessionDataList(userId) }
    suspend fun updateUserProgramSessionData(userProgramSessionData: UserProgramSessionData)
        = safeApiCall { apiService.updateUserProgramSessionData(userProgramSessionData.id, userProgramSessionData).bodyOrThrow() }
    suspend fun deleteUserProgramSessionData(id: Int)
        = safeApiCall { apiService.deleteUserProgramSessionData(id).bodyOrThrow() }


    // UserProgramSessionPhoto
    suspend fun createUserProgramSessionPhoto(userProgramSessionPhoto: UserProgramSessionPhoto)
        = safeApiCall { apiService.createUserProgramSessionPhoto(userProgramSessionPhoto).bodyOrThrow() }
    suspend fun updateUserProgramSessionPhoto(userProgramSessionPhoto: UserProgramSessionPhoto)
        = safeApiCall { apiService.updateUserProgramSessionPhoto(userProgramSessionPhoto.id, userProgramSessionPhoto).bodyOrThrow() }
    suspend fun deleteUserProgramSessionPhoto(id: Int)
        = safeApiCall { apiService.deleteUserProgramSessionPhoto(id).bodyOrThrow() }
}



val apiService = retrofit.create(ApiService::class.java)
val remoteDataSource = RemoteDataSource(apiService)
