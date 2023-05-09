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


    // UserProgramExercise
    @GET("user_program_exercises")
    suspend fun getUserProgramExercises(): List<UserProgramExerciseJSON>

    @GET("user_program_exercises/{id}")
    suspend fun getUserProgramExercise(@Path("id") id: Int): UserProgramExerciseJSON

    @POST("user_program_exercises")
    suspend fun createUserProgramExercise(@Body userProgramExercise: UserProgramExercise): Response<UserProgramExerciseJSON>


    // UserExercise
    @GET("user_exercises")
    suspend fun getUserExercises(@Query("user_id")userId: Int): List<UserExerciseJSON>

    @GET("user_exercises/{id}")
    suspend fun getUserExercise(@Path("id") id: Int): UserExerciseJSON

    @POST("user_exercises")
    suspend fun createUserExercise(@Body userExercise: UserExercise): Response<UserExerciseJSON>


    // UserProgramSession
    @GET("user_program_sessions")
    suspend fun getUserProgramSessions(@Query("user_program_id") userProgramId: Int): List<UserProgramSessionJSON>

    @GET("user_program_sessions/{id}")
    suspend fun getUserProgramSession(@Path("id") id: Int): UserProgramSessionJSON

    @POST("user_program_sessions")
    suspend fun createUserProgramSession(@Body userProgramSession: UserProgramSession): Response<UserProgramSessionJSON>


    // UserProgramSessionData
    @GET("user_program_session_data")
    suspend fun getUserProgramSessionDataList(): List<UserProgramSessionDataJSON>

    @GET("user_program_session_data/{id}")
    suspend fun getUserProgramSessionData(@Path("id") id: Int): UserProgramSessionDataJSON

    @POST("user_program_session_data")
    suspend fun createUserProgramSessionData(@Body userProgramSessionData: UserProgramSessionData): Response<UserProgramSessionDataJSON>


    // UserProgramSessionPhoto
    @GET("user_program_session_photos")
    suspend fun getUserProgramSessionPhotos(): List<UserProgramSessionPhotoJSON>

    @GET("user_program_session_photos/{id}")
    suspend fun getUserProgramSessionPhoto(@Path("id") id: Int): UserProgramSessionPhotoJSON

    @POST("user_program_session_photos")
    suspend fun createUserProgramSessionPhoto(@Body userProgramSessionPhoto: UserProgramSessionPhoto): Response<UserProgramSessionPhotoJSON>
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

    suspend fun getUserPrograms(userId: Int) = safeApiCall { apiService.getUserPrograms(userId) }

    suspend fun getUserProgram(id: Int) = safeApiCall { apiService.getUserProgram(id) }

    // UserExercise
    suspend fun createUserExercise(userExercise: UserExercise)
    = safeApiCall { apiService.createUserExercise(userExercise).bodyOrThrow() }
    suspend fun getUserExercises(userId: Int) = safeApiCall { apiService.getUserExercises(userId) }

    // UserProgramSession
    suspend fun getUserProgramSessions(userProgramId: Int)
    = safeApiCall { apiService.getUserProgramSessions(userProgramId) }
}


val apiService = retrofit.create(ApiService::class.java)
val remoteDataSource = RemoteDataSource(apiService)
